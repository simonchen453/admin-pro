package com.adminpro.rbac.api;

import com.adminpro.core.base.util.JsonUtil;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.exceptions.APIException;
import com.adminpro.framework.cache.AppCache;
import com.adminpro.framework.common.helper.AuditLogHelper;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.framework.common.helper.ip.AddressUtils;
import com.adminpro.framework.common.helper.ip.IpUtils;
import com.adminpro.framework.security.auth.AuthToken;
import com.adminpro.framework.security.auth.AuthUserDetailServiceImpl;
import com.adminpro.framework.security.auth.LoginUser;
import com.adminpro.framework.security.auth.TokenHelper;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.rbac.common.RbacConstants;
import com.adminpro.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.rbac.domains.entity.dept.DeptService;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.rbac.domains.entity.user.UserIden;
import com.adminpro.rbac.domains.entity.user.UserService;
import com.adminpro.rbac.domains.entity.usertoken.UserTokenEntity;
import com.adminpro.rbac.enums.UserLoginPlatform;
import com.adminpro.rbac.enums.UserStatus;
import com.adminpro.tools.domains.entity.session.SessionEntity;
import com.adminpro.tools.domains.entity.session.SessionService;
import com.adminpro.tools.domains.enums.SessionStatus;
import eu.bitwalker.useragentutils.UserAgent;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.util.*;

import static com.adminpro.framework.common.helper.AuditLogHelper.CATEGORY_ADMIN;

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

    public String login(UserIden userIden, String password, String platform) throws APIException {
        return login(userIden, password, platform, null);
    }

    public String login(UserIden userIden, String password, String platform, Device device) throws APIException {
        logger.info(MessageFormat.format("用户{0}尝试登陆{1}", userIden.toSecurityUsername(), platform));
        boolean restRequest = WebHelper.isRestRequest();

        Authentication authentication = verifyAccount(userIden, password);

        final LoginUser userDetails = (LoginUser) userDetailsService.loadUserByUsername(userIden.toSecurityUsername());
        if (authentication == null || userDetails == null) {
            return "no_match";
        }
        if (StringHelper.equals(userDetails.getStatus(), UserStatus.NEW.getCode())) {
            logger.error("用户{0}, 账户等待激活", userIden.toSecurityUsername());
            return "pending_active";
        }
        if (StringHelper.equals(userDetails.getStatus(), UserStatus.LOCK.getCode())) {
            logger.error("用户{0}, 账户锁定", userIden.toSecurityUsername());
            return "user_locked";
        }

        if (UserLoginPlatform.INTERNET.getCode().equals(platform) && !isInternetUser(userIden.getUserDomain())
                || UserLoginPlatform.SYSTEM.getCode().equals(platform) && !isSystemUser(userIden.getUserDomain())) {
            logger.error("用户{0}, 没有权限登陆", userIden.toSecurityUsername());
            return "no_privilege";
        }

        logger.info(MessageFormat.format("用户{0}登陆成功", userIden.getUserDomain() + "/" + userDetails.getLoginName()));
//        UserTokenEntity userTokenEntity = tokenHelper.generateToken(userIden, device);
//        return userTokenEntity.getToken();
        if (restRequest) {
            AuthToken principal = (AuthToken) authentication.getPrincipal();
            String token = principal.getToken();
            tokenHelper.generateToken(userIden, token);
            UserEntity userEntity = UserService.getInstance().findById(userIden);
            userEntity.setLatestLoginTime(new Date());
            UserService.getInstance().update(userEntity);
            AuditLogHelper.log(CATEGORY_ADMIN, "User Management", "login", "success", JsonUtil.toJson(userIden));
            return token;
        } else {
            HttpSession session = WebHelper.getHttpRequest().getSession();
            AuthToken principal = (AuthToken) authentication.getPrincipal();
            String token = principal.getToken();
            if (StringUtils.equals(token, "success")) {
                setUserAgent(userDetails);
                session.setAttribute(LOGIN_AUTH_USER_KEY, userDetails);
                renewSession(session, WebHelper.getHttpRequest());
                UserEntity userEntity = UserService.getInstance().findById(userIden);
                userEntity.setLatestLoginTime(new Date());
                UserService.getInstance().update(userEntity);
                loginUserSession(userDetails);
                AuditLogHelper.log(CATEGORY_ADMIN, "User Management", "login", "success", JsonUtil.toJson(userIden));
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
        return ArrayUtils.contains(RbacConstants.NEED_CHECK_CAPTURE_DOMAINS, userDomain);
    }

    public LoginUser getLoginUser() {
        HttpServletRequest httpRequest = WebHelper.getHttpRequest();
        if (httpRequest == null) {
            return null;
        }
        boolean restRequest = WebHelper.isRestRequest();
        if (restRequest) {
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
        boolean restRequest = WebHelper.isRestRequest();
        if (restRequest) {
            String authToken = getAuthToken();
            UserTokenEntity userTokenEntity = TokenHelper.getInstance().deactiveToken(authToken);
            if (userTokenEntity != null) {
                AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, new UserIden(userTokenEntity.getUserDomain(), userTokenEntity.getUserId()).toSecurityUsername());
            }
            SecurityContextHolder.getContext().setAuthentication(null);
            return true;
        } else {
            HttpSession session = httpRequest.getSession();
            session.removeAttribute(LOGIN_AUTH_USER_KEY);
            session.removeAttribute(RbacCacheConstants.AUTH_USER_DETAIL_CACHE);
            SessionService.getInstance().invalid(session.getId());
            session.invalidate();
            return true;
        }
    }

    public void renewSession(HttpSession session, HttpServletRequest request) {
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
        HttpSession session = WebHelper.getHttpRequest().getSession();
        String c = (String) session.getAttribute(RbacCacheConstants.CAPTCHA_CACHE);
        logger.debug("session中的Captcha：" + c);
        logger.debug("用户输入的Captcha：" + captcha);
        return StringUtils.equalsIgnoreCase(captcha, c);
    }
}
