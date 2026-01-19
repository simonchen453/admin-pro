package com.adminpro.system.rbac.api;

import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.exceptions.APIException;
import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.core.cache.CurrentUserCache;
import com.adminpro.system.core.common.helper.AuditLogHelper;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.core.common.helper.ip.IpUtils;
import com.adminpro.system.core.security.auth.LoginUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.adminpro.system.core.security.jwt.DeviceFingerprintService;
import com.adminpro.system.core.security.jwt.JwtCacheConstants;
import com.adminpro.system.core.security.jwt.JwtTokenProvider;
import com.adminpro.system.core.security.jwt.RefreshTokenService;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import com.adminpro.system.rbac.common.RbacConstants;
import com.adminpro.system.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.system.rbac.domains.entity.dept.DeptService;
import com.adminpro.system.rbac.domains.entity.jwt.RefreshTokenData;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.adminpro.system.rbac.domains.entity.user.UserService;
import com.adminpro.system.rbac.domains.vo.jwt.JwtLoginResponse;
import com.adminpro.system.rbac.enums.UserStatus;
import com.adminpro.system.config.JwtProperties;
import com.adminpro.system.rbac.api.Device;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.javasimon.aop.Monitored;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;

/**
 * 登录助手类
 * <p>
 * 提供用户登录、登出、以及获取当前登录用户信息等核心功能。
 * 采用 JWT 无状态认证模式。
 * <p>
 * 主要功能：
 * <ul>
 * <li>用户登录：支持用户名密码登录，返回 JWT Token（Access Token + Refresh Token）</li>
 * <li>用户登出：清理认证信息，从白名单移除 Token，撤销 Refresh Token（单点登出）</li>
 * <li>获取当前用户：从 SecurityContext 中获取登录用户信息</li>
 * <li>Token 刷新：支持使用 Refresh Token 获取新的 Access Token</li>
 * </ul>
 * <p>
 * 认证模式：
 * <ul>
 * <li>JWT 无状态认证：使用 Authorization Header 或 Cookie 传递 Token</li>
 * <li>Access Token：短期有效，用于 API 认证</li>
 * <li>Refresh Token：长期有效，用于刷新 Access Token，支持设备管理</li>
 * </ul>
 * <p>
 * 安全特性：
 * <ul>
 * <li>登录失败审计：记录登录失败日志</li>
 * <li>Token 白名单：Access Token 存储在缓存中，支持主动撤销</li>
 * <li>单点登出：撤销用户所有 Refresh Token</li>
 * <li>设备管理：限制同一用户的最大设备数</li>
 * <li>验证码支持：可选的验证码验证功能</li>
 * </ul>
 *
 * @author simon
 * @see LoginUser
 */
@Service
@Monitored
@Transactional(rollbackFor = Exception.class)
public class LoginHelper {

    /**
     * 日志记录器
     */
    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取LoginHelper单例实例
     *
     * @return LoginHelper实例
     */
    public static LoginHelper getInstance() {
        return SpringUtil.getBean(LoginHelper.class);
    }

