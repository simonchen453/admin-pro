package com.adminpro.config;

import com.adminpro.tools.domains.entity.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 配置预加载器
 * 在应用启动时批量加载所有配置到缓存，避免启动时频繁查询数据库
 * 
 * 使用 InitializingBean.afterPropertiesSet() 在 Bean 创建后立即预加载
 * 这比 @PostConstruct 执行得更早，可以确保在其他 Bean 的 @Bean 方法执行之前完成
 * 同时实现 ApplicationRunner 作为备用方案
 * 
 * @author optimization
 */
@Component
@Order(1) // 优先执行，在其他组件使用配置之前
public class ConfigPreloader implements ApplicationRunner, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ConfigPreloader.class);

    @Autowired
    private ConfigService configService;

    private static volatile boolean preloaded = false;

    /**
     * InitializingBean.afterPropertiesSet() 在 Bean 创建后、依赖注入完成后立即执行
     * 这比 @PostConstruct 执行得更早，可以确保在其他 Bean 的 @Bean 方法执行之前完成配置预加载
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (preloaded) {
            return;
        }
        synchronized (ConfigPreloader.class) {
            if (preloaded) {
                return;
            }
            try {
                logger.info("开始预加载系统配置到缓存（InitializingBean）...");
                long startTime = System.currentTimeMillis();
                
                // 使用批量预加载方法，一次性加载所有配置到缓存
                int loadedCount = configService.preloadAll();
                
                long duration = System.currentTimeMillis() - startTime;
                logger.info("配置预加载完成（InitializingBean），已加载 {} 个配置项到缓存，耗时 {} ms", loadedCount, duration);
                
                preloaded = true;
            } catch (Exception e) {
                logger.warn("配置预加载失败（InitializingBean），将使用懒加载方式: {}", e.getMessage());
                // 不抛出异常，允许应用继续启动
            }
        }
    }

    /**
     * @PostConstruct 作为备用方案（在 afterPropertiesSet 之后执行）
     */
    @PostConstruct
    public void preloadConfigsPostConstruct() {
        if (!preloaded) {
            try {
                logger.info("开始预加载系统配置到缓存（@PostConstruct）...");
                long startTime = System.currentTimeMillis();
                
                int loadedCount = configService.preloadAll();
                
                long duration = System.currentTimeMillis() - startTime;
                logger.info("配置预加载完成（@PostConstruct），已加载 {} 个配置项到缓存，耗时 {} ms", loadedCount, duration);
                
                preloaded = true;
            } catch (Exception e) {
                logger.warn("配置预加载失败（@PostConstruct），将使用懒加载方式: {}", e.getMessage());
            }
        }
    }

    /**
     * ApplicationRunner 作为备用方案
     * 如果 @PostConstruct 因为某些原因没有执行，这里会再次尝试
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!preloaded) {
            try {
                logger.info("开始预加载系统配置到缓存（ApplicationRunner）...");
                long startTime = System.currentTimeMillis();
                
                int loadedCount = configService.preloadAll();
                
                long duration = System.currentTimeMillis() - startTime;
                logger.info("配置预加载完成（ApplicationRunner），已加载 {} 个配置项到缓存，耗时 {} ms", loadedCount, duration);
                
                preloaded = true;
            } catch (Exception e) {
                logger.warn("配置预加载失败（ApplicationRunner），将使用懒加载方式: {}", e.getMessage());
            }
        } else {
            logger.debug("配置已在 @PostConstruct 阶段预加载，跳过 ApplicationRunner 预加载");
        }
    }
}

