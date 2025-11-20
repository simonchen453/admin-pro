package com.adminpro.framework.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * 基于 Spring Cache Simple 实现的缓存工具类
 * 当 spring.cache.type=simple 时使用
 */
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "simple", matchIfMissing = false)
@Component
public class AppSimpleCache extends AppCache {

    @Autowired
    private CacheManager cacheManager;

    /**
     * 默认过期时长，单位：秒（Simple 缓存不支持过期，此参数仅用于兼容接口）
     */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;

    private Cache getCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new RuntimeException("缓存 " + cacheName + " 不存在，请检查配置");
        }
        return cache;
    }

    @Override
    public void set(String cacheName, String key, Object value) {
        Cache cache = getCache(cacheName);
        cache.put(key, value);
    }

    @Override
    public void set(String cacheName, String key, Object value, Integer expire) {
        // Simple 缓存不支持过期时间，忽略 expire 参数
        set(cacheName, key, value);
    }

    @Override
    public <T> T get(String cacheName, String key, Class<T> clazz) {
        Cache cache = getCache(cacheName);
        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper != null) {
            Object value = wrapper.get();
            if (value != null && clazz.isAssignableFrom(value.getClass())) {
                return clazz.cast(value);
            }
        }
        return null;
    }

    @Override
    public void delete(String cacheName, String key) {
        Cache cache = getCache(cacheName);
        cache.evict(key);
    }
}

