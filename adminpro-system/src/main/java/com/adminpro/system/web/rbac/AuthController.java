package com.adminpro.system.web.rbac;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.util.DateUtil;
import com.adminpro.framework.client.helper.ClientHelper;
import com.adminpro.framework.exceptions.APIException;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.constants.ConfigKeys;
import com.adminpro.system.core.common.constants.WebConstants;
import com.adminpro.system.core.common.helper.ConfigHelper;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.rbac.api.Device;
import com.adminpro.system.rbac.api.LoginHelper;
import com.adminpro.system.rbac.api.PasswordValidator;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import com.adminpro.system.rbac.common.RbacConstants;
import com.adminpro.system.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.system.rbac.domains.entity.dept.DeptService;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;

import com.adminpro.system.rbac.domains.entity.user.UserService;
import com.adminpro.system.rbac.domains.vo.login.LoginResponse;
import com.adminpro.system.rbac.domains.vo.login.LoginUserVo;
import com.adminpro.system.rbac.domains.vo.user.PasswordRuleVo;
import com.adminpro.system.rbac.domains.vo.user.UpdateProfileVo;
import com.adminpro.system.rbac.domains.vo.user.UserInfoResponseVo;
import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

/**
 * 认证控制器
 * <p>
 * 提供用户登录、登出、获取用户信息、验证码等认证相关的API接口
 * </p>
 *
 * @author system
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    /**
     * 用户登录
     * <p>
     * 处理用户登录请求，支持用户名/邮箱/手机号登录，验证码验证
     * </p>
     *
     * @param request     HTTP请求对象，用于检测设备类型
     * @param loginUserVo 登录请求参数，包含用户名、密码、域、验证码等信息
     * @return 登录结果，包含token、用户基本信息、权限信息等
     */
    @SysLog("用户登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public R<LoginResponse> login(HttpServletRequest request, @RequestBody LoginUserVo loginUserVo) {
        BeanUtil.beanAttributeValueTrim(loginUserVo);
        String loginName = loginUserVo.getUserId();
        String password = loginUserVo.getPassword();
        String userDomain = loginUserVo.getDomain();
        String captcha = loginUserVo.getCaptcha();
        LoginResponse loginResponse = new LoginResponse();
        try {
            if (StringUtils.isEmpty(loginName)) {
                return R.error("601", "用户名不能为空");
            }

            if (StringUtils.isEmpty(password)) {
                return R.error("601", "密码不能为空");
            }
            if (StringUtils.isEmpty(userDomain)) {
                return R.error("601", "非法User Domain");
            }

            // spring-mobile-device 已停止维护，使用简单的设备检测
            Device currentDevice = createDeviceFromRequest(request);

            boolean isMobileApp = ClientHelper.isMobileAppRequest(request);
            boolean isMiniProgram = ClientHelper.isMiniProgramRequest(request);
            if (!isMobileApp && !isMiniProgram && !WebHelper.isDevModel()
                    && LoginHelper.getInstance().needCheckCapture(userDomain)) {
                if (StringUtils.isEmpty(captcha)) {
                    return R.error("验证码不正确");
                }
                boolean b = LoginHelper.getInstance().validCaptcha(captcha);
                if (!b) {
                    return R.error("验证码不正确");
                }
            }

            UserEntity userEntity = userService.findByUserDomainAndLoginName(userDomain, loginName);
            if (userEntity == null) {
                return R.error("用户名未注册");
            }
            String token = LoginHelper.getInstance().login(
                    userEntity.getUserDomain(), userEntity.getLoginName(), password, currentDevice);
            if ("pending_active".equals(token)) {
                return R.error("601", WebConstants.USER_PENDING_ACTIVE);
            } else if (RbacConstants.LOGIN_RESULT_USER_LOCKED.equals(token)) {
                return R.error("601", WebConstants.USER_LOCKED);
            } else if (RbacConstants.LOGIN_RESULT_USER_INACTIVE.equals(token)) {
                return R.error("601", WebConstants.USER_HAS_NO_PRIVILEGE);
            } else if (RbacConstants.LOGIN_RESULT_NO_MATCH.equals(token)) {
                return R.error("601", WebConstants.USER_NOT_MATCHED);
            }

            loginResponse.setId(userEntity.getId());
            loginResponse.setUserId(loginName);
            loginResponse.setToken(token);
            loginResponse.setHasPayPwd(!StringUtils.isEmpty(userEntity.getPayPwd()));
            loginResponse.setAuthed(userEntity.isAuthenticated());
            loginResponse.setIdNo(userEntity.getIdNo());
            loginResponse.setRealName(userEntity.getRealName());
            loginResponse.setDomain(userDomain);
            loginResponse.setDisplay(userEntity.getDisplay());
            loginResponse.setMobileNo(userEntity.getMobileNo());
            loginResponse.setDate(DateUtil.formatDate(new Date()));
            if (StringUtils.isEmpty(userEntity.getAvatarUrl())) {
                String avatarUrl = ConfigHelper.getString(ConfigKeys.User.AVATAR_URL);
                loginResponse.setAvatarUrl(WebConstants.getServerAddress() + avatarUrl);
            } else {
                loginResponse.setAvatarUrl(userEntity.getAvatarUrl());
            }
            loginResponse.setExtUserId(userEntity.getExtUserId());
            if (LoginHelper.getInstance().isIntranetUser(userDomain)) {
                loginResponse.setPost(userEntity.getPost());
                loginResponse.setPostNo(userEntity.getJobNo());
            }
            String deptNo = userEntity.getDeptNo();
            if (StringUtils.isNotEmpty(deptNo)) {
                DeptEntity deptEntity = deptService.findByNo(deptNo);
                if (deptEntity != null) {
                    loginResponse.setDeptName(deptEntity.getName());
                    loginResponse.setDeptNo(deptEntity.getNo());
                }
            }
            return R.ok(loginResponse);
        } catch (Exception e) {
            logger.error("登陆异常：", e);
            if (e instanceof AuthenticationException) {
                return R.error("601", "账号或密码错误");
            } else if (e instanceof BadCredentialsException) {
                return R.error("601", e.getMessage());
            } else if (e instanceof UsernameNotFoundException) {
                return R.error("601", e.getMessage());
            } else if (e instanceof APIException) {
                return R.error("601", e.getMessage());
            } else {
                return R.error(e.getMessage());
            }
        }
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 根据当前登录token获取用户的详细信息，包括基本资料、部门信息等
     * </p>
     *
     * @return 用户详细信息
     */
    @PreAuthorize("@ss.hasPermission('system:common')")
    @RequestMapping(value = "/userinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<UserInfoResponseVo> getUserInfo() {
        try {
            LoginHelper loginHelper = LoginHelper.getInstance();
            LoginUser loginUser = loginHelper.getLoginUser();
            if (loginUser == null) {
                return R.error("未登录");
            }
            UserEntity userEntity = userService.findByUserDomainAndLoginName(
                    loginUser.getUserDomain(), loginUser.getLoginName());
            if (userEntity == null) {
                return R.error("用户不存在");
            }

            // 构建响应VO
            UserInfoResponseVo userInfoVo = new UserInfoResponseVo();
            userInfoVo.setUserId(userEntity.getId());
            userInfoVo.setUserDomain(userEntity.getUserDomain());
            userInfoVo.setLoginName(userEntity.getLoginName());
            userInfoVo.setRealName(userEntity.getRealName());
            userInfoVo.setMobileNo(userEntity.getMobileNo());
            userInfoVo.setEmail(userEntity.getEmail());
            userInfoVo.setAvatarUrl(userEntity.getAvatarUrl());
            userInfoVo.setSex(userEntity.getSex());
            userInfoVo.setStatus(userEntity.getStatus());
            userInfoVo.setDeptNo(userEntity.getDeptNo());
            userInfoVo.setLatestLoginTime(userEntity.getLatestLoginTime());
            userInfoVo.setDescription(userEntity.getDescription());
            // 根据部门编号查询部门名称
            String deptNo = userEntity.getDeptNo();
            if (StringUtils.isNotEmpty(deptNo)) {
                DeptEntity deptEntity = deptService.findByNo(deptNo);
                if (deptEntity != null) {
                    userInfoVo.setDeptName(deptEntity.getName());
                }
            }

            return R.ok(userInfoVo);
        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
            return R.error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取密码规则
     * <p>
     * 获取系统当前配置的密码强度规则，包括长度、复杂度等要求
     * </p>
     *
     * @return 密码规则配置信息
     */
    @PreAuthorize("@ss.hasPermission('system:common')")
    @RequestMapping(value = "/password-rule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<PasswordRuleVo> getPasswordRule() {
        try {
            PasswordRuleVo rule = PasswordValidator.getPasswordRule();
            return R.ok(rule);
        } catch (Exception e) {
            logger.error("获取密码规则失败", e);
            return R.error("获取密码规则失败: " + e.getMessage());
        }
    }

    /**
     * 更新个人资料
     * <p>
     * 允许当前登录用户更新自己的基本资料信息
     * </p>
     *
     * @param updateProfileVo 个人资料更新请求参数
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermission('system:common')")
    @SysLog("更新个人资料")
    @RequestMapping(value = "/profile", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public R updateProfile(@RequestBody UpdateProfileVo updateProfileVo) {
        try {
            LoginHelper loginHelper = LoginHelper.getInstance();
            LoginUser loginUser = loginHelper.getLoginUser();
            if (loginUser == null) {
                return R.error("未登录");
            }
            UserEntity userEntity = userService.findByUserDomainAndLoginName(
                    loginUser.getUserDomain(), loginUser.getLoginName());
            if (userEntity == null) {
                return R.error("用户不存在");
            }

            BeanUtil.beanAttributeValueTrim(updateProfileVo);
            if (StringUtils.isNotEmpty(updateProfileVo.getRealName())) {
                userEntity.setRealName(updateProfileVo.getRealName());
            }
            if (StringUtils.isNotEmpty(updateProfileVo.getMobileNo())) {
                userEntity.setMobileNo(updateProfileVo.getMobileNo());
            }
            if (StringUtils.isNotEmpty(updateProfileVo.getEmail())) {
                userEntity.setEmail(updateProfileVo.getEmail());
            }
            if (StringUtils.isNotEmpty(updateProfileVo.getAvatarUrl())) {
                userEntity.setAvatarUrl(updateProfileVo.getAvatarUrl());
            }
            if (StringUtils.isNotEmpty(updateProfileVo.getSex())) {
                userEntity.setSex(updateProfileVo.getSex());
            }
            if (StringUtils.isNotEmpty(updateProfileVo.getDescription())) {
                userEntity.setDescription(updateProfileVo.getDescription());
            }

            userService.update(userEntity);
            return R.ok("个人资料更新成功");
        } catch (Exception e) {
            logger.error("更新个人资料失败", e);
            return R.error("更新个人资料失败: " + e.getMessage());
        }
    }

    /**
     * 生成验证码图片
     * <p>
     * 生成数学运算类型的验证码图片，用于登录安全验证
     * </p>
     *
     * @param response HTTP响应对象，用于输出验证码图片
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    @RequestMapping(value = "/captcha.jpg", method = RequestMethod.GET)
    public void captcha(HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        String capStr = null;
        String code = null;
        BufferedImage bi = null;

        String capText = captchaProducerMath.createText();
        capStr = capText.substring(0, capText.lastIndexOf("@"));
        code = capText.substring(capText.lastIndexOf("@") + 1);
        bi = captchaProducerMath.createImage(capStr);

        logger.debug("生成验证码：" + code);

        // 获取或创建 session，确保验证码存储在其中
        HttpSession session = request.getSession();
        // 如果 session 不存在或无效，创建新 session
        if (session == null) {
            session = request.getSession(true);
        }
        logger.debug("session id:" + session.getId());

        session.setAttribute(RbacCacheConstants.CAPTCHA_CACHE, code);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    /**
     * 根据请求创建设备信息
     * <p>
     * 从User-Agent中解析设备类型（PC、手机、平板）
     * </p>
     *
     * @param request HTTP请求对象
     * @return 设备信息对象
     */
    private Device createDeviceFromRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return new SimpleDevice(true, false, false);
        }
        String ua = userAgent.toLowerCase();
        boolean isMobile = ua.contains("mobile") || ua.contains("android") || ua.contains("iphone");
        boolean isTablet = ua.contains("tablet") || ua.contains("ipad");
        return new SimpleDevice(!isMobile && !isTablet, isMobile && !isTablet, isTablet);
    }

    private static class SimpleDevice implements Device {
        private final boolean normal;
        private final boolean mobile;
        private final boolean tablet;

        SimpleDevice(boolean normal, boolean mobile, boolean tablet) {
            this.normal = normal;
            this.mobile = mobile;
            this.tablet = tablet;
        }

        @Override
        public boolean isNormal() {
            return normal;
        }

        @Override
        public boolean isMobile() {
            return mobile;
        }

        @Override
        public boolean isTablet() {
            return tablet;
        }
    }
}
