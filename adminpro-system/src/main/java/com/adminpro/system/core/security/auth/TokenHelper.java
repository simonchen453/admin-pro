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
 * Token助手类
 * <p>
 * 负责用户认证Token的完整生命周期管理，包括：
 * <ul>
 * <li>Token生成：支持多种设备类型（Web、移动端、平板）的Token生成</li>
 * <li>Token验证：验证Token的有效性和过期状态</li>
 * <li>Token刷新：在Token即将过期时自动延长有效期</li>
 * <li>Token注销：使Token失效，用于用户登出场景</li>
 * </ul>
 * <p>
 * 安全特性：
 * <ul>
 * <li>支持单点登录（SSO）配置：可通过配置踢出已登录用户</li>
 * <li>设备类型区分：不同设备类型的Token有不同的过期时间策略</li>
 * <li>Token持久化：所有Token存储在数据库中，便于管理和审计</li>
 * <li>自动刷新：验证通过后自动刷新Token过期时间</li>
 * </ul>
 *
 * @author simon
 * @see UserTokenEntity
 * @see Device
 */
@Component
public class TokenHelper implements Serializable {

    /**
     * 获取TokenHelper单例实例
     *
     * @return TokenHelper实例
     */
    public static TokenHelper getInstance() {
        return SpringUtil.getBean(TokenHelper.class);
    }

    /**
     * 未知设备类型
     */
    public static final String AUDIENCE_UNKNOWN = "unknown";

    /**
     * Web端设备类型
     */
    public static final String AUDIENCE_WEB = "web";

    /**
     * 移动端设备类型
     */
    public static final String AUDIENCE_MOBILE = "mobile";

    /**
     * 平板设备类型
     */
    public static final String AUDIENCE_TABLET = "tablet";

    /**
     * 默认过期时间：12小时（Web端）
     * <p>
     * 单位：秒
     */
    public final static int DEFAULT_EXPIRE_WEB = 12 * 60 * 60;

    /**
     * 移动端过期时间：30天（可配置）
     * <p>
     * 单位：秒，可通过配置项 app.token.expire.mobile 覆盖
     */
    public final static int DEFAULT_EXPIRE_MOBILE = 30 * 24 * 60 * 60;

    @Autowired
    private com.adminpro.system.rbac.domains.entity.user.UserService userService;

    @Autowired
    private UserTokenService userTokenService;

    /**
     * 日志记录器
     */
    private static Logger logger = LoggerFactory.getLogger(TokenHelper.class);

    /**
     * 通过Token获取用户ID（全局唯一主键）
     * <p>
     * 此方法会验证Token的有效性和过期状态，只有有效的Token才能获取到用户ID
     *
     * @param token 认证Token字符串
     * @return 用户ID（全局唯一主键），如Token无效或已过期则返回null
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
     * <p>
     * 此方法会验证Token的有效性和过期状态，只有有效的Token才能获取到用户实体
     *
     * @param token 认证Token字符串
     * @return 用户实体，如Token无效或已过期则返回null
     */
    public com.adminpro.system.rbac.domains.entity.user.UserEntity getUserByToken(String token) {
        String userId = getUserIdByToken(token);
        if (userId == null) {
            return null;
        }
        return userService.findById(userId);
    }

    /**
     * 通过Token获取Token实体
     * <p>
     * 此方法会验证Token的有效性，如Token无效则抛出异常
     *
     * @param token 认证Token字符串
     * @return Token实体
     * @throws InvalidAuthTokenException 当Token无效或已过期时抛出
     */
    public UserTokenEntity getByToken(String token) {
        UserTokenEntity byToken = userTokenService.findByToken(token);
        if (byToken == null || !byToken.isValid()) {
            throw new InvalidAuthTokenException(WebConstants.INVALID_AUTH_TOKEN_EXCEPTION);
        }
        return byToken;
    }

    /**
     * 判断Token是否过期（内部方法）
     * <p>
     * 注意：此方法不检查Token状态，只检查过期时间
     *
     * @param token 认证Token字符串
     * @return true表示已过期，false表示未过期
     */
    private Boolean isTokenExpired(String token) {
        UserTokenEntity byToken = userTokenService.findByToken(token);
        Date expireTime = byToken.getExpireTime();
        return expireTime.before(new Date());
    }

