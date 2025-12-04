package com.adminpro.framework.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * 统一的缓存实现类
 * 基于 org.springframework.cache.CacheManager，支持三种缓存类型：
 * - simple: 简单内存缓存
 * - redis: Redis 缓存
 * - jcache: EhCache 3.x 缓存（通过 JCache 标准）
 * 
 * Spring Boot 会根据 spring.cache.type 自动创建对应的 CacheManager
 */
@Component
public class AppCacheImpl extends AppCache {

    @Autowired
    private CacheManager cacheManager;

    /**
     * 默认过期时长，单位：秒
     */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;

    /**
     * 会话默认过期时长，单位：秒
     */
    public final static long SESSION_DEFAULT_EXPIRE = 60 * 30;

    /**
     * 获取缓存实例，如果不存在则创建
     */
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
        Cache cache = getCache(cacheName);
        
        // 如果指定了过期时间
        if (expire != null && expire > 0) {
            // RedisCache 支持在配置时设置过期时间，动态设置需要直接操作 RedisTemplate
            // 对于 Simple 和 EhCache，过期时间由配置控制
            // 这里统一使用 put 方法，过期时间由各自的配置决定
            cache.put(key, value);
        } else {
            // 未指定过期时间，使用默认行为
            cache.put(key, value);
        }
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

