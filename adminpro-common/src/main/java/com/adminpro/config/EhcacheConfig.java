package com.adminpro.config;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.URI;

/**
 * EhCache 3.x 配置类
 * 当 spring.cache.type=jcache 时启用（Spring Boot 3.x 使用 jcache 来支持 EhCache 3）
 */
@Configuration
@Slf4j
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "jcache", matchIfMissing = false)
public class EhcacheConfig {

    /**
     * 创建 JCache CacheManager (EhCache 3.x)
     */
    @Bean
    public CacheManager jcacheCacheManager() {
        try {
            CachingProvider cachingProvider = Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
            URI configUri = getClass().getResource("/ehcache.xml").toURI();
            CacheManager cacheManager = cachingProvider.getCacheManager(configUri, getClass().getClassLoader());
            log.info("EhCache 3.x JCache CacheManager 初始化成功");
            return cacheManager;
        } catch (Exception e) {
            log.error("EhCache 3.x JCache CacheManager 初始化失败", e);
            throw new RuntimeException("无法初始化 EhCache CacheManager", e);
        }
    }

    /**
     * 创建 Spring Cache CacheManager，包装 JCache CacheManager
     */
    @Bean
    @Primary
    public org.springframework.cache.CacheManager cacheManager(CacheManager jcacheCacheManager) {
        JCacheCacheManager cacheManager = new JCacheCacheManager(jcacheCacheManager);
        log.info("Spring Cache CacheManager (EhCache 3.x) 初始化成功");
        return cacheManager;
    }
}
