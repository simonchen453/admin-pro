package com.adminpro.framework.security.auth;


import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.framework.common.constants.WebConstants;
import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.exceptions.InvalidAuthTokenException;
import com.adminpro.framework.exceptions.LogoutException;
import com.adminpro.rbac.common.RbacConstants;
import com.adminpro.rbac.domains.entity.user.UserIden;
import com.adminpro.rbac.domains.entity.usertoken.UserTokenEntity;
import com.adminpro.rbac.domains.entity.usertoken.UserTokenService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

@Component
public class TokenHelper implements Serializable {

    // spring-mobile-device 已停止维护，使用简单的 Device 接口
    interface Device {
        boolean isNormal();
        boolean isMobile();
        boolean isTablet();
    }

    public static TokenHelper getInstance() {
        return SpringUtil.getBean(TokenHelper.class);
    }

    public static final String AUDIENCE_UNKNOWN = "unknown";
    public static final String AUDIENCE_WEB = "web";
    public static final String AUDIENCE_MOBILE = "mobile";
    public static final String AUDIENCE_TABLET = "tablet";

    //12小时后过期
    public final static int EXPIRE = 12 * 60 * 60;

    @Autowired
    private UserTokenService userTokenService;

    private static Logger logger = LoggerFactory.getLogger(TokenHelper.class);

    public UserIden getUserIdenByToken(String token) {
        UserTokenEntity byToken = userTokenService.findByToken(token);
        if (byToken == null || !byToken.isValid()) {
            //throw new InvalidAuthTokenException(WebConstants.INVALID_AUTH_TOKEN_EXCEPTION);
            logger.info(WebConstants.INVALID_AUTH_TOKEN_EXCEPTION);
            return null;
        }

        return new UserIden(byToken.getUserDomain(), byToken.getUserId());
    }

    public UserTokenEntity getByToken(String token) {
        UserTokenEntity byToken = userTokenService.findByToken(token);
        if (byToken == null || !byToken.isValid()) {
            throw new InvalidAuthTokenException(WebConstants.INVALID_AUTH_TOKEN_EXCEPTION);
        }

        return byToken;
    }

    private Boolean isTokenExpired(String token) {
        UserTokenEntity byToken = userTokenService.findByToken(token);
        Date expireTime = byToken.getExpireTime();
        return expireTime.before(new Date());
    }

    private String generateAudience(Device device) {
        String audience = AUDIENCE_UNKNOWN;
        if (device.isNormal()) {
            audience = AUDIENCE_WEB;
        } else if (device.isTablet()) {
            audience = AUDIENCE_TABLET;
        } else if (device.isMobile()) {
            audience = AUDIENCE_MOBILE;
        }
        return audience;
    }

    @Transactional
    public UserTokenEntity generateToken(UserIden userIden, Device device) {
        String token = TokenGenerator.generateValue();
        Date now = new Date();
        boolean deactive = ConfigHelper.getBoolean(RbacConstants.KILL_SESSION_WHEN_LOGIN, false);
        if (deactive) {
            userTokenService.inactive(userIden);
        }
        UserTokenEntity userTokenEntity = new UserTokenEntity();
        userTokenEntity.setUserDomain(userIden.getUserDomain());
        userTokenEntity.setUserId(userIden.getUserId());
        userTokenEntity.setToken(token);
        userTokenEntity.setDevice(generateAudience(device));
        userTokenEntity.setStatus(UserTokenEntity.STATUS_ACTIVITY);
        Date expireTime = DateUtils.addSeconds(now, EXPIRE);
        userTokenEntity.setExpireTime(expireTime);
        userTokenEntity.setUpdateTime(now);
        userTokenService.create(userTokenEntity);
        return userTokenEntity;
    }

    @Transactional
    public UserTokenEntity generateToken(UserIden userIden) {
        String token = TokenGenerator.generateValue();
        return generateToken(userIden, token);
    }

    @Transactional
    public UserTokenEntity generateToken(UserIden userIden, String token) {
        Date now = new Date();
        boolean deactive = ConfigHelper.getBoolean(RbacConstants.KILL_SESSION_WHEN_LOGIN, false);
        if (deactive) {
            userTokenService.inactive(userIden);
        }
        UserTokenEntity userTokenEntity = new UserTokenEntity();
        userTokenEntity.setUserDomain(userIden.getUserDomain());
        userTokenEntity.setUserId(userIden.getUserId());
        userTokenEntity.setToken(token);
//        userTokenEntity.setDevice(generateAudience(device));
        userTokenEntity.setStatus(UserTokenEntity.STATUS_ACTIVITY);
        Date expireTime = DateUtils.addSeconds(now, EXPIRE);
        userTokenEntity.setExpireTime(expireTime);
        userTokenEntity.setUpdateTime(now);
        userTokenService.create(userTokenEntity);
        return userTokenEntity;
    }

    public Boolean validateToken(String token, LoginUser authUser) {
        UserTokenEntity byToken = userTokenService.findByToken(token);
        String userDomain = byToken.getUserDomain();
        String userId = byToken.getUserId();
        Date expireTime = byToken.getExpireTime();
        if (StringUtils.equals(userDomain, authUser.getUserDomain()) && StringUtils.equals(userId, authUser.getUsername())
                && !expireTime.before(new Date()) && StringUtils.equals(byToken.getStatus(), UserTokenEntity.STATUS_ACTIVITY)) {
            refreshToken(token);
            return true;
        } else {
            return false;
        }
    }

    public UserTokenEntity refreshToken(String token) {
        UserTokenEntity byToken = userTokenService.findByToken(token);
        Date now = new Date();
        Date expireTime = DateUtils.addSeconds(now, EXPIRE);
        byToken.setExpireTime(expireTime);
        userTokenService.update(byToken);
        return byToken;
    }

    public UserTokenEntity deactiveToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new LogoutException("非法Token");
        }
        UserTokenEntity tokenEntity = userTokenService.findByToken(token);
        if (tokenEntity != null) {
            userTokenService.inactive(tokenEntity);
            return tokenEntity;
        } else {
            throw new LogoutException("非法Token");
        }

    }
}
