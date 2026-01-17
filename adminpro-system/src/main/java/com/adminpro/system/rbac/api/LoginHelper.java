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
            if (e instanceof BadCredentialsException) {
                throw new APIException("用户密码不匹配");
            } else {
                throw new APIException("登录失败");
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
     * 根据请求类型自动选择获取方式：
     * <ul>
     * <li>移动端：从SecurityContext中获取</li>
     * <li>Web端：从Session中获取</li>
     * </ul>
     *
     * @return 登录用户信息，未登录时返回null
     */
    public LoginUser getLoginUser() {
        HttpServletRequest httpRequest = WebHelper.getHttpRequest();
        if (httpRequest == null) {
            return null;
        }
        boolean isMobileRequest = ClientHelper.isMobileRequest(WebHelper.getHttpRequest());
        if (isMobileRequest) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof LoginUser) {
                    LoginUser authUser = (LoginUser) principal;
                    return authUser;
                }
            }
        } else {
            HttpSession session = httpRequest.getSession();
            LoginUser authUser = (LoginUser) session.getAttribute(LOGIN_AUTH_USER_KEY);
            return authUser;
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
     * 从Session中获取验证码并与用户输入进行比较。
     * 验证成功后自动清除Session中的验证码，防止重复使用。
     *
     * @param captcha 用户输入的验证码
     * @return true表示验证码正确，false表示错误或Session已失效
     */
    public boolean validCaptcha(String captcha) {
        HttpServletRequest request = WebHelper.getHttpRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.debug("验证码验证时 session 不存在");
            return false;
        }

        try {
            String c = (String) session.getAttribute(RbacCacheConstants.CAPTCHA_CACHE);
            logger.debug("session中的Captcha：" + c);
            logger.debug("用户输入的Captcha：" + captcha);
            boolean isValid = StringUtils.equalsIgnoreCase(captcha, c);

            // 验证后清除验证码，防止重复使用
            if (isValid) {
                session.removeAttribute(RbacCacheConstants.CAPTCHA_CACHE);
            }

            return isValid;
        } catch (IllegalStateException e) {
            logger.debug("验证码验证时 session 已失效");
            return false;
        }
    }
}
