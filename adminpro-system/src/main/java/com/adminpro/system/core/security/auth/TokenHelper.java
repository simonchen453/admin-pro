package com.adminpro.system.core.security.auth;

import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.exceptions.LogoutException;
import com.adminpro.system.core.common.constants.WebConstants;
import com.adminpro.system.core.common.helper.ConfigHelper;
import com.adminpro.system.core.exceptions.InvalidAuthTokenException;
import com.adminpro.system.rbac.api.Device;
import com.adminpro.system.rbac.common.RbacConstants;
import com.adminpro.system.rbac.domains.entity.usertoken.UserTokenEntity;
import com.adminpro.system.rbac.domains.entity.usertoken.UserTokenService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

/**
 * Token 助手类
 * 用于生成、验证和管理用户认证Token
 */
@Component
public class TokenHelper implements Serializable {

    public static TokenHelper getInstance() {
        return SpringUtil.getBean(TokenHelper.class);
    }

    public static final String AUDIENCE_UNKNOWN = "unknown";
    public static final String AUDIENCE_WEB = "web";
    public static final String AUDIENCE_MOBILE = "mobile";
    public static final String AUDIENCE_TABLET = "tablet";

    // 默认过期时间：12小时（Web端）
    public final static int DEFAULT_EXPIRE_WEB = 12 * 60 * 60;
    // 移动端过期时间：30天（可配置）
    public final static int DEFAULT_EXPIRE_MOBILE = 30 * 24 * 60 * 60;

    @Autowired
    private com.adminpro.system.rbac.domains.entity.user.UserService userService;

    @Autowired
    private UserTokenService userTokenService;

    private static Logger logger = LoggerFactory.getLogger(TokenHelper.class);

    /**
     * 通过Token获取用户ID
     * 
     * @param token 认证Token
     * @return 用户ID（全局唯一主键），如无效则返回null
     */
    public String getUserIdByToken(String token) {
        UserTokenEntity byToken = userTokenService.findByToken(token);
        if (byToken == null || !byToken.isValid()) {
            logger.info(WebConstants.INVALID_AUTH_TOKEN_EXCEPTION);
            return null;
        }
        return byToken.getUserId();
    }

