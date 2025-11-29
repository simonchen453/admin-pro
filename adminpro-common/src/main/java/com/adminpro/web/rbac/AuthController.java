package com.adminpro.web.rbac;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.exceptions.APIException;
import com.adminpro.framework.common.constants.WebConstants;
import com.adminpro.framework.common.helper.ConfigHelper;

import static com.adminpro.framework.common.constants.ConfigKeys.User;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.framework.common.web.BaseController;
import com.adminpro.framework.common.annotation.SysLog;
import com.adminpro.rbac.api.Device;
import com.adminpro.rbac.api.LoginHelper;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.rbac.domains.entity.dept.DeptService;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.rbac.domains.entity.user.UserService;
import com.adminpro.rbac.domains.vo.login.LoginResponse;
import com.adminpro.rbac.domains.vo.login.LoginUserVo;
import com.adminpro.rbac.domains.vo.user.UpdateProfileVo;
import com.adminpro.rbac.enums.UserLoginPlatform;
import com.google.code.kaptcha.Producer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.javasimon.aop.Monitored;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import javax.imageio.ImageIO;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/rest/auth")
@Monitored
public class AuthController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @SysLog("用户登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public R<LoginResponse> login(HttpServletRequest request, @RequestBody LoginUserVo loginUserVo) {
        BeanUtil.beanAttributeValueTrim(loginUserVo);
        String loginName = loginUserVo.getUserId();
        String password = loginUserVo.getPassword();
        String platform = loginUserVo.getPlatform();
        String captcha = loginUserVo.getCaptcha();
        LoginResponse loginResponse = new LoginResponse();
        try {
            if (StringUtils.isEmpty(loginName)) {
                return R.error("601", "用户名不能为空");
            }

            if (StringUtils.isEmpty(password)) {
                return R.error("601", "密码不能为空");
            }
            if (!UserLoginPlatform.isValidLoginPlatform(platform)) {
                return R.error("601", "非法登陆平台");
            }

            // spring-mobile-device 已停止维护，使用简单的设备检测
            Device currentDevice = createDeviceFromRequest(request);

            String userDomain = UserLoginPlatform.getUserDomain(platform);

            if (!WebHelper.isDevModel() && LoginHelper.getInstance().needCheckCapture(userDomain)) {
                if (StringUtils.isEmpty(captcha)) {
                    return R.error("验证码不正确");
                }
                boolean b = LoginHelper.getInstance().validCaptcha(captcha);
                if (!b) {
                    return R.error("验证码不正确");
                }
            }

            /*if (!LoginHelper.getInstance().isSystemUser(userDomain) && !ValidationUtil.isValidateMobile(loginName)) {
                return R.error("601", "请输入正确手机号");
            }*/
            UserEntity userEntity = userService.findByUserDomainAndLoginName(userDomain, loginName);
            if (userEntity == null) {
                return R.error("用户名未注册");
            }
            String token = LoginHelper.getInstance().login(userEntity.getUserIden(), password, platform, currentDevice);
            if ("pending_active".equals(token)) {
                return R.error("601", WebConstants.USER_PENDING_ACTIVE);
            } else if ("user_locked".equals(token)) {
                return R.error("601", WebConstants.USER_LOCKED);
            } else if ("no_privilege".equals(token)) {
                return R.error("601", WebConstants.USER_HAS_NO_PRIVILEGE);
            } else if ("no_match".equals(token)) {
                return R.error("601", WebConstants.USER_NOT_MATCHED);
            }

            loginResponse.setId(userEntity.getUserIden().getUserId());
            loginResponse.setUserId(loginName);
            loginResponse.setToken(token);
            loginResponse.setHasPayPwd(!StringUtils.isEmpty(userEntity.getPayPwd()));
            loginResponse.setAuthed(userEntity.isAuthenticated());
            loginResponse.setIdNo(userEntity.getIdNo());
            loginResponse.setRealName(userEntity.getRealName());
            loginResponse.setPlatform(UserLoginPlatform.getPlatForm(userDomain));
            loginResponse.setDisplay(userEntity.getDisplay());
            loginResponse.setMobileNo(userEntity.getMobileNo());
            loginResponse.setDate(DateUtil.formatDate(new Date()));
            if (StringUtils.isEmpty(userEntity.getAvatarUrl())) {
                String avatarUrl = ConfigHelper.getString(User.AVATAR_URL);
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
            e.printStackTrace();
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

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<UserEntity> getUserInfo() {
        try {
            com.adminpro.rbac.api.LoginHelper loginHelper = LoginHelper.getInstance();
            com.adminpro.framework.security.auth.LoginUser loginUser = loginHelper.getLoginUser();
            if (loginUser == null) {
                return R.error("未登录");
            }
            com.adminpro.rbac.domains.entity.user.UserIden userIden = loginUser.getUserIden();
            UserEntity userEntity = userService.findByUserDomainAndUserId(userIden.getUserDomain(), userIden.getUserId());
            if (userEntity == null) {
                return R.error("用户不存在");
            }
            return R.ok(userEntity);
        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
            return R.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @SysLog("更新个人资料")
    @RequestMapping(value = "/profile", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public R updateProfile(@RequestBody UpdateProfileVo updateProfileVo) {
        try {
            com.adminpro.rbac.api.LoginHelper loginHelper = LoginHelper.getInstance();
            com.adminpro.framework.security.auth.LoginUser loginUser = loginHelper.getLoginUser();
            if (loginUser == null) {
                return R.error("未登录");
            }
            com.adminpro.rbac.domains.entity.user.UserIden userIden = loginUser.getUserIden();
            UserEntity userEntity = userService.findByUserDomainAndUserId(userIden.getUserDomain(), userIden.getUserId());
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
        request.getSession().setAttribute(RbacCacheConstants.CAPTCHA_CACHE, code);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    // spring-mobile-device 已停止维护，使用 Device 接口
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

    private static class SimpleDevice implements com.adminpro.rbac.api.Device {
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
