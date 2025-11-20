package com.adminpro.framework.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Cache工具类
 */
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
@Component
public class AppRedisCache extends AppCache {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ValueOperations<String, Object> valueOperations;

    /**
     * 默认过期时长，单位：秒
     */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;

    public final static long SESSION_DEFAULT_EXPIRE = 60 * 30;
    /**
     * 不设置过期时长
     */
    public final static long NOT_EXPIRE = -1;

    private void set(String key, Object value, long expire) {
        RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
        byte[] serialize = valueSerializer.serialize(value);
        valueOperations.set(key, serialize);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    private void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    @Override
    public void set(String cacheName, String key, Object value) {
        set(cacheName + ":" + key, value, DEFAULT_EXPIRE);
    }

    @Override
    public void set(String cacheName, String key, Object value, Integer expire) {
        set(cacheName + ":" + key, value, expire);
    }

    private <T> T get(String key, Class<T> clazz, long expire) {
        byte[] value = (byte[]) valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }

        RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

        return value == null ? null : (T) valueSerializer.deserialize(value);
    }

    private <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    @Override
    public <T> T get(String cacheName, String key, Class<T> clazz) {
        return get(cacheName + ":" + key, clazz);
    }

    private void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void delete(String cacheName, String key) {
        delete(cacheName + ":" + key);
    }
}
