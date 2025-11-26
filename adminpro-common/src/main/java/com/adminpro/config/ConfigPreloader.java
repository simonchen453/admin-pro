package com.adminpro.config;

import com.adminpro.tools.domains.entity.config.ConfigEntity;
import com.adminpro.tools.domains.entity.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 配置预加载器
 * 在应用启动时批量加载所有配置到缓存，避免启动时频繁查询数据库
 * 
 * @author optimization
 */
@Component
@Order(1) // 优先执行，在其他组件使用配置之前
public class ConfigPreloader implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ConfigPreloader.class);

    private final ConfigService configService;

    public ConfigPreloader(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            logger.info("开始预加载系统配置到缓存...");
            long startTime = System.currentTimeMillis();
            
            // 批量加载所有配置（通过查询所有配置，触发缓存）
            // 注意：这里需要 ConfigService 提供 findAll 方法，或者使用其他方式批量加载
            // 由于 ConfigService 已经有缓存注解，我们只需要触发一次查询即可预热缓存
            
            // 方案1：如果 ConfigService 有 findAll 方法，直接调用
            // List<ConfigEntity> allConfigs = configService.findAll();
            
            // 方案2：预加载常用的配置项（根据启动日志中的配置项）
            String[] commonConfigKeys = {
                "app.default.nation",
                "app.check.capture.domains",
                "app.default.nationality",
                "app.default.province",
                "app.default.city",
                "app.default.district",
                "app.default.maritalstatus",
                "app.default.certtype",
                "app.default.idtype",
                "app.upload.dir",
                "app.deployment.mode",
                "app.auth.code.expire.period",
                "app.dept.super.parent.id",
                // Quartz 配置
                "org.quartz.jobStore.driverDelegateClass",
                "org.quartz.scheduler.instanceName",
                "org.quartz.scheduler.instanceId",
                "org.quartz.threadPool.class",
                "org.quartz.threadPool.threadCount",
                "org.quartz.threadPool.threadPriority",
                "org.quartz.jobStore.class",
                "org.quartz.jobStore.isClustered",
                "org.quartz.jobStore.clusterCheckinInterval",
                "org.quartz.jobStore.maxMisfiresToHandleAtATime",
                "org.quartz.jobStore.misfireThreshold",
                "org.quartz.jobStore.tablePrefix",
                "org.quartz.jobStore.dataSource",
                "org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread",
                "org.quartz.jobStore.useProperties"
            };
            
            int loadedCount = 0;
            for (String key : commonConfigKeys) {
                try {
                    ConfigEntity config = configService.findByKey(key);
                    if (config != null) {
                        loadedCount++;
                    }
                } catch (Exception e) {
                    // 忽略单个配置加载失败，继续加载其他配置
                    logger.debug("预加载配置 {} 失败: {}", key, e.getMessage());
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("配置预加载完成，已加载 {} 个配置项，耗时 {} ms", loadedCount, duration);
            
        } catch (Exception e) {
            logger.warn("配置预加载失败，将使用懒加载方式: {}", e.getMessage());
            // 不抛出异常，允许应用继续启动
        }
    }
}

