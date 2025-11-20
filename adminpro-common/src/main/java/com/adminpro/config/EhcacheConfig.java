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
 * 
 * 注意：如果使用 Spring Boot 自动配置，可以删除此类，只需在 application.yml 中配置：
 * spring.cache.type=jcache
 * spring.cache.jcache.config=classpath:ehcache.xml
 * spring.cache.jcache.provider=org.ehcache.jsr107.EhcacheCachingProvider
 * 
 * 这里保留手动配置以提供更好的错误处理和日志
 */
@Configuration
@Slf4j
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "jcache", matchIfMissing = false)
public class EhcacheConfig {

    /**
     * 创建 JCache CacheManager (EhCache 3.x)
     * 如果 Spring Boot 自动配置可用，此方法可能不会被调用
     */
    @Bean(name = "jcacheCacheManager")
    public CacheManager jcacheCacheManager() {
        try {
            // 检查 JAXB 是否可用（javax 版本，EhCache XML 配置需要）
            try {
                Class.forName("javax.xml.bind.JAXBContext");
                log.debug("JAXB API 可用");
            } catch (ClassNotFoundException e) {
                log.error("JAXB API 不可用，请检查依赖", e);
                throw new RuntimeException("JAXB API 不可用，请确保已添加 jaxb-api 依赖", e);
            }
            
            // 检查 JAXB 实现是否可用
            try {
                Class.forName("org.glassfish.jaxb.runtime.v2.JAXBContextFactory");
                log.debug("Jakarta JAXB 实现类可用");
            } catch (ClassNotFoundException e) {
                log.error("缺少 Jakarta JAXB 实现！", e);
            }
            
            CachingProvider cachingProvider = Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
            log.info("使用 CachingProvider: {}", cachingProvider.getClass().getName());
            
            URI configUri = getClass().getResource("/ehcache.xml").toURI();
            log.info("加载 EhCache 配置文件: {}", configUri);
            
            CacheManager cacheManager = cachingProvider.getCacheManager(configUri, getClass().getClassLoader());
            log.info("EhCache 3.x JCache CacheManager 初始化成功");
            return cacheManager;
        } catch (Exception e) {
            log.error("EhCache 3.x JCache CacheManager 初始化失败", e);
            Throwable cause = e;
            int depth = 0;
            while (cause != null && depth < 5) {
                log.error("错误原因 [{}]: {} - {}", depth, cause.getClass().getName(), cause.getMessage());
                if (cause.getCause() != null && cause.getCause() != cause) {
                    cause = cause.getCause();
                    depth++;
                } else {
                    break;
                }
            }
            throw new RuntimeException("无法初始化 EhCache CacheManager: " + e.getMessage(), e);
        }
    }

    /**
     * 创建 Spring Cache CacheManager，包装 JCache CacheManager
     * 如果 Spring Boot 自动配置可用，此方法可能不会被调用
     */
    @Bean
    @Primary
    public org.springframework.cache.CacheManager cacheManager(CacheManager jcacheCacheManager) {
        JCacheCacheManager cacheManager = new JCacheCacheManager(jcacheCacheManager);
        log.info("Spring Cache CacheManager (EhCache 3.x) 初始化成功");
        return cacheManager;
    }
}
