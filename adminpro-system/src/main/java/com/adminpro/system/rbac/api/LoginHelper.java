package com.adminpro.system.rbac.api;

import com.adminpro.framework.base.util.JsonUtil;
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
import com.adminpro.system.rbac.domains.entity.user.UserIden;
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
 * @author simon
 */
@Service
@Monitored
@Transactional(rollbackFor = Exception.class)
public class LoginHelper {

    Logger logger = LoggerFactory.getLogger(getClass());

    public static LoginHelper getInstance() {
        return SpringUtil.getBean(LoginHelper.class);
    }

    public static final String LOGIN_AUTH_USER_KEY = "http_login_authuser";

    @Autowired
    private AuthenticationManager authenticationmanager;

    @Autowired
    private AuthUserDetailServiceImpl userDetailsService;

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private UserService userService;

    public String login(UserIden userIden, String password, String userDomain) throws APIException {
        return login(userIden, password, userDomain, null);
    }

    public String login(UserIden userIden, String password, String userDomain, Device device) throws APIException {
        logger.info(MessageFormat.format("用户{0}尝试登陆{1}", userIden.toSecurityUsername(), userDomain));
        boolean isMobileRequest = ClientHelper.isMobileRequest(WebHelper.getHttpRequest());

        Authentication authentication = verifyAccount(userIden, password);

        final LoginUser userDetails = (LoginUser) userDetailsService.loadUserByUsername(userIden.toSecurityUsername());
        if (authentication == null || userDetails == null) {
            return "no_match";
        }
        if (StringHelper.equals(userDetails.getStatus(), UserStatus.LOCKED.getCode())) {
            logger.error("用户{0}, 账户锁定", userIden.toSecurityUsername());
            return "user_locked";
        }
        if (StringHelper.equals(userDetails.getStatus(), UserStatus.INACTIVE.getCode())) {
            logger.error("用户{0}, 账户停用", userIden.toSecurityUsername());
            return "user_inactive";
        }

        if (!StringUtils.equals(userDomain, userIden.getUserDomain())) {
            logger.error("用户{0}, 没有权限登陆", userIden.toSecurityUsername());
            return "no_privilege";
        }

        logger.info(MessageFormat.format("用户{0}登陆成功", userIden.getUserDomain() + "/" + userDetails.getLoginName()));
        if (isMobileRequest) {
            AuthToken principal = (AuthToken) authentication.getPrincipal();
            String token = principal.getToken();
            String deviceType = device != null ? tokenHelper.generateAudience(device) : TokenHelper.AUDIENCE_WEB;
            tokenHelper.generateToken(userIden, token, deviceType);
            UserEntity userEntity = UserService.getInstance().findByIden(userIden);
            userEntity.setLatestLoginTime(new Date());
            UserService.getInstance().update(userEntity);
            AuditLogHelper.log(AuditLogHelper.CATEGORY_ADMIN, "User Management", "login", "success", JsonUtil.toJson(userIden));
            return token;
        } else {
            HttpSession session = WebHelper.getHttpRequest().getSession();
            AuthToken principal = (AuthToken) authentication.getPrincipal();
            String token = principal.getToken();
            if (StringUtils.equals(token, "success")) {
                setUserAgent(userDetails);
                session.setAttribute(LOGIN_AUTH_USER_KEY, userDetails);
                renewSession(session, WebHelper.getHttpRequest());
                UserEntity userEntity = UserService.getInstance().findByIden(userIden);
                userEntity.setLatestLoginTime(new Date());
                UserService.getInstance().update(userEntity);
                loginUserSession(userDetails);
                AuditLogHelper.log(AuditLogHelper.CATEGORY_ADMIN, "User Management", "login", "success", JsonUtil.toJson(userIden));
            }
            return token;
        }
    }

    private Authentication verifyAccount(UserIden userIden, String password) throws APIException {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userIden.toSecurityUsername(), password);
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
     *
     * @param loginUser 登录信息
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

    private static void loginUserSession(LoginUser loginUser) {
        UserEntity user = loginUser.getUser();
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setUserDomain(user.getUserDomain());
        sessionEntity.setUserId(user.getUserId());
        sessionEntity.setDeptNo(user.getDeptNo());
        sessionEntity.setLoginName(user.getLoginName());
        sessionEntity.setStatus(SessionStatus.ACTIVE.getCode());
        sessionEntity.setSessionId(WebHelper.getSessionId());
        sessionEntity.setIpAddr(loginUser.getIpAddr());
        sessionEntity.setLoginLocation(loginUser.getLoginLocation());
        sessionEntity.setBrowser(loginUser.getBrowser());
        sessionEntity.setOs(loginUser.getOs());
        SessionService.getInstance().create(sessionEntity);
    }

    public boolean isInternetUser(String userDomain) {
        return StringUtils.equals(userDomain, RbacConstants.INTERNET_DOMAIN);
    }

    public boolean isSystemUser(String userDomain) {
        return StringUtils.equals(userDomain, RbacConstants.SYSTEM_DOMAIN);
    }

    public boolean isIntranetUser(String userDomain) {
        return StringUtils.equals(userDomain, RbacConstants.INTRANET_DOMAIN);
    }

    public boolean needCheckCapture(String userDomain) {
        return ArrayUtils.contains(RbacConstants.getNeedCheckCaptureDomains(), userDomain);
    }

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

    public String getLoginUserRealName() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getRealName();
        } else {
            return null;
        }
    }

    public String getLoginUserDeptNo() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getDeptNo();
        } else {
            return null;
        }
    }

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

    public UserIden getLoginUserIden() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getUserIden();
        } else {
            return null;
        }
    }

    public String getUserDomain() {
        UserIden loginUserIden = getLoginUserIden();
        if (loginUserIden != null) {
            return loginUserIden.getUserDomain();
        } else {
            return null;
        }
    }

    public String getUserId() {
        UserIden loginUserIden = getLoginUserIden();
        if (loginUserIden != null) {
            return loginUserIden.getUserId();
        } else {
            return null;
        }
    }

    public UserEntity getUserEntity() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getUser();
        } else {
            return null;
        }
    }

    public boolean isCurrentUser(UserIden userIden) {
        String userDomain = getUserDomain();
        String userId = getUserId();
        if (StringUtils.equals(userDomain, userIden.getUserDomain())
                && StringUtils.equals(userId, userIden.getUserId())) {
            return true;
        } else {
            return false;
        }
    }

    public String[] getPermissions() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            List<String> permissions = loginUser.getPermissions();
            return permissions.toArray(new String[permissions.size()]);
        } else {
            return new String[0];
        }
    }

    public boolean logout() {
        HttpServletRequest httpRequest = WebHelper.getHttpRequest();
        boolean isMobileRequest = ClientHelper.isMobileRequest(WebHelper.getHttpRequest());
        if (isMobileRequest) {
            String authToken = getAuthToken();
            UserTokenEntity userTokenEntity = TokenHelper.getInstance().deactiveToken(authToken);
            if (userTokenEntity != null) {
                AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, new UserIden(userTokenEntity.getUserDomain(), userTokenEntity.getUserId()).toSecurityUsername());
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

    private void fillAttributesToSession(HttpSession session, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            session.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    public String getAuthToken() {
        HttpServletRequest request = WebHelper.getHttpRequest();
        String authHeader = request.getHeader("x-access-token");
        return authHeader;
    }

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