    /**
     * 根据设备类型生成设备类型标识字符串
     * <p>
     * 将Device对象转换为设备类型字符串（web/mobile/tablet）
     *
     * @param device 设备对象
     * @return 设备类型字符串，默认为web
     */
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
     * <p>
     * 此方法会自动生成Token字符串，并创建Token实体记录到数据库。
     * 如果启用了单点登录配置（KILL_SESSION_WHEN_LOGIN），会先使该用户的所有旧Token失效。
     *
     * @param userId 用户ID（全局唯一主键）
     * @param device 设备类型，用于确定Token过期时间
     * @return 新生成的Token实体
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
     * <p>
     * 此方法会先查找用户，然后调用generateTokenByUserId生成Token。
     * 通常在用户登录成功后调用此方法。
     *
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param device     设备类型，用于确定Token过期时间
     * @return 新生成的Token实体
     * @throws IllegalArgumentException 当用户不存在时抛出
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
     * <p>
     * 自动生成Token字符串，设备类型默认为Web端
     *
     * @param userId 用户ID（全局唯一主键）
     * @return 新生成的Token实体
     */
    @Transactional
    public UserTokenEntity generateTokenByUserId(String userId) {
        String token = TokenGenerator.generateValue();
        return generateTokenByUserId(userId, token);
    }

    /**
     * 通过用户ID和指定Token生成Token实体
     * <p>
     * 使用指定的Token字符串，设备类型默认为Web端
     *
     * @param userId 用户ID（全局唯一主键）
     * @param token  指定的Token字符串
     * @return 新生成的Token实体
     */
    @Transactional
    public UserTokenEntity generateTokenByUserId(String userId, String token) {
        return generateTokenByUserId(userId, token, AUDIENCE_WEB);
    }

    /**
     * 通过用户ID、指定Token和设备类型生成Token实体
     * <p>
     * 使用指定的Token字符串和设备类型生成Token实体
     *
     * @param userId     用户ID（全局唯一主键）
     * @param token      指定的Token字符串
     * @param deviceType 设备类型（web/mobile/tablet）
     * @return 新生成的Token实体
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
     * <p>
     * 自动生成Token字符串，设备类型默认为Web端
     *
     * @param userDomain 用户域
     * @param loginName  登录名
     * @return 新生成的Token实体
     * @throws IllegalArgumentException 当用户不存在时抛出
     */
    @Transactional
    public UserTokenEntity generateToken(String userDomain, String loginName) {
        String token = TokenGenerator.generateValue();
        return generateToken(userDomain, loginName, token);
    }

    /**
     * 通过用户域、登录名和指定Token生成Token实体
     * <p>
     * 使用指定的Token字符串，设备类型默认为Web端
     *
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param token      指定的Token字符串
     * @return 新生成的Token实体
     * @throws IllegalArgumentException 当用户不存在时抛出
     */
    @Transactional
    public UserTokenEntity generateToken(String userDomain, String loginName, String token) {
        return generateToken(userDomain, loginName, token, AUDIENCE_WEB);
    }

    /**
     * 通过用户域、登录名、指定Token和设备类型生成Token实体
     * <p>
     * 使用指定的Token字符串和设备类型生成Token实体
     *
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param token      指定的Token字符串
     * @param deviceType 设备类型（web/mobile/tablet）
     * @return 新生成的Token实体
     * @throws IllegalArgumentException 当用户不存在时抛出
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

    /**
     * 验证Token是否有效
     * <p>
     * 验证内容包括：
     * <ul>
     * <li>Token是否存在</li>
     * <li>Token是否过期</li>
     * <li>Token是否处于活动状态</li>
     * <li>Token所属用户是否匹配</li>
     * </ul>
     * <p>
     * 验证通过后会自动刷新Token的过期时间
     *
     * @param token    认证Token字符串
     * @param authUser 登录用户信息
     * @return true表示Token有效，false表示Token无效
     */
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

    /**
     * 刷新Token过期时间
     * <p>
     * 根据Token的设备类型重新计算过期时间，并更新到数据库。
     * 通常在验证通过后自动调用，以延长Token的有效期。
     *
     * @param token 认证Token字符串
     * @return 刷新后的Token实体
     */
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
     * <p>
     * 移动端和平板设备使用较长的过期时间（默认30天），
     * Web端使用较短的过期时间（默认12小时）。
     * 可通过配置文件覆盖默认值。
     *
     * @param deviceType 设备类型（web/mobile/tablet）
     * @return 过期时间（秒）
     */
    public int getExpireSeconds(String deviceType) {
        if (AUDIENCE_MOBILE.equals(deviceType) || AUDIENCE_TABLET.equals(deviceType)) {
            return ConfigHelper.getInt("app.token.expire.mobile", DEFAULT_EXPIRE_MOBILE);
        }
        return ConfigHelper.getInt("app.token.expire.web", DEFAULT_EXPIRE_WEB);
    }

    /**
     * 使Token失效（用户登出）
     * <p>
     * 将Token的状态设置为失效状态，用于用户主动登出场景。
     * 失效后的Token无法再次通过验证。
     *
     * @param token 认证Token字符串
     * @return 失效的Token实体
     * @throws LogoutException 当Token为空或不存在时抛出
     */
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
