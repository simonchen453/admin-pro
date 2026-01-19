package com.adminpro.system.core.cache;

import com.adminpro.system.rbac.api.LoginHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * 当前用户会话缓存工具类
 * <p>
 * 基于 JTI (JWT Token ID) 的会话级缓存，用于存储当前登录会话的临时数据。
 * Token 刷新时自动迁移数据，登出时自动清理。
 * </p>
 *
 * <h3>使用示例</h3>
 * 
 * <pre>{@code
 * // 存储会话数据
 * CurrentUserCache.set("cart", cartItems, 3600);
 *
 * // 读取缓存
 * List<CartItem> cart = CurrentUserCache.get("cart", List.class);
 *
 * // 删除缓存
 * CurrentUserCache.delete("cart");
 * }</pre>
 *
 * <h3>Key 格式</h3>
 * <ul>
 * <li>数据 Key: <code>user:{userId}:session:{jti}:{suffix}</code></li>
 * <li>索引 Key: <code>user:{userId}:session:{jti}:_keys_</code></li>
 * </ul>
 *
 * @author simon
 * @since 1.0.0
 * @see AppCache
 */
public class CurrentUserCache {

    private static final Logger log = LoggerFactory.getLogger(CurrentUserCache.class);

    /**
     * 用户数据缓存区域名称
     */
    public static final String USER_CACHE_REGION = "user:data";

    /**
     * 会话缓存索引 key 后缀
     */
    private static final String INDEX_SUFFIX = "_keys_";

    // ==================== 便捷 CRUD 方法 ====================

    /**
     * 设置会话级缓存
     *
     * @param suffix        key 后缀
     * @param value         缓存值
     * @param expireSeconds 过期时间（秒）
     */
    public static void set(String suffix, Object value, int expireSeconds) {
        String key = buildKey(suffix);
        AppCache.getInstance().set(USER_CACHE_REGION, key, value, expireSeconds);
        addToIndex(key, expireSeconds);
        log.debug("设置会话缓存: key={}, expire={}s", key, expireSeconds);
    }

    /**
     * 设置会话级缓存（使用默认过期时间）
     *
     * @param suffix key 后缀
     * @param value  缓存值
     */
    public static void set(String suffix, Object value) {
        String key = buildKey(suffix);
        AppCache.getInstance().set(USER_CACHE_REGION, key, value);
        addToIndex(key, 0);
        log.debug("设置会话缓存: key={}", key);
    }

    /**
     * 获取会话级缓存
     *
     * @param suffix key 后缀
     * @param clazz  值类型
     * @param <T>    返回类型
     * @return 缓存值，不存在则返回 null
     */
    public static <T> T get(String suffix, Class<T> clazz) {
        String key = buildKey(suffix);
        return AppCache.getInstance().get(USER_CACHE_REGION, key, clazz);
    }

    /**
     * 删除会话级缓存
     *
     * @param suffix key 后缀
     */
    public static void delete(String suffix) {
        String key = buildKey(suffix);
        AppCache.getInstance().delete(USER_CACHE_REGION, key);
        removeFromIndex(key);
        log.debug("删除会话缓存: key={}", key);
    }

    // ==================== 清理方法 ====================

    /**
     * 清除当前会话的所有缓存
     * <p>
     * 在用户 logout 时调用。
     * </p>
     *
     * @return 清理的缓存数量
     */
    @SuppressWarnings("unchecked")
    public static int clearCurrentSession() {
        String userId = getCurrentUserIdSafe();
        String jti = getCurrentJti();

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(jti)) {
            log.debug("当前用户未登录或无法获取 JTI，跳过缓存清理");
            return 0;
        }

        String indexKey = buildIndexKey(userId, jti);
        Set<String> keys = AppCache.getInstance().get(USER_CACHE_REGION, indexKey, Set.class);

        if (keys == null || keys.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (String key : keys) {
            try {
                AppCache.getInstance().delete(USER_CACHE_REGION, key);
                count++;
            } catch (Exception e) {
                log.warn("删除缓存 {} 失败: {}", key, e.getMessage());
            }
        }

        // 删除索引本身
        AppCache.getInstance().delete(USER_CACHE_REGION, indexKey);

        log.info("已清理会话 {} 的 {} 个缓存", jti, count);
        return count;
    }

    // ==================== 会话迁移方法 ====================

