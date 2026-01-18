package com.adminpro.system.web.rbac;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.client.helper.ClientHelper;

import com.adminpro.framework.exceptions.APIException;
import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.rbac.api.Device;
import com.adminpro.system.rbac.api.LoginHelper;
import com.adminpro.system.rbac.api.PasswordValidator;
import com.adminpro.system.rbac.common.RbacCacheConstants;

import com.adminpro.system.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.system.rbac.domains.entity.dept.DeptService;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.adminpro.system.rbac.domains.entity.user.UserService;

import com.adminpro.system.rbac.domains.vo.login.LoginUserVo;
import com.adminpro.system.rbac.domains.vo.user.PasswordRuleVo;
import com.adminpro.system.rbac.domains.vo.user.UpdateProfileVo;
import com.adminpro.system.rbac.domains.vo.user.UserInfoResponseVo;
import com.adminpro.system.rbac.domains.vo.jwt.JwtLoginResponse;
import com.google.code.kaptcha.Producer;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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

/**
 * 认证控制器
 * <p>
 * 提供用户登录、登出、获取用户信息、验证码等认证相关的API接口
 * </p>
 *
 * @author system
 * @since 1.0.0
 */
@Tag(name = "认证管理", description = "用户登录、登出、获取用户信息、验证码等认证相关接口")
@RestController
@RequestMapping("/api/v1/auth")
/**
 * 使用 Lombok @RequiredArgsConstructor 自动生成构造器进行依赖注入。
 * 所有 final 字段将通过构造器自动注入，无需显式编写 @Autowired。
 * 添加新依赖时，只需添加 private final 字段即可。
 * 注意：captchaProducerMath 使用 @Resource 注入，不能使用 final。
 */
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final UserService userService;
    private final LoginHelper loginHelper;
    private final DeptService deptService;

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
    @Operation(summary = "用户登录", description = "处理用户登录请求，支持用户名/邮箱/手机号登录，验证码验证。返回token、用户基本信息、权限信息等")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = """
            统一响应格式，通过 restCode 判断业务状态：
            - restCode=200: 登录成功，data 字段包含 JwtLoginResponse 对象
            - restCode=401: 认证失败
            - restCode=601: 参数错误或业务失败
            - restCode=500: 服务器内部错误
            """, content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class)))
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public R<JwtLoginResponse> login(HttpServletRequest request,
            HttpServletResponse httpResponse,
            @RequestBody LoginUserVo loginUserVo) {
        BeanUtil.beanAttributeValueTrim(loginUserVo);
        String loginName = loginUserVo.getLoginName();
        String password = loginUserVo.getPassword();
        String userDomain = loginUserVo.getDomain();
        String captcha = loginUserVo.getCaptcha();

        try {
            if (StringUtils.isEmpty(loginName)) {
                return R.badRequest("用户名不能为空");
            }

            if (StringUtils.isEmpty(password)) {
                return R.badRequest("密码不能为空");
            }
            if (StringUtils.isEmpty(userDomain)) {
                return R.badRequest("用户域不能为空");
            }

            // spring-mobile-device 已停止维护，使用简单的设备检测
            Device currentDevice = createDeviceFromRequest(request);

            boolean isMobileApp = ClientHelper.isMobileAppRequest(request);
            boolean isMiniProgram = ClientHelper.isMiniProgramRequest(request);
            if (!isMobileApp && !isMiniProgram && !WebHelper.isDevModel()
                    && loginHelper.needCheckCapture(userDomain)) {
                if (StringUtils.isEmpty(captcha)) {
                    return R.badRequest("验证码不能为空");
                }
                boolean b = loginHelper.validCaptcha(captcha);
                if (!b) {
                    return R.badRequest("验证码不正确");
                }
            }

            JwtLoginResponse response = loginHelper.loginJwt(
                    userDomain, loginName, password, currentDevice, loginUserVo.isRememberMe());

            // 设置 Refresh Token Cookie (HttpOnly, Secure, SameSite=Strict)
            if (response != null && response.getRefreshToken() != null) {
                Cookie refreshTokenCookie = new Cookie("refreshToken", response.getRefreshToken());
                refreshTokenCookie.setHttpOnly(true); // 防止 XSS 攻击
                refreshTokenCookie.setSecure(request.isSecure()); // 仅 HTTPS 时发送（生产环境）
                refreshTokenCookie.setPath("/"); // 全站可用
                // Refresh Token 有效期（根据 rememberMe 设置）
                int maxAge = loginUserVo.isRememberMe() ? 30 * 24 * 60 * 60 : 7 * 24 * 60 * 60; // 30天 或 7天
                refreshTokenCookie.setMaxAge(maxAge);
                // SameSite 属性需要通过 Set-Cookie header 设置
                httpResponse.addCookie(refreshTokenCookie);
                // 添加 SameSite=Strict 属性（Cookie API 不直接支持）
                httpResponse.setHeader("Set-Cookie",
                        String.format("refreshToken=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Strict%s",
                                response.getRefreshToken(), maxAge, request.isSecure() ? "; Secure" : ""));
            }

            return R.ok(response);
        } catch (Exception e) {
            logger.error("登陆异常：", e);
            if (e instanceof AuthenticationException) {
                return R.authFailed("账号或密码错误");
            } else if (e instanceof BadCredentialsException) {
                return R.authFailed(e.getMessage());
            } else if (e instanceof UsernameNotFoundException) {
                return R.authFailed(e.getMessage());
            } else if (e instanceof APIException) {
                return R.authFailed(e.getMessage());
            } else {
                return R.error(e.getMessage());
            }
        }
    }

    /**
     * 刷新Token
     * <p>
     * 使用Refresh Token获取新的Access Token和Refresh Token
     * </p>
     *
     * @param map 包含refreshToken的请求体
     * @return 新的登录信息
     */
    @SysLog("刷新Token")
    @Operation(summary = "刷新Token", description = "使用Refresh Token获取新的Access Token和Refresh Token。Refresh Token从HttpOnly Cookie中读取。")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = """
            统一响应格式，通过 restCode 判断业务状态：
            - restCode=200: 刷新成功，data 字段包含 JwtLoginResponse 对象
            - restCode=401: Refresh Token 无效或已过期
            - restCode=500: 服务器内部错误
            """, content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class)))
    @RequestMapping(value = "/refresh", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<JwtLoginResponse> refresh(
            HttpServletRequest request, HttpServletResponse httpResponse) {
        // 从 HttpOnly Cookie 中读取 refreshToken
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (StringUtils.isEmpty(refreshToken)) {
            return R.authFailed("Refresh Token不存在，请重新登录");
        }

        try {
            JwtLoginResponse response = loginHelper.refreshJwt(refreshToken);

            // 设置新的 Refresh Token Cookie (HttpOnly, Secure, SameSite=Strict)
            if (response != null && response.getRefreshToken() != null) {
                int maxAge = 7 * 24 * 60 * 60; // 7天
                httpResponse.setHeader("Set-Cookie",
                        String.format("refreshToken=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Strict%s",
                                response.getRefreshToken(), maxAge, request.isSecure() ? "; Secure" : ""));
            }

            return R.ok(response);
        } catch (APIException e) {
            return R.authFailed(e.getMessage());
        } catch (Exception e) {
            logger.error("刷新Token失败", e);
            return R.error("刷新Token失败");
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
    @Operation(summary = "获取当前登录用户信息", description = "根据当前登录token获取用户的详细信息，包括基本资料、部门信息等")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = """
            统一响应格式，通过 restCode 判断业务状态：
            - restCode=200: 查询成功，data 字段包含 UserInfoResponseVo 对象
            - restCode=401: 未授权，需要登录
            - restCode=500: 服务器内部错误
            """, content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class)))
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
            userInfoVo.setId(userEntity.getId());
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
    @Operation(summary = "获取密码规则", description = "获取系统当前配置的密码强度规则，包括长度、复杂度等要求")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = """
            统一响应格式，通过 restCode 判断业务状态：
            - restCode=200: 查询成功，data 字段包含 PasswordRuleVo 对象
            - restCode=401: 未授权，需要登录
            - restCode=500: 服务器内部错误
            """, content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class)))
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
    @Operation(summary = "更新个人资料", description = "允许当前登录用户更新自己的基本资料信息，包括真实姓名、手机号、邮箱、头像等")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = """
            统一响应格式，通过 restCode 判断业务状态：
            - restCode=200: 更新成功
            - restCode=400: 参数错误
            - restCode=401: 未授权，需要登录
            - restCode=500: 服务器内部错误
            """, content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class)))
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
    @Operation(summary = "生成验证码图片", description = "生成数学运算类型的验证码图片，用于登录安全验证")
    @RequestMapping(value = "/captcha.jpg", method = RequestMethod.GET)
    public void captcha(HttpServletResponse response) throws ServletException, IOException {
        // 设置响应头，禁用缓存
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        String capStr = null;
        String code = null;
        BufferedImage bi = null;

        String capText = captchaProducerMath.createText();
        capStr = capText.substring(0, capText.lastIndexOf("@"));
        code = capText.substring(capText.lastIndexOf("@") + 1);
        bi = captchaProducerMath.createImage(capStr);

        // 生成唯一的验证码 Key
        String captchaKey = java.util.UUID.randomUUID().toString().replace("-", "");

        // 将验证码存储到缓存，有效期 5 分钟（300秒）- 无状态方式
        AppCache.getInstance().set(RbacCacheConstants.CAPTCHA_CACHE, captchaKey, code, 300);

        logger.debug("验证码生成 - captchaKey: {}, 答案: {}", captchaKey, code);

        // 通过 Cookie 返回 captchaKey 给前端
        Cookie captchaCookie = new Cookie("captchaKey", captchaKey);
        captchaCookie.setPath("/");
        captchaCookie.setMaxAge(300); // 5分钟
        captchaCookie.setHttpOnly(false); // 允许前端 JS 读取
        response.addCookie(captchaCookie);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    /**
     * 用户登出
     * <p>
     * 处理用户登出请求，清除session和token
     * </p>
     *
     * @return 登出结果
     */
    @SysLog("用户登出")
    @Operation(summary = "用户登出", description = "处理用户登出请求，清除session和token")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = """
            统一响应格式，通过 restCode 判断业务状态：
            - restCode=200: 登出成功
            - restCode=500: 登出失败或服务器内部错误
            """, content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class)))
    @RequestMapping(value = "/logout", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<String> logout() {
        try {
            LoginHelper.getInstance().logout();
            return R.ok("登出成功");
        } catch (Exception e) {
            logger.error("登出异常：", e);
            return R.error("登出失败: " + e.getMessage());
        }
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
