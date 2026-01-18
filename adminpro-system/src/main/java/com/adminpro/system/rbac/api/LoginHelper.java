package com.adminpro.system.rbac.api;

import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.client.helper.ClientHelper;
import com.adminpro.framework.exceptions.APIException;
import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.core.common.helper.AuditLogHelper;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.core.common.helper.ip.AddressUtils;
import com.adminpro.system.core.common.helper.ip.IpUtils;
import com.adminpro.system.core.security.auth.AuthToken;
import com.adminpro.system.core.security.auth.AuthUserDetailServiceImpl;
import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.core.security.auth.TokenHelper;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import com.adminpro.system.rbac.common.RbacConstants;
import com.adminpro.system.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.system.rbac.domains.entity.dept.DeptService;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.adminpro.system.rbac.domains.entity.user.UserService;
import com.adminpro.system.rbac.domains.entity.usertoken.UserTokenEntity;
import com.adminpro.system.rbac.enums.UserStatus;
import com.adminpro.system.tools.domains.entity.session.SessionEntity;
import com.adminpro.system.tools.domains.entity.session.SessionService;
import com.adminpro.system.tools.domains.enums.SessionStatus;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
 * 支持移动端和Web端两种不同的认证模式。
 * <p>
 * 主要功能：
 * <ul>
 * <li>用户登录：支持用户名密码登录，返回Token或Session</li>
 * <li>用户登出：清理认证信息，使Token或Session失效</li>
 * <li>获取当前用户：从Session或SecurityContext中获取登录用户信息</li>
 * <li>会话管理：Session的创建、刷新、失效等操作</li>
 * </ul>
 * <p>
 * 认证模式：
 * <ul>
 * <li>移动端：基于Token的无状态认证，使用Authorization Header传递Token</li>
 * <li>Web端：基于Session的有状态认证，使用HttpSession存储用户信息</li>
 * </ul>
 * <p>
 * 安全特性：
 * <ul>
 * <li>登录失败审计：记录登录失败日志</li>
 * <li>会话固定攻击防护：登录成功后重新创建Session</li>
 * <li>用户信息收集：记录登录IP、地点、浏览器、操作系统等</li>
 * <li>验证码支持：可选的验证码验证功能</li>
 * </ul>
 *
 * @author simon
 * @see TokenHelper
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

    /**
     * Web端Session中存储登录用户的Key
     */
    public static final String LOGIN_AUTH_USER_KEY = "http_login_authuser";

    @Autowired
    private AuthenticationManager authenticationmanager;

    @Autowired
    private AuthUserDetailServiceImpl userDetailsService;

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private com.adminpro.system.core.security.jwt.JwtTokenProvider jwtTokenProvider;

    @Autowired
    private com.adminpro.system.core.security.jwt.RefreshTokenService refreshTokenService;

    @Autowired
    private com.adminpro.system.config.JwtProperties jwtProperties;

    /**
     * 用户登录（Web端）
     * <p>
     * 默认使用Web端登录，不指定设备类型
     *
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param password   密码
     * @return 登录结果，成功返回"success"，失败返回错误码
     * @throws APIException 登录失败时抛出
     */
    public String login(String userDomain, String loginName, String password) throws APIException {
        return login(userDomain, loginName, password, null);
    }

    /**
     * 用户登录（支持设备类型）
     * <p>
     * 根据请求类型自动选择认证模式：
     * <ul>
     * <li>移动端请求：返回Token，支持Bearer认证</li>
     * <li>Web端请求：使用Session认证</li>
     * </ul>
     * <p>
     * 登录流程：
     * <ol>
     * <li>验证账户和密码</li>
     * <li>检查用户状态（是否锁定、停用）</li>
     * <li>根据请求类型选择认证模式</li>
     * <li>记录登录日志和用户信息</li>
     * <li>更新最后登录时间</li>
     * </ol>
     *
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param password   密码
     * @param device     设备类型，为null时默认为Web端
     * @return 登录结果，成功返回"success"或Token，失败返回错误码
     * @throws APIException 登录失败时抛出
     */
    public String login(String userDomain, String loginName, String password, Device device) throws APIException {
        String securityUsername = userDomain + "_" + loginName;
        logger.info(MessageFormat.format("用户{0}尝试登陆{1}", securityUsername, userDomain));
        boolean isMobileRequest = ClientHelper.isMobileRequest(WebHelper.getHttpRequest());

        Authentication authentication = verifyAccount(userDomain, loginName, password);

        final LoginUser userDetails = (LoginUser) userDetailsService.loadUserByUsername(securityUsername);
        if (authentication == null || userDetails == null) {
            return RbacConstants.LOGIN_RESULT_NO_MATCH;
        }
        if (StringHelper.equals(userDetails.getStatus(), UserStatus.LOCKED.getCode())) {
            logger.error("用户{0}, 账户锁定", securityUsername);
            return RbacConstants.LOGIN_RESULT_USER_LOCKED;
        }
        if (StringHelper.equals(userDetails.getStatus(), UserStatus.INACTIVE.getCode())) {
            logger.error("用户{0}, 账户停用", securityUsername);
            return RbacConstants.LOGIN_RESULT_USER_INACTIVE;
        }

        logger.info(MessageFormat.format("用户{0}登陆成功", userDomain + "/" + userDetails.getLoginName()));
        if (isMobileRequest) {
            return handleMobileLogin(userDomain, loginName, authentication, device);
        } else {
            return handleWebLogin(userDomain, loginName, authentication, userDetails);
        }
    }

    /**
     * 处理移动端登录
     * <p>
     * 移动端登录使用Token认证模式：
     * <ol>
     * <li>生成Token并存储到数据库</li>
     * <li>更新用户最后登录时间</li>
     * <li>记录登录审计日志</li>
     * <li>返回Token给客户端</li>
     * </ol>
     *
     * @param userDomain     用户域
     * @param loginName      登录名
     * @param authentication 认证对象
     * @param device         设备类型
     * @return Token字符串
     */
    private String handleMobileLogin(String userDomain, String loginName, Authentication authentication,
            Device device) {
        AuthToken principal = (AuthToken) authentication.getPrincipal();
        String token = principal.getToken();
        String deviceType = device != null ? tokenHelper.generateAudience(device) : TokenHelper.AUDIENCE_WEB;
        tokenHelper.generateToken(userDomain, loginName, token, deviceType);
        UserEntity userEntity = UserService.getInstance().findByUserDomainAndLoginName(userDomain, loginName);
        userEntity.setLatestLoginTime(new Date());
        UserService.getInstance().update(userEntity);
        AuditLogHelper.log(AuditLogHelper.CATEGORY_ADMIN, RbacConstants.AUDIT_MODULE_USER,
                RbacConstants.AUDIT_ACTION_LOGIN, RbacConstants.LOGIN_RESULT_SUCCESS,
                "userDomain=" + userDomain + ", loginName=" + loginName);
        return token;
    }

    /**
     * 处理Web端登录
     * <p>
     * Web端登录使用Session认证模式：
     * <ol>
     * <li>设置用户代理信息（IP、地点、浏览器、操作系统）</li>
     * <li>将用户信息存储到Session</li>
     * <li>重新创建Session（防止会话固定攻击）</li>
     * <li>创建会话记录</li>
     * <li>更新用户最后登录时间</li>
     * <li>记录登录审计日志</li>
     * </ol>
     *
     * @param userDomain     用户域
     * @param loginName      登录名
     * @param authentication 认证对象
     * @param userDetails    用户详情
     * @return 登录结果，成功返回"success"
     */
    private String handleWebLogin(String userDomain, String loginName, Authentication authentication,
            LoginUser userDetails) {
        HttpSession session = WebHelper.getHttpRequest().getSession();
        Object principal = authentication.getPrincipal();
        String token = RbacConstants.LOGIN_RESULT_SUCCESS;
        if (principal instanceof AuthToken) {
            token = ((AuthToken) principal).getToken();
        }
        if (StringUtils.equals(token, RbacConstants.LOGIN_RESULT_SUCCESS)) {
            setUserAgent(userDetails);
            session.setAttribute(LOGIN_AUTH_USER_KEY, userDetails);
            renewSession(session, WebHelper.getHttpRequest());
            UserEntity userEntity = UserService.getInstance().findByUserDomainAndLoginName(userDomain, loginName);
            userEntity.setLatestLoginTime(new Date());
            UserService.getInstance().update(userEntity);
            loginUserSession(userDetails);
            AuditLogHelper.log(AuditLogHelper.CATEGORY_ADMIN, RbacConstants.AUDIT_MODULE_USER,
                    RbacConstants.AUDIT_ACTION_LOGIN,
                    RbacConstants.LOGIN_RESULT_SUCCESS,
                    "userDomain=" + userDomain + ", loginName=" + loginName);
        }
        return token;
    }

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
     * 设置用户代理信息
     * <p>
     * 从HTTP请求中提取并设置以下信息：
     * <ul>
     * <li>IP地址：从X-Forwarded-For或RemoteAddr获取</li>
     * <li>登录地点：根据IP地址解析地理位置</li>
     * <li>浏览器类型：从User-Agent解析</li>
     * <li>操作系统：从User-Agent解析</li>
     * </ul>
     *
     * @param loginUser 登录用户信息
     */
    private void setUserAgent(LoginUser loginUser) {
        HttpServletRequest request = WebHelper.getHttpRequest();
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(request);
        loginUser.setIpAddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 创建用户会话记录
     * <p>
     * 在用户登录成功后，将会话信息保存到数据库，
     * 用于后续的会话管理和审计。
     *
     * @param loginUser 登录用户信息
     */
    private static void loginUserSession(LoginUser loginUser) {
        UserEntity user = loginUser.getUser();
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setUserId(user.getId());
        sessionEntity.setDeptNo(user.getDeptNo());
        sessionEntity.setStatus(SessionStatus.ACTIVE.getCode());
        sessionEntity.setSessionId(WebHelper.getSessionId());
        sessionEntity.setIpAddr(loginUser.getIpAddr());
        sessionEntity.setLoginLocation(loginUser.getLoginLocation());
        sessionEntity.setBrowser(loginUser.getBrowser());
        sessionEntity.setOs(loginUser.getOs());
        SessionService.getInstance().create(sessionEntity);
    }

    /**
     * 判断是否为互联网用户
     *
     * @param userDomain 用户域
     * @return true表示是互联网用户
     */
    public boolean isInternetUser(String userDomain) {
        return StringUtils.equals(userDomain, RbacConstants.INTERNET_DOMAIN);
    }

    /**
     * 判断是否为系统用户
     *
     * @param userDomain 用户域
     * @return true表示是系统用户
     */
    public boolean isSystemUser(String userDomain) {
        return StringUtils.equals(userDomain, RbacConstants.SYSTEM_DOMAIN);
    }

    /**
     * 判断是否为内网用户
     *
     * @param userDomain 用户域
     * @return true表示是内网用户
     */
    public boolean isIntranetUser(String userDomain) {
        return StringUtils.equals(userDomain, RbacConstants.INTRANET_DOMAIN);
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
            return loginUser.getUserId();
        }
        return null;
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
     * 根据请求类型执行不同的登出逻辑：
     * <ul>
     * <li>移动端：使Token失效，清除认证信息</li>
     * <li>Web端：使Session失效，清除会话记录</li>
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
                            com.adminpro.system.core.security.jwt.JwtCacheConstants.ACCESS_TOKEN_CACHE,
                            jti,
                            String.class
                    );

                    // 移除 Access Token 白名单
                    AppCache.getInstance().delete(
                            com.adminpro.system.core.security.jwt.JwtCacheConstants.ACCESS_TOKEN_CACHE,
                            jti
                    );

                    // 如果找到了 userId，清除该用户的所有 Refresh Token（单点登出）
                    if (StringUtils.isNotBlank(userId)) {
                        com.adminpro.system.core.security.jwt.RefreshTokenService rtService =
                                SpringUtil.getBean(com.adminpro.system.core.security.jwt.RefreshTokenService.class);
                        int revokedCount = rtService.revokeAllUserTokens(userId);
                        logger.info("用户 {} 登出，已清除 {} 个 Refresh Token", userId, revokedCount);
                    }

                    // 清除 SecurityContext
                    SecurityContextHolder.clearContext();
                    return true;
                }
            } catch (Exception e) {
                logger.warn("JWT logout process failed", e);
            }
        }

        // 2. 传统登出逻辑
        boolean isMobileRequest = ClientHelper.isMobileRequest(WebHelper.getHttpRequest());
        if (isMobileRequest) {
            String authToken = getAuthToken();
            UserTokenEntity userTokenEntity = TokenHelper.getInstance().deactiveToken(authToken);
            if (userTokenEntity != null) {
                com.adminpro.system.rbac.domains.entity.user.UserEntity user = UserService.getInstance()
                        .findById(userTokenEntity.getUserId());
                if (user != null) {
                    String securityUsername = user.getUserDomain() + "_" + user.getLoginName();
                    AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, securityUsername);
                }
            }
            SecurityContextHolder.getContext().setAuthentication(null);
            return true;
        } else {
            // 使用 getSession(false) 避免自动创建新 session
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                logger.debug("logout: {}", session.getId());
                SessionService.getInstance().invalid(session.getId());
                session.invalidate();
            }
            return true;
        }
    }

    /**
     * 重新创建Session
     * <p>
     * 防止会话固定攻击的安全措施。
     * 登录成功后，将旧Session的属性复制到新Session，并使旧Session失效。
     *
     * @param session 当前Session
     * @param request HTTP请求
     */
    public void renewSession(HttpSession session, HttpServletRequest request) {
        logger.debug("renewSession: {}", session.getId());
        Map<String, Object> attrs = getSessionAttributes(session);
        session.invalidate();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Cookie cookie = request.getCookies()[0];
            cookie.setMaxAge(0);
        }
        session = request.getSession(true);
        fillAttributesToSession(session, attrs);
    }

    /**
     * 获取Session中的所有属性
     *
     * @param s HttpSession对象
     * @return 属性Map
     */
    private Map<String, Object> getSessionAttributes(HttpSession s) {
        Enumeration nameEnum = s.getAttributeNames();
        Map<String, Object> map = new HashMap<String, Object>();
        while (nameEnum.hasMoreElements()) {
            String key = (String) nameEnum.nextElement();
            Object value = s.getAttribute(key);
            if (null != value) {
                map.put(key, s.getAttribute(key));
            }
        }

        return map;
    }

    /**
     * 将属性填充到Session
     *
     * @param session HttpSession对象
     * @param map     属性Map
     */
    private void fillAttributesToSession(HttpSession session, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            session.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 获取请求中的认证Token
     * <p>
     * 从HTTP Header中提取x-access-token
     *
     * @return Token字符串，不存在时返回null
     */
    public String getAuthToken() {
        HttpServletRequest request = WebHelper.getHttpRequest();
        String authHeader = request.getHeader("x-access-token");
        return authHeader;
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
    public com.adminpro.system.rbac.domains.vo.jwt.JwtLoginResponse loginJwt(String userDomain, String loginName,
            String password, Device device, boolean rememberMe) throws APIException {
        String securityUsername = userDomain + "_" + loginName;
        // 1. 验证账户
        Authentication authentication = verifyAccount(userDomain, loginName, password);
        LoginUser userDetails = (LoginUser) userDetailsService.loadUserByUsername(securityUsername);

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

        String accessToken = jwtTokenProvider.createAccessToken(userDetails.getUserId(), claims);

        // 3. 存入 Access Token 白名单（使用与 JWT 一致的过期时间）
        String jti = jwtTokenProvider.getJti(accessToken);
        int accessTokenValidity = jwtProperties.getAccessTokenValidity(platform);
        // Ehcache key 是 jti，value 是 userId，expire 是过期时间（秒）
        AppCache.getInstance().set(com.adminpro.system.core.security.jwt.JwtCacheConstants.ACCESS_TOKEN_CACHE, jti,
                userDetails.getUserId(), accessTokenValidity);

        // 4. 生成 Refresh Token
        // 4. 生成 Refresh Token
        com.adminpro.system.rbac.domains.entity.jwt.RefreshTokenData rtData = new com.adminpro.system.rbac.domains.entity.jwt.RefreshTokenData();
        rtData.setUserId(userDetails.getUserId());
        rtData.setUserDomain(userDetails.getUserDomain());
        rtData.setLoginName(userDetails.getLoginName());
        rtData.setPlatform(platform);

        // 使用设备指纹生成稳定的 deviceId（同一设备多次登录相同）
        HttpServletRequest request = WebHelper.getHttpRequest();
        com.adminpro.system.core.security.jwt.DeviceFingerprintService fingerprintService =
            SpringUtil.getBean(com.adminpro.system.core.security.jwt.DeviceFingerprintService.class);

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
        return com.adminpro.system.rbac.domains.vo.jwt.JwtLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenValidity(platform))
                .user(com.adminpro.system.rbac.domains.vo.jwt.JwtLoginResponse.UserInfo.builder()
                        .id(userDetails.getUserId())
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
    public com.adminpro.system.rbac.domains.vo.jwt.JwtLoginResponse refreshJwt(String refreshToken)
            throws APIException {
        // 1. 验证 Refresh Token (获取数据)
        com.adminpro.system.rbac.domains.entity.jwt.RefreshTokenData data = refreshTokenService
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

        // 4. 生成新的 Access Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userDomain", userDetails.getUserDomain());
        claims.put("loginName", userDetails.getLoginName());
        claims.put("realName", userDetails.getRealName());
        claims.put("deptNo", userDetails.getDeptNo());
        String platform = data.getPlatform(); // 保持原有平台
        claims.put("aud", platform);
        claims.put("permissions", userDetails.getPermissions());

        String accessToken = jwtTokenProvider.createAccessToken(userDetails.getUserId(), claims);

        // 5. 存入 Access Token 白名单（使用与 JWT 一致的过期时间）
        String jti = jwtTokenProvider.getJti(accessToken);
        int accessTokenValidity = jwtProperties.getAccessTokenValidity(platform);
        AppCache.getInstance().set(com.adminpro.system.core.security.jwt.JwtCacheConstants.ACCESS_TOKEN_CACHE, jti,
                userDetails.getUserId(), accessTokenValidity);

        return com.adminpro.system.rbac.domains.vo.jwt.JwtLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenValidity(platform))
                .user(com.adminpro.system.rbac.domains.vo.jwt.JwtLoginResponse.UserInfo.builder()
                        .id(userDetails.getUserId())
                        .loginName(userDetails.getLoginName())
                        .realName(userDetails.getRealName())
                        .avatarUrl(userDetails.getUser().getAvatarUrl())
                        .build())
                .build();
    }
}
