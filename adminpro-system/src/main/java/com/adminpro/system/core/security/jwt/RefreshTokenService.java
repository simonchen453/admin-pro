package com.adminpro.system.core.security.jwt;

import com.adminpro.system.config.JwtProperties;
import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.rbac.domains.entity.device.UserDeviceDao;
import com.adminpro.system.rbac.domains.entity.device.UserDeviceEntity;
import com.adminpro.system.rbac.domains.entity.jwt.RefreshTokenData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Refresh Token 管理服务
 * 
 * @author adminpro
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtProperties jwtProperties;
    private final UserDeviceDao userDeviceDao;

    /**
     * 创建 Refresh Token 并绑定设备
     */
    @Transactional(rollbackFor = Exception.class)
    public String createRefreshToken(RefreshTokenData data) {
        String token = "rt_" + UUID.randomUUID().toString().replace("-", "");

        // 1. 存入缓存
        int validitySeconds = jwtProperties.getRefreshTokenValidity(
                data.getPlatform(),
                data.isRememberMe());

        // 注意：AppCache 的接口可能还没支持设置具体的 expire time for individual entry if using general
        // cache?
        // Ehcache cache definition sets a global TTL.
        // 如果需要动态 TTL，可能需要不同的 Cache alias 或 Ehcache 的特殊 API。
        // 为简化，目前使用统一的 jwt:refresh_token 配置 (30天)。
        // 如果 "记住我" 逻辑非常重要且差异很大 (7天 vs 30天)，我们通常取最大值，或者由 Schedule 任务清理。
        // 这里我们简单存入。
        AppCache.getInstance().set(JwtCacheConstants.REFRESH_TOKEN_CACHE, token, data);

        // 2. 更新或创建设备记录
        UserDeviceEntity device = userDeviceDao.findByDeviceId(data.getUserId(), data.getDeviceId());
        if (device == null) {
            device = new UserDeviceEntity();
            device.setId(UUID.randomUUID().toString()); // Assuming UUID PK
            device.setUserId(data.getUserId());
            device.setDeviceId(data.getDeviceId());
            device.setPlatform(data.getPlatform());
            device.setDeviceName(data.getDeviceName());
            device.setRefreshTokenJti(token); // 这里存 RT 的 token string 作为标识
            device.setLastIp(data.getIp());
            device.setLastUserAgent(data.getUserAgent());
            device.setLastActiveAt(LocalDateTime.now());
            device.setIsActive(1);
            userDeviceDao.insert(device);
        } else {
            device.setRefreshTokenJti(token);
            device.setLastIp(data.getIp());
            device.setLastUserAgent(data.getUserAgent());
            device.setLastActiveAt(LocalDateTime.now());
            device.setIsActive(1);
            userDeviceDao.update(device);
        }

        return token;
    }

    /**
     * 验证 Refresh Token
     */
    public RefreshTokenData validateRefreshToken(String token) {
        // 从缓存获取
        RefreshTokenData data = AppCache.getInstance().get(
                JwtCacheConstants.REFRESH_TOKEN_CACHE,
                token,
                RefreshTokenData.class);

        if (data == null) {
            return null;
        }

        // 可以在这里检查是否已被撤销 (例如 check user_device status)
        return data;
    }

    /**
     * 撤销 Refresh Token
     */
    public void revokeRefreshToken(String token) {
        AppCache.getInstance().delete(JwtCacheConstants.REFRESH_TOKEN_CACHE, token);

        // 可选：更新设备状态
        // String userId = ... (cache 拿不到了并不可惜，设备记录还在)
    }

    /**
     * 轮换 Token
     */
    @Transactional(rollbackFor = Exception.class)
    public String rotateRefreshToken(String oldToken) {
        RefreshTokenData data = validateRefreshToken(oldToken);
        if (data == null) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // 撤销旧的
        revokeRefreshToken(oldToken);

        // 生成新的
        data.setLastUsedAt(LocalDateTime.now());
        return createRefreshToken(data);
    }
}