    /**
     * 通过Token获取用户实体
     * 
     * @param token 认证Token
     * @return 用户实体，如无效则返回null
     */
    public com.adminpro.system.rbac.domains.entity.user.UserEntity getUserByToken(String token) {
        String userId = getUserIdByToken(token);
        if (userId == null) {
            return null;
        }
        return userService.findById(userId);
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

    public String generateAudience(Device device) {
        if (device == null) {
            return AUDIENCE_WEB;
        }
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

    /**
     * 通过用户ID生成Token
     * 
     * @param userId 用户ID（全局唯一主键）
     * @param device 设备类型
     * @return Token实体
     */
    @Transactional
    public UserTokenEntity generateTokenByUserId(String userId, Device device) {
        String token = TokenGenerator.generateValue();
        Date now = new Date();
        boolean deactive = ConfigHelper.getBoolean(RbacConstants.KILL_SESSION_WHEN_LOGIN, false);
        if (deactive) {
            userTokenService.inactiveByUserId(userId);
        }
        UserTokenEntity userTokenEntity = new UserTokenEntity();
        userTokenEntity.setUserId(userId);
        userTokenEntity.setToken(token);
        String deviceType = generateAudience(device);
        userTokenEntity.setDevice(deviceType);
        userTokenEntity.setStatus(UserTokenEntity.STATUS_ACTIVITY);
        int expireSeconds = getExpireSeconds(deviceType);
        Date expireTime = DateUtils.addSeconds(now, expireSeconds);
        userTokenEntity.setExpireTime(expireTime);
        userTokenEntity.setUpdateTime(now);
        userTokenService.create(userTokenEntity);
        return userTokenEntity;
    }

    /**
     * 通过用户域和登录名生成Token（用于登录场景）
     * 
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param device     设备类型
     * @return Token实体
     */
    @Transactional
    public UserTokenEntity generateToken(String userDomain, String loginName, Device device) {
        com.adminpro.system.rbac.domains.entity.user.UserEntity user = userService
                .findByUserDomainAndLoginName(userDomain, loginName);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在: " + userDomain + "/" + loginName);
        }
        return generateTokenByUserId(user.getId(), device);
    }

    /**
     * 通过用户ID生成Token（Web端）
     */
    @Transactional
    public UserTokenEntity generateTokenByUserId(String userId) {
        String token = TokenGenerator.generateValue();
        return generateTokenByUserId(userId, token);
    }

    /**
     * 通过用户ID和指定Token生成Token实体
     */
    @Transactional
    public UserTokenEntity generateTokenByUserId(String userId, String token) {
        return generateTokenByUserId(userId, token, AUDIENCE_WEB);
    }

    /**
     * 通过用户ID、指定Token和设备类型生成Token实体
     */
    @Transactional
    public UserTokenEntity generateTokenByUserId(String userId, String token, String deviceType) {
        Date now = new Date();
        boolean deactive = ConfigHelper.getBoolean(RbacConstants.KILL_SESSION_WHEN_LOGIN, false);
        if (deactive) {
            userTokenService.inactiveByUserId(userId);
        }
        UserTokenEntity userTokenEntity = new UserTokenEntity();
        userTokenEntity.setUserId(userId);
        userTokenEntity.setToken(token);
        userTokenEntity.setDevice(deviceType != null ? deviceType : AUDIENCE_WEB);
        userTokenEntity.setStatus(UserTokenEntity.STATUS_ACTIVITY);
        int expireSeconds = getExpireSeconds(deviceType);
        Date expireTime = DateUtils.addSeconds(now, expireSeconds);
        userTokenEntity.setExpireTime(expireTime);
        userTokenEntity.setUpdateTime(now);
        userTokenService.create(userTokenEntity);
        return userTokenEntity;
    }

    /**
     * 通过用户域和登录名生成Token（Web端，用于登录场景）
     */
    @Transactional
    public UserTokenEntity generateToken(String userDomain, String loginName) {
        String token = TokenGenerator.generateValue();
        return generateToken(userDomain, loginName, token);
    }

    /**
     * 通过用户域、登录名和指定Token生成Token实体
     */
    @Transactional
    public UserTokenEntity generateToken(String userDomain, String loginName, String token) {
        return generateToken(userDomain, loginName, token, AUDIENCE_WEB);
    }

    /**
     * 通过用户域、登录名、指定Token和设备类型生成Token实体
     */
    @Transactional
    public UserTokenEntity generateToken(String userDomain, String loginName, String token, String deviceType) {
        com.adminpro.system.rbac.domains.entity.user.UserEntity user = userService
                .findByUserDomainAndLoginName(userDomain, loginName);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在: " + userDomain + "/" + loginName);
        }
        return generateTokenByUserId(user.getId(), token, deviceType);
    }

    public Boolean validateToken(String token, LoginUser authUser) {
        UserTokenEntity byToken = userTokenService.findByToken(token);
        String userId = byToken.getUserId();
        Date expireTime = byToken.getExpireTime();
        if (StringUtils.equals(userId, authUser.getUserId())
                && !expireTime.before(new Date())
                && StringUtils.equals(byToken.getStatus(), UserTokenEntity.STATUS_ACTIVITY)) {
            refreshToken(token);
            return true;
        } else {
            return false;
        }
    }

    public UserTokenEntity refreshToken(String token) {
        UserTokenEntity byToken = userTokenService.findByToken(token);
        Date now = new Date();
        String deviceType = byToken.getDevice();
        int expireSeconds = getExpireSeconds(deviceType);
        Date expireTime = DateUtils.addSeconds(now, expireSeconds);
        byToken.setExpireTime(expireTime);
        userTokenService.update(byToken);
        return byToken;
    }

    /**
     * 根据设备类型获取Token过期时间（秒）
     */
    public int getExpireSeconds(String deviceType) {
        if (AUDIENCE_MOBILE.equals(deviceType) || AUDIENCE_TABLET.equals(deviceType)) {
            return ConfigHelper.getInt("app.token.expire.mobile", DEFAULT_EXPIRE_MOBILE);
        }
        return ConfigHelper.getInt("app.token.expire.web", DEFAULT_EXPIRE_WEB);
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
