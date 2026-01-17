package com.adminpro.system.config;

import com.adminpro.system.core.common.constants.ConfigKeys;
import com.adminpro.system.core.common.helper.ConfigHelper;
import com.adminpro.system.tools.lock.CacheKeyGenerator;
import com.adminpro.system.tools.lock.LockKeyGenerator;
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
 * Redis缓存配置类
 * <p>
 * 该配置类负责配置Spring Cache与Redis的集成，使用Redis作为缓存存储。
 * 当配置文件中设置 spring.cache.type=redis 时，该配置类生效。
 * <p>
 * 主要功能：
 * <ul>
 *   <li>配置RedisCacheManager作为Spring Cache的缓存管理器</li>
 *   <li>配置RedisTemplate的序列化策略（Key使用String，Value使用JDK序列化）</li>
 *   <li>支持为不同缓存名称配置不同的过期时间（TTL）</li>
 *   <li>配置缓存键生成器，支持自定义缓存键策略</li>
 *   <li>配置分布式锁的键生成器</li>
 * </ul>
 * <p>
 * 配置条件：
 * <ul>
 *   <li>spring.cache.type=redis：启用Redis缓存</li>
 *   <li>需要Redis依赖和Redis服务可用</li>
 * </ul>
 * <p>
 * TTL配置格式：
 * 在配置文件中通过 cache.ttls 配置，格式：缓存名@过期分钟数
 * 例如：userCache@30,userDetailCache@60
 *
 * @author simon
 * @see org.springframework.cache.CacheManager
 * @see org.springframework.data.redis.cache.RedisCacheManager
 * @see org.springframework.boot.autoconfigure.AutoConfigureAfter
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Slf4j
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "redis", matchIfMissing = false)
public class RedisCacheConfig extends CachingConfigurerSupport {

    /**
     * 配置Redis缓存管理器
     * <p>
     * 该方法创建并配置RedisCacheManager，作为Spring Cache的缓存实现：
     * <ul>
     *   <li>使用Redis作为缓存存储</li>
     *   <li>支持多缓存名称和不同TTL配置</li>
     *   <li>Key使用String序列化（便于阅读和调试）</li>
     *   <li>Value使用JDK序列化（保持对象类型）</li>
     *   <li>使用锁机制保证并发安全</li>
     * </ul>
     * <p>
     * TTL配置示例（application.yml）：
     * <pre>
     * cache:
     *   ttls:
     *     - userCache@30          # userCache缓存30分钟
     *     - userDetailCache@60    # userDetailCache缓存60分钟
     * </pre>
     * <p>
     * 注意事项：
     * <ul>
     *   <li>使用@Primary注解，作为默认缓存管理器</li>
     *   <li>TTL单位为分钟</li>
     *   <li>未配置TTL的缓存使用默认策略</li>
     * </ul>
     *
     * @param connectionFactory Redis连接工厂，用于连接Redis服务器
     * @return 配置好的RedisCacheManager实例
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(connectionFactory);
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        ConcurrentHashMap configMap = new ConcurrentHashMap<>();
        String[] ttls = ConfigHelper.getStringArray(ConfigKeys.Cache.TTLS);
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

    /**
     * 配置RedisTemplate
     * <p>
     * 该方法创建并配置RedisTemplate，用于直接操作Redis：
     * <ul>
     *   <li>Key序列化：使用StringRedisSerializer，便于阅读和调试</li>
     *   <li>Value序列化：使用JdkSerializationRedisSerializer，保持对象类型</li>
     *   <li>泛型配置：RedisTemplate&lt;String, Object&gt;</li>
     *   <li>连接设置：使用提供的RedisConnectionFactory</li>
     * </ul>
     * <p>
     * 使用场景：
     * <ul>
     *   <li>需要直接操作Redis时使用</li>
     *   <li>不使用Spring Cache注解时使用</li>
     *   <li>需要更细粒度控制时使用</li>
     * </ul>
     * <p>
     * 注意事项：
     * <ul>
     *   <li>JDK序列化要求对象实现Serializable接口</li>
     *   <li>序列化后的数据不易读（二进制格式）</li>
     *   <li>如需可读性，可改用JSON序列化</li>
     * </ul>
     *
     * @param connectionFactory Redis连接工厂
     * @return 配置好的RedisTemplate实例
     */
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

    /**
     * 配置Redis值操作对象
     * <p>
     * 该方法创建ValueOperations实例，用于操作Redis的String类型数据：
     * <ul>
     *   <li>提供简单的key-value操作</li>
     *   <li>支持set、get、delete等基本操作</li>
     *   <li>支持过期时间设置</li>
     *   <li>支持多种数据类型的存储</li>
     * </ul>
     * <p>
     * 使用示例：
     * <pre>
     * valueOperations.set("key", "value");
     * Object value = valueOperations.get("key");
     * </pre>
     *
     * @param redisTemplate RedisTemplate实例
     * @return ValueOperations实例，用于Redis字符串操作
     */
    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 配置缓存键生成器
     * <p>
     * 该方法创建自定义的缓存键生成器，用于生成分布式缓存锁的键：
     * <ul>
     *   <li>实现LockKeyGenerator接口</li>
     *   <li>支持基于方法参数生成唯一键</li>
     *   <li>用于分布式缓存锁场景</li>
     *   <li>确保键的唯一性和可读性</li>
     * </ul>
     * <p>
     * 应用场景：
     * <ul>
     *   <li>防止缓存击穿</li>
     *   <li>防止缓存雪崩</li>
     *   <li>分布式锁实现</li>
     * </ul>
     *
     * @return 缓存键生成器实例
     */
    @Bean
    public CacheKeyGenerator cacheKeyGenerator() {
        return new LockKeyGenerator();
    }
}
