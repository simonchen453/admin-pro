package com.adminpro.config;

import com.adminpro.tools.domains.entity.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 配置预加载器
 * <p>
 * 在应用启动时批量加载所有配置到缓存，避免启动时频繁查询数据库。
 * </p>
 * <p>
 * 使用 {@link InitializingBean#afterPropertiesSet()} 在 Bean 创建后立即预加载，
 * 这比 {@code @PostConstruct} 执行得更早，可以确保在其他 Bean 的 {@code @Bean} 方法执行之前完成。
 * 通过 {@code @DependsOn("configPreloader")} 确保依赖此 Bean 的其他 Bean 会在配置预加载完成后才初始化。
 * </p>
 * 
 * @author optimization
 */
@Component
public class ConfigPreloader implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ConfigPreloader.class);

    @Autowired
    private ConfigService configService;

    /**
     * 在 Bean 创建后、依赖注入完成后立即执行配置预加载
     */
    @Override
    public void afterPropertiesSet() {
        try {
            logger.info("开始预加载系统配置到缓存...");
            long startTime = System.currentTimeMillis();
            
            int loadedCount = configService.preloadAll();
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("配置预加载完成，已加载 {} 个配置项到缓存，耗时 {} ms", loadedCount, duration);
        } catch (Exception e) {
            logger.warn("配置预加载失败，将使用懒加载方式: {}", e.getMessage());
            // 不抛出异常，允许应用继续启动
        }
    }
}