    /**
     * 迁移会话级缓存（AT 刷新时调用）
     *
     * @param userId        用户ID
     * @param oldJti        旧的 JWT Token ID
     * @param newJti        新的 JWT Token ID
     * @param newTtlSeconds 新的 TTL（秒）
     * @return 迁移的缓存数量
     */
    @SuppressWarnings("unchecked")
    public static int migrateSession(String userId, String oldJti, String newJti, int newTtlSeconds) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(oldJti) || StringUtils.isBlank(newJti)) {
            return 0;
        }

        if (oldJti.equals(newJti)) {
            return 0;
        }

        // 旧的索引和前缀
        String oldIndexKey = buildIndexKey(userId, oldJti);
        String oldPrefix = String.format("user:%s:session:%s:", userId, oldJti);
        String newPrefix = String.format("user:%s:session:%s:", userId, newJti);

        Set<String> oldKeys = AppCache.getInstance().get(USER_CACHE_REGION, oldIndexKey, Set.class);
        if (oldKeys == null || oldKeys.isEmpty()) {
            return 0;
        }

        // 新的索引
        String newIndexKey = buildIndexKey(userId, newJti);
        Set<String> newKeys = new HashSet<>();
        int count = 0;

        for (String oldKey : oldKeys) {
            if (oldKey.startsWith(oldPrefix)) {
                try {
                    Object value = AppCache.getInstance().get(USER_CACHE_REGION, oldKey, Object.class);
                    if (value != null) {
                        String suffix = oldKey.substring(oldPrefix.length());
                        String newKey = newPrefix + suffix;
                        AppCache.getInstance().set(USER_CACHE_REGION, newKey, value, newTtlSeconds);
                        newKeys.add(newKey);
                        AppCache.getInstance().delete(USER_CACHE_REGION, oldKey);
                        count++;
                    }
                } catch (Exception e) {
                    log.warn("迁移缓存失败 {}: {}", oldKey, e.getMessage());
                }
            }
        }

        // 删除旧索引，创建新索引
        AppCache.getInstance().delete(USER_CACHE_REGION, oldIndexKey);
        if (!newKeys.isEmpty()) {
            AppCache.getInstance().set(USER_CACHE_REGION, newIndexKey, newKeys, newTtlSeconds);
        }

        log.info("会话缓存迁移完成: {} -> {}, 迁移 {} 个", oldJti, newJti, count);
        return count;
    }

    // ==================== 内部方法 ====================

    /**
     * 构建会话级缓存 key
     */
    private static String buildKey(String suffix) {
        String userId = getCurrentUserId();
        String jti = getCurrentJti();
        if (StringUtils.isBlank(jti)) {
            throw new IllegalStateException("无法获取当前会话的 JTI");
        }
        return String.format("user:%s:session:%s:%s", userId, jti, suffix);
    }

    /**
     * 构建会话索引 key
     */
    private static String buildIndexKey(String userId, String jti) {
        return String.format("user:%s:session:%s:%s", userId, jti, INDEX_SUFFIX);
    }

    /**
     * 将 key 添加到当前会话的索引中
     */
    @SuppressWarnings("unchecked")
    private static void addToIndex(String key, int expireSeconds) {
        String userId = getCurrentUserId();
        String jti = getCurrentJti();
        if (StringUtils.isBlank(jti)) {
            return;
        }

        String indexKey = buildIndexKey(userId, jti);
        Set<String> keys = AppCache.getInstance().get(USER_CACHE_REGION, indexKey, Set.class);
        if (keys == null) {
            keys = new HashSet<>();
        }
        keys.add(key);

        // 索引的过期时间与缓存数据一致
        if (expireSeconds > 0) {
            AppCache.getInstance().set(USER_CACHE_REGION, indexKey, keys, expireSeconds);
        } else {
            AppCache.getInstance().set(USER_CACHE_REGION, indexKey, keys);
        }
    }

    /**
     * 从当前会话的索引中移除 key
     */
    @SuppressWarnings("unchecked")
    private static void removeFromIndex(String key) {
        String userId = getCurrentUserId();
        String jti = getCurrentJti();
        if (StringUtils.isBlank(jti)) {
            return;
        }

        String indexKey = buildIndexKey(userId, jti);
        Set<String> keys = AppCache.getInstance().get(USER_CACHE_REGION, indexKey, Set.class);
        if (keys != null) {
            keys.remove(key);
            if (keys.isEmpty()) {
                AppCache.getInstance().delete(USER_CACHE_REGION, indexKey);
            } else {
                AppCache.getInstance().set(USER_CACHE_REGION, indexKey, keys);
            }
        }
    }

    private static String getCurrentUserId() {
        String userId = LoginHelper.getInstance().getLoginUserId();
        if (StringUtils.isBlank(userId)) {
            throw new IllegalStateException("用户未登录");
        }
        return userId;
    }

    private static String getCurrentUserIdSafe() {
        return LoginHelper.getInstance().getLoginUserId();
    }

    private static String getCurrentJti() {
        return LoginHelper.getInstance().getCurrentJti();
    }
}
