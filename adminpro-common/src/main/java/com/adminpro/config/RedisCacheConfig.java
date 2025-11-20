package com.adminpro.config;

import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.tools.lock.CacheKeyGenerator;
import com.adminpro.tools.lock.LockKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author simon
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Slf4j
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "redis", matchIfMissing = false)
public class RedisCacheConfig extends CachingConfigurerSupport {

    /**
     * 缓存管理器
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(connectionFactory);
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        ConcurrentHashMap configMap = new ConcurrentHashMap<>();
        String[] ttls = ConfigHelper.getStringArray("app.cache.ttls");
        Set cacheNames = new HashSet();
        for (int i = 0; i < ttls.length; i++) {
            String ttl = ttls[i];
            String[] split = ttl.split("@");
            if (ArrayUtils.isNotEmpty(split) && split.length == 2) {
                long t = -1L;
                try {
                    t = Long.valueOf(split[1]);
                } catch (Exception e) {
                }
                if (t > 0) {
                    cacheNames.add(split[0]);
                    configMap.put(split[0], config.entryTtl(Duration.ofMinutes(t)));
                    log.debug("缓存：" + split[0] + ", 过期时间设置为： " + t + "分钟");
                }
            }
        }
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(writer).initialCacheNames(cacheNames).withInitialCacheConfigurations(configMap).build();
        return redisCacheManager;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer(this.getClass().getClassLoader());
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jdkSerializationRedisSerializer);
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public CacheKeyGenerator cacheKeyGenerator() {
        return new LockKeyGenerator();
    }
}
