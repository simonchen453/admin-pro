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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
     * 本地锁，用于防止同一个 Refresh Token 被并发刷新
     * Key: Refresh Token, Value: 锁标记
     */
    private final ConcurrentHashMap<String, Object> refreshLocks = new ConcurrentHashMap<>();

    /**
     * 创建 Refresh Token 并绑定设备
     */
    @Transactional(rollbackFor = Exception.class)
    public String createRefreshToken(RefreshTokenData data) {
        String token = "rt_" + UUID.randomUUID().toString().replace("-", "");

        // 1. 检查并限制设备数量
        enforceDeviceLimit(data.getUserId());

        // 2. 存入缓存
        int validitySeconds = jwtProperties.getRefreshTokenValidity(
                data.getPlatform(),
                data.isRememberMe());

        AppCache.getInstance().set(JwtCacheConstants.REFRESH_TOKEN_CACHE, token, data);

        // 3. 更新或创建设备记录
        UserDeviceEntity device = userDeviceDao.findByDeviceId(data.getUserId(), data.getDeviceId());
        if (device == null) {
            device = new UserDeviceEntity();
            device.setId(UUID.randomUUID().toString());
            device.setUserId(data.getUserId());
            device.setDeviceId(data.getDeviceId());
            device.setPlatform(data.getPlatform());
            device.setDeviceName(data.getDeviceName());
            device.setRefreshTokenJti(token);
            device.setLastIp(data.getIp());
            device.setLastUserAgent(data.getUserAgent());
            device.setLastActiveAt(LocalDateTime.now());
            device.setIsActive(1);
            userDeviceDao.insert(device);
        } else {
            // 设备已存在，更新信息
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
     * 强制执行设备数量限制
     * <p>
     * 如果用户的活跃设备数超过配置的最大值，踢出最久未使用的设备
     * </p>
     *
     * @param userId 用户ID
     */
    private void enforceDeviceLimit(String userId) {
        int maxDevices = jwtProperties.getMaxDevicesPerUser();

        // 获取用户的所有设备
        List<UserDeviceEntity> allDevices = userDeviceDao.findByUserId(userId);
        long activeDeviceCount = allDevices.stream()
                .filter(d -> d.getIsActive() != null && d.getIsActive() == 1)
                .count();

        // 如果未超限，直接返回
        if (activeDeviceCount < maxDevices) {
            return;
        }

        // 找出最久未使用的活跃设备
        allDevices.stream()
                .filter(d -> d.getIsActive() != null && d.getIsActive() == 1)
                .min(Comparator.comparing(UserDeviceEntity::getLastActiveAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .ifPresent(oldestDevice -> {
                    log.info("用户 {} 设备数超限({}>)，踢出最旧设备: {} ({})",
                            userId, activeDeviceCount, oldestDevice.getDeviceId(),
                            oldestDevice.getDeviceName());

                    // 撤销该设备的 Refresh Token
                    if (oldestDevice.getRefreshTokenJti() != null) {
                        AppCache.getInstance().delete(
                                JwtCacheConstants.REFRESH_TOKEN_CACHE,
                                oldestDevice.getRefreshTokenJti());
                    }

                    // 标记为不活跃
                    oldestDevice.setIsActive(0);
                    userDeviceDao.update(oldestDevice);
                });
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

        // 可以在这里检查是否已被撤销 (例如 check sys_user_device status)
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
     * 撤销用户所有 Refresh Token（用于单点登出）
     * <p>
     * 清除指定用户的所有设备的 Refresh Token
     * </p>
     *
     * @param userId 用户ID
     * @return 撤销的 Token 数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int revokeAllUserTokens(String userId) {
        // 获取用户的所有设备
        List<UserDeviceEntity> devices = userDeviceDao.findByUserId(userId);
        int revokedCount = 0;

        for (UserDeviceEntity device : devices) {
            if (device.getRefreshTokenJti() != null) {
                // 从缓存中删除 Refresh Token
                AppCache.getInstance().delete(
                        JwtCacheConstants.REFRESH_TOKEN_CACHE,
                        device.getRefreshTokenJti());

                // 清空设备的 Refresh Token JTI
                device.setRefreshTokenJti(null);
                device.setIsActive(0);
                userDeviceDao.update(device);

                revokedCount++;
            }
        }

        log.info("已撤销用户 {} 的 {} 个 Refresh Token", userId, revokedCount);
        return revokedCount;
    }

    /**
     * 撤销指定设备的 Refresh Token (踢出设备)
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void revokeByDeviceId(String userId, String deviceId) {
        UserDeviceEntity device = userDeviceDao.findByDeviceId(userId, deviceId);
        if (device != null && device.getRefreshTokenJti() != null) {
            // 1. 从缓存删除
            AppCache.getInstance().delete(
                    JwtCacheConstants.REFRESH_TOKEN_CACHE,
                    device.getRefreshTokenJti());

            // 2. 更新数据库状态
            device.setRefreshTokenJti(null);
            device.setIsActive(0);
            userDeviceDao.update(device);
        }
    }

    /**
     * 轮换 Token（带并发控制）
     * <p>
     * 使用本地锁防止同一个 Refresh Token 被并发刷新，
     * 避免旧 Token 被多次使用导致的竞态条件
     * </p>
     *
     * @param oldToken 旧的 Refresh Token
     * @return 新的 Refresh Token
     * @throws JwtAuthenticationException 如果 Token 无效
     */
    @Transactional(rollbackFor = Exception.class)
    public String rotateRefreshToken(String oldToken) {
        // 获取或创建该 Token 的锁对象
        Object lock = refreshLocks.computeIfAbsent(oldToken, k -> new Object());

        synchronized (lock) {
            try {
                // 双重检查：验证 Token 是否仍然有效
                // （可能已被其他线程撤销）
                RefreshTokenData data = validateRefreshToken(oldToken);
                if (data == null) {
                    throw new JwtAuthenticationException(JwtErrorCode.REFRESH_TOKEN_INVALID);
                }

                // 撤销旧的
                revokeRefreshToken(oldToken);

                // 生成新的
                data.setLastUsedAt(LocalDateTime.now());
                String newToken = createRefreshToken(data);

                // 清理锁（防止内存泄漏）
                refreshLocks.remove(oldToken);

                return newToken;
            } catch (JwtAuthenticationException e) {
                // 业务异常，清理锁后抛出
                refreshLocks.remove(oldToken);
                throw e;
            } catch (Exception e) {
                // 系统异常，清理锁后抛出
                refreshLocks.remove(oldToken);
                throw new JwtAuthenticationException(JwtErrorCode.INTERNAL_ERROR, e);
            }
        }
    }
}
