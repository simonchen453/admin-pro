package com.adminpro.system.config;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URI;

/**
 * EhCache 3.x 缓存配置类
 * <p>
 * 该配置类负责配置EhCache 3.x作为Spring Cache的缓存实现。
 * Spring Boot 3.x使用JCache（JSR-107）标准来支持EhCache 3。
 * <p>
 * 配置条件：
 * <ul>
 *   <li>spring.cache.type=jcache：启用JCache缓存</li>
 *   <li>需要EhCache 3.x依赖</li>
 *   <li>需要ehcache.xml配置文件</li>
 * </ul>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>配置JCache CacheManager（EhCache 3.x实现）</li>
 *   <li>包装为Spring Cache兼容的CacheManager</li>
 *   <li>提供详细的错误处理和日志</li>
 *   <li>检查JAXB依赖可用性</li>
 * </ul>
 * <p>
 * 注意事项：
 * <ul>
 *   <li>Spring Boot 3.x使用javax版本的JAXB</li>
 *   <li>需要jaxb-api和jaxb-impl依赖</li>
 *   <li>配置文件位置：classpath:ehcache.xml</li>
 * </ul>
 * <p>
 * 如使用Spring Boot自动配置，可在application.yml中配置：
 * <pre>
 * spring.cache.type=jcache
 * spring.cache.jcache.config=classpath:ehcache.xml
 * spring.cache.jcache.provider=org.ehcache.jsr107.EhcacheCachingProvider
 * </pre>
 * <p>
 * 这里保留手动配置以提供更好的错误处理和日志记录。
 *
 * @author simon
 * @see javax.cache.CacheManager
 * @see org.ehcache.jsr107.EhcacheCachingProvider
 * @see org.springframework.cache.jcache.JCacheCacheManager
 */
@Configuration
@Slf4j
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "jcache", matchIfMissing = false)
public class EhcacheConfig {

    /**
     * 创建JCache CacheManager（EhCache 3.x实现）
     * <p>
     * 该方法创建并配置EhCache 3.x的CacheManager：
     * <ul>
     *   <li>使用EhcacheCachingProvider作为JCache提供者</li>
     *   <li>从ehcache.xml加载缓存配置</li>
     *   <li>检查JAXB依赖可用性（XML解析需要）</li>
     *   <li>提供详细的错误信息和堆栈跟踪</li>
     * </ul>
     * <p>
     * 初始化步骤：
     * <ol>
     *   <li>检查JAXB API是否可用</li>
     *   <li>检查JAXB实现是否可用</li>
     *   <li>获取EhCache CachingProvider</li>
     *   <li>加载ehcache.xml配置文件</li>
     *   <li>创建CacheManager实例</li>
     * </ol>
     * <p>
     * 常见错误：
     * <ul>
     *   <li>JAXB API不可用：添加jaxb-api依赖</li>
     *   <li>JAXB实现不可用：添加jaxb-impl依赖</li>
     *   <li>配置文件不存在：检查ehcache.xml路径</li>
     *   <li>配置文件格式错误：检查XML语法</li>
     * </ul>
     * <p>
     * 注意：如果Spring Boot自动配置可用，此Bean可能不会被调用。
     *
     * @return 配置好的JCache CacheManager实例
     * @throws RuntimeException 当初始化失败时抛出
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
     * 创建Spring Cache CacheManager
     * <p>
     * 该方法将JCache CacheManager包装为Spring Cache兼容的CacheManager：
     * <ul>
     *   <li>使用JCacheCacheManager包装JCache CacheManager</li>
     *   <li>使其与Spring Cache注解兼容（@Cacheable等）</li>
     *   <li>使用@Primary注解作为默认缓存管理器</li>
     *   <li>提供Spring标准的缓存操作接口</li>
     * </ul>
     * <p>
     * 包装优势：
     * <ul>
     *   <li>可以使用Spring Cache注解</li>
     *   <li>统一缓存操作接口</li>
     *   <li>便于与其他Spring组件集成</li>
     *   <li>支持缓存抽象和切换</li>
     * </ul>
     * <p>
     * 使用示例：
     * <pre>
     * &#64;Cacheable(value = "userCache", key = "#id")
     * public User getUser(Long id) {
     *     return userRepository.findById(id);
     * }
     * </pre>
     * <p>
     * 注意：如果Spring Boot自动配置可用，此Bean可能不会被调用。
     *
     * @param jcacheCacheManager JCache CacheManager实例（由jcacheCacheManager()方法创建）
     * @return Spring Cache兼容的CacheManager实例
     */
    @Bean
    @Primary
    public org.springframework.cache.CacheManager cacheManager(CacheManager jcacheCacheManager) {
        JCacheCacheManager cacheManager = new JCacheCacheManager(jcacheCacheManager);
        log.info("Spring Cache CacheManager (EhCache 3.x) 初始化成功");
        return cacheManager;
    }
}