    @Autowired
    private AuthenticationManager authenticationmanager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 验证账户和密码
     * <p>
     * 使用Spring Security的AuthenticationManager进行认证验证。
     *
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param password   密码
     * @return 认证对象
     * @throws APIException 认证失败时抛出，包含失败原因
     */
    private Authentication verifyAccount(String userDomain, String loginName, String password) throws APIException {
        String securityUsername = userDomain + "_" + loginName;
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                securityUsername, password);
        try {
            final Authentication authentication = authenticationmanager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}, exception type: {}, message: {}",
                    securityUsername, e.getClass().getName(), e.getMessage(), e);
            if (e instanceof BadCredentialsException) {
                throw new APIException("用户密码不匹配");
            } else {
                throw new APIException("登录失败: " + e.getMessage());
            }
        }
    }

    /**
     * 判断用户域是否需要验证码验证
     *
     * @param userDomain 用户域
     * @return true表示需要验证码
     */
    public boolean needCheckCapture(String userDomain) {
        return ArrayUtils.contains(RbacConstants.getNeedCheckCaptureDomains(), userDomain);
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 从 SecurityContext 获取 JWT 认证的用户信息。
     * JWT认证模式下，JwtAuthenticationFilter 会将用户信息设置到 SecurityContext。
     * <p>
     * 注意：不再使用 Session，服务器为无状态模式。
     *
     * @return 登录用户信息，未登录时返回null
     */
    public LoginUser getLoginUser() {
        // 从 SecurityContext 获取 (JWT 认证)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前登录用户的真实姓名
     *
     * @return 真实姓名，未登录时返回null
     */
    public String getLoginUserRealName() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getRealName();
        } else {
            return null;
        }
    }

    /**
     * 获取当前登录用户的部门编号
     *
     * @return 部门编号，未登录时返回null
     */
    public String getLoginUserDeptNo() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getDeptNo();
        } else {
            return null;
        }
    }

    /**
     * 获取当前登录用户的部门ID（全局唯一主键）
     *
     * @return 部门ID，未登录或部门不存在时返回null
     */
    public String getLoginUserDeptId() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            String deptNo = loginUser.getDeptNo();
            DeptEntity deptEntity = DeptService.getInstance().findByNo(deptNo);
            if (deptEntity != null) {
                return deptEntity.getId();
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户ID（全局唯一主键）
     *
     * @return 用户ID，未登录时返回null
     */
    public String getLoginUserId() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getId();
        }
        return null;
    }

    /**
     * 获取当前会话的 JTI（JWT Token ID）
     * <p>
     * JTI 是 JWT 的唯一标识，每次登录生成不同的 JTI。
     * 可用于构建会话级缓存 key，区分同一用户的不同登录会话。
     * </p>
     *
     * @return JTI，未登录或无法获取时返回 null
     */
    public String getCurrentJti() {
        HttpServletRequest request = WebHelper.getHttpRequest();
        if (request == null) {
            return null;
        }

        try {
            // 从 Authorization Header 中提取 JWT
            String bearerToken = request.getHeader("Authorization");
            if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
                String jwt = bearerToken.substring(7);
                return jwtTokenProvider.getJti(jwt);
            }

            // 从 Cookie 中提取
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("accessToken".equals(cookie.getName())) {
                        return jwtTokenProvider.getJti(cookie.getValue());
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("获取 JTI 失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前设备ID（基于设备指纹）
     * <p>
     * 使用 HTTP 请求特征（User-Agent、Accept-Language 等）生成稳定的设备标识。
     * 同一浏览器/设备的多次请求会生成相同的设备ID。
     * </p>
     *
     * @return 设备ID（32位哈希值），无法获取时返回 "unknown"
     */
    public String getCurrentDeviceId() {
        HttpServletRequest request = WebHelper.getHttpRequest();
        if (request == null) {
            return "unknown";
        }

        try {
            DeviceFingerprintService fingerprintService = SpringUtil.getBean(DeviceFingerprintService.class);
            return fingerprintService.generateFingerprint(request);
        } catch (Exception e) {
            logger.debug("获取设备指纹失败: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * 获取当前登录用户的用户域
     *
     * @return 用户域，未登录时返回null
     */
    public String getUserDomain() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getUserDomain();
        }
        return null;
    }

    /**
     * 获取当前登录用户的登录名
     *
     * @return 登录名，未登录时返回null
     */
    public String getLoginName() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getLoginName();
        }
        return null;
    }

    /**
     * 获取当前登录用户的实体对象
     *
     * @return 用户实体对象，未登录时返回null
     */
    public UserEntity getUserEntity() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getUser();
        } else {
            return null;
        }
    }

    /**
     * 判断是否为当前登录用户
     * <p>
     * 比对用户域和登录名是否与当前登录用户一致
     *
     * @param userDomain 用户域
     * @param loginName  登录名
     * @return true表示是当前登录用户
     */
    public boolean isCurrentUser(String userDomain, String loginName) {
        String currentDomain = getUserDomain();
        String currentLoginName = getLoginName();
        return StringUtils.equals(currentDomain, userDomain)
                && StringUtils.equals(currentLoginName, loginName);
    }

    /**
     * 获取当前登录用户的所有权限
     *
     * @return 权限数组，未登录时返回空数组
     */
    public String[] getPermissions() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            List<String> permissions = loginUser.getPermissions();
            return permissions.toArray(new String[permissions.size()]);
        } else {
            return new String[0];
        }
    }

    /**
     * 用户登出
     * <p>
     * JWT 登出流程：
     * <ul>
     * <li>从 Authorization Header 或 Cookie 中获取 JWT</li>
     * <li>验证 Token 并从白名单中移除</li>
     * <li>清除该用户的所有 Refresh Token（单点登出）</li>
     * <li>清除 SecurityContext</li>
     * </ul>
     *
     * @return 登出成功返回true
     */
    public boolean logout() {
        HttpServletRequest httpRequest = WebHelper.getHttpRequest();

        // 1. 尝试处理 JWT 登出
        String jwt = null;
        String bearerToken = httpRequest.getHeader("Authorization");
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            jwt = bearerToken.substring(7);
        } else if (httpRequest.getCookies() != null) {
            for (Cookie cookie : httpRequest.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (StringUtils.isNotBlank(jwt)) {
            try {
                if (jwtTokenProvider.validateToken(jwt)) {
                    String jti = jwtTokenProvider.getJti(jwt);

                    // 从白名单获取 userId
                    String userId = AppCache.getInstance().get(
                            JwtCacheConstants.ACCESS_TOKEN_CACHE,
                            jti,
                            String.class);

                    // 移除 Access Token 白名单
                    AppCache.getInstance().delete(
                            JwtCacheConstants.ACCESS_TOKEN_CACHE,
                            jti);

                    // 如果找到了 userId，清除该用户的所有 Refresh Token（单点登出）
                    if (StringUtils.isNotBlank(userId)) {
                        RefreshTokenService rtService = SpringUtil.getBean(RefreshTokenService.class);
                        int revokedCount = rtService.revokeAllUserTokens(userId);
                        logger.info("用户 {} 登出，已清除 {} 个 Refresh Token", userId, revokedCount);

                        // 清除当前会话的业务缓存（不影响其他设备/会话）
                        int cacheCount = CurrentUserCache.clearCurrentSession();
                        logger.info("用户 {} 登出，已清除 {} 个业务缓存", userId, cacheCount);
                    }

                    // 清除 SecurityContext
                    SecurityContextHolder.clearContext();
                    return true;
                }
            } catch (Exception e) {
                logger.warn("JWT logout process failed", e);
            }
        }

        return false;
    }

    /**
     * 验证验证码
     * <p>
     * 从AppCache中获取验证码并与用户输入进行比较。
     * 验证成功后自动删除缓存中的验证码，防止重复使用。
     * <p>
     * 注意：不再使用 Session，服务器为无状态模式。
     *
     * @param captcha 用户输入的验证码
     * @return true表示验证码正确，false表示验证码不存在或错误
     */
    public boolean validCaptcha(String captcha) {
        HttpServletRequest request = WebHelper.getHttpRequest();

        // 从 Cookie 中获取 captchaKey，使用缓存验证（无状态）
        String captchaKey = getCaptchaKeyFromCookie(request);
        if (captchaKey == null) {
            logger.debug("验证码验证失败 - captchaKey Cookie 不存在");
            return false;
        }

        String storedCaptcha = AppCache.getInstance().get(RbacCacheConstants.CAPTCHA_CACHE, captchaKey, String.class);
        logger.debug("验证码验证(缓存) - captchaKey: {}, 存储的验证码: {}, 用户输入: {}", captchaKey, storedCaptcha, captcha);

        if (storedCaptcha == null) {
            logger.debug("验证码验证失败 - 缓存中没有存储验证码");
            return false;
        }

        boolean isValid = StringUtils.equalsIgnoreCase(captcha, storedCaptcha);
        if (isValid) {
            // 验证成功后删除缓存，防止重复使用
            AppCache.getInstance().delete(RbacCacheConstants.CAPTCHA_CACHE, captchaKey);
        }
        return isValid;
    }

    /**
     * 从 Cookie 中获取 captchaKey
     */
    private String getCaptchaKeyFromCookie(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("captchaKey".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * JWT 方式登录
     */
    public JwtLoginResponse loginJwt(String userDomain, String loginName,
            String password, Device device, boolean rememberMe) throws APIException {
        String securityUsername = userDomain + "_" + loginName;
        // 1. 验证账户
        Authentication authentication = verifyAccount(userDomain, loginName, password);
        LoginUser userDetails = (LoginUser) authentication.getPrincipal();

        if (authentication == null || userDetails == null) {
            throw new APIException("账号或密码错误");
        }
        if (StringHelper.equals(userDetails.getStatus(), UserStatus.LOCKED.getCode())) {
            throw new APIException("账户已锁定");
        }
        if (StringHelper.equals(userDetails.getStatus(), UserStatus.INACTIVE.getCode())) {
            throw new APIException("账户已停用");
        }

        // 2. 生成 Access Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userDomain", userDetails.getUserDomain());
        claims.put("loginName", userDetails.getLoginName());
        claims.put("realName", userDetails.getRealName());
        claims.put("deptNo", userDetails.getDeptNo());
        String platform = (device != null && device.isMobile()) ? "mobile" : "web";
        claims.put("aud", platform);
        claims.put("permissions", userDetails.getPermissions());

        String accessToken = jwtTokenProvider.createAccessToken(userDetails.getId(), claims);

        // 3. 存入 Access Token 白名单（使用与 JWT 一致的过期时间）
        String jti = jwtTokenProvider.getJti(accessToken);
        int accessTokenValidity = jwtProperties.getAccessTokenValidity(platform);
        // Ehcache key 是 jti，value 是 userId，expire 是过期时间（秒）
        AppCache.getInstance().set(JwtCacheConstants.ACCESS_TOKEN_CACHE, jti,
                userDetails.getId(), accessTokenValidity);

        // 4. 生成 Refresh Token
        RefreshTokenData rtData = new RefreshTokenData();
        rtData.setUserId(userDetails.getId());
        rtData.setUserDomain(userDetails.getUserDomain());
        rtData.setLoginName(userDetails.getLoginName());
        rtData.setPlatform(platform);

        // 使用设备指纹生成稳定的 deviceId（同一设备多次登录相同）
        HttpServletRequest request = WebHelper.getHttpRequest();
        DeviceFingerprintService fingerprintService = SpringUtil.getBean(DeviceFingerprintService.class);

        String deviceId = fingerprintService.generateFingerprint(request);
        String deviceName = fingerprintService.generateDeviceName(request);
        String ip = IpUtils.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");

        rtData.setDeviceId(deviceId);
        rtData.setDeviceName(deviceName);
        rtData.setIp(ip);
        rtData.setUserAgent(userAgent);
        rtData.setCreatedAt(java.time.LocalDateTime.now());
        rtData.setLastUsedAt(java.time.LocalDateTime.now());
        rtData.setRememberMe(rememberMe);

        String refreshToken = refreshTokenService.createRefreshToken(rtData);

        // 5. 记录日志
        UserEntity userEntity = userDetails.getUser();
        userEntity.setLatestLoginTime(new Date());
        UserService.getInstance().update(userEntity);
        AuditLogHelper.log(AuditLogHelper.CATEGORY_ADMIN, RbacConstants.AUDIT_MODULE_USER,
                RbacConstants.AUDIT_ACTION_LOGIN, RbacConstants.LOGIN_RESULT_SUCCESS,
                "userDomain=" + userDomain + ", loginName=" + loginName);

        // 6. 构造响应
        return JwtLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenValidity(platform))
                .user(JwtLoginResponse.UserInfo.builder()
                        .id(userDetails.getId())
                        .loginName(userDetails.getLoginName())
                        .realName(userDetails.getRealName())
                        .avatarUrl(userDetails.getUser().getAvatarUrl())
                        .build())
                .build();
    }

    /**
     * 刷新 Token
     *
     * @param refreshToken 刷新令牌
     * @return 新的 Token 响应
     */
    public JwtLoginResponse refreshJwt(String refreshToken)
            throws APIException {
        // 1. 验证 Refresh Token (获取数据)
        RefreshTokenData data = refreshTokenService
                .validateRefreshToken(refreshToken);
        if (data == null) {
            throw new APIException("无效或已过期的 Refresh Token");
        }

        // 2. 重新加载用户信息 (确保权限最新)
        String securityUsername = data.getUserDomain() + "_" + data.getLoginName();
        LoginUser userDetails = null;
        try {
            userDetails = (LoginUser) userDetailsService.loadUserByUsername(securityUsername);
        } catch (Exception e) {
            logger.error("刷新Token时加载用户失败: {}", securityUsername, e);
            throw new APIException("加载用户信息失败: " + e.getMessage());
        }

        if (userDetails == null) {
            throw new APIException("用户不存在");
        }
        if (StringHelper.equals(userDetails.getStatus(), UserStatus.LOCKED.getCode())) {
            throw new APIException("账户已锁定");
        }
        if (StringHelper.equals(userDetails.getStatus(), UserStatus.INACTIVE.getCode())) {
            throw new APIException("账户已停用");
        }

        // 3. 轮换 Refresh Token
        String newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);

        // 4. 获取旧 JTI（用于会话缓存迁移）
        String oldJti = getCurrentJti();

        // 5. 生成新的 Access Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userDomain", userDetails.getUserDomain());
        claims.put("loginName", userDetails.getLoginName());
        claims.put("realName", userDetails.getRealName());
        claims.put("deptNo", userDetails.getDeptNo());
        String platform = data.getPlatform(); // 保持原有平台
        claims.put("aud", platform);
        claims.put("permissions", userDetails.getPermissions());

        String accessToken = jwtTokenProvider.createAccessToken(userDetails.getId(), claims);

        // 6. 存入 Access Token 白名单（使用与 JWT 一致的过期时间）
        String newJti = jwtTokenProvider.getJti(accessToken);
        int accessTokenValidity = jwtProperties.getAccessTokenValidity(platform);
        AppCache.getInstance().set(JwtCacheConstants.ACCESS_TOKEN_CACHE, newJti,
                userDetails.getId(), accessTokenValidity);

        // 7. 迁移会话级缓存（从旧 JTI 到新 JTI）
        if (StringUtils.isNotBlank(oldJti) && !oldJti.equals(newJti)) {
            CurrentUserCache.migrateSession(userDetails.getId(), oldJti, newJti, accessTokenValidity);
        }

        return JwtLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenValidity(platform))
                .user(JwtLoginResponse.UserInfo.builder()
                        .id(userDetails.getId())
                        .loginName(userDetails.getLoginName())
                        .realName(userDetails.getRealName())
                        .avatarUrl(userDetails.getUser().getAvatarUrl())
                        .build())
                .build();
    }
}
