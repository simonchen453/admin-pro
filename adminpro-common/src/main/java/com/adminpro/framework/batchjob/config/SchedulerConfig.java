package com.adminpro.framework.batchjob.config;

import com.adminpro.framework.common.helper.ConfigHelper;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Properties;

import static com.adminpro.framework.common.constants.ConfigKeys.*;

@Configuration
@DependsOn({"springQuartzJobFactory", "configPreloader"})
public class SchedulerConfig {

    public static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

    @Autowired
    @Qualifier(value = "springQuartzJobFactory")
    private JobFactory jobFactory;

    /**
     * Quartz 启动延迟时间（秒），可通过配置 app.quartz.startup-delay 设置，默认 5 秒
     */
    @Value("${app.quartz.startup-delay:5}")
    private int startupDelay;

    /**
     * Quartz 配置属性，在 @PostConstruct 中加载，确保配置已预加载到缓存
     */
    private Properties quartzPropertiesCache;

    /**
     * 在 @PostConstruct 中加载 Quartz 配置
     * 此时 ConfigPreloader 的 afterPropertiesSet() 已经执行，配置已预加载到缓存
     * 查询配置时会从缓存中获取，避免重复查询数据库
     */
    @PostConstruct
    public void initQuartzProperties() {
        try {
            logger.debug("开始加载 Quartz 配置属性（@PostConstruct）...");
            quartzPropertiesCache = buildQuartzProperties();
            logger.debug("Quartz 配置属性加载完成");
        } catch (Exception e) {
            logger.warn("加载 Quartz 配置属性失败: {}", e.getMessage());
        }
    }

    /**
     * 创建 SchedulerFactoryBean
     * 使用 @Lazy 延迟执行，确保 @PostConstruct 已经执行，配置已预加载到缓存
     */
    @Bean
    @Lazy
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        // 优化：使用可配置的启动延迟，默认从 30 秒减少到 5 秒
        factory.setStartupDelay(startupDelay);
        factory.setApplicationContextSchedulerContextKey("applicationContextKey");
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        
        // 使用在 @PostConstruct 中预加载的配置
        // 如果 @PostConstruct 还未执行（理论上不应该），则延迟加载
        // 此时 ConfigPreloader 应该已经预加载配置到缓存，查询会从缓存中获取
        Properties quartzProperties = quartzPropertiesCache;
        if (quartzProperties == null) {
            logger.debug("quartzPropertiesCache 为 null，延迟加载配置（此时配置应已预加载到缓存）");
            quartzProperties = buildQuartzProperties();
        }
        
        if (quartzProperties == null) {
            return factory;
        }
        factory.setQuartzProperties(quartzProperties);
        factory.setSchedulerName(quartzProperties.getProperty(QUARTZ_SCHEDULER_INSTANCE_NAME));
        return factory;
    }

    /**
     * 构建 Quartz 配置属性
     * 此时配置应该已经预加载到缓存，查询会从缓存中获取
     */
    private Properties buildQuartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        Properties prop = new Properties();
        prop.setProperty("org.quartz.dataSource.myDS.connectionProvider.class", "com.adminpro.framework.batchjob.config.HikariConnectionProvider");
        prop.setProperty(QUARTZ_JOBSTORE_DRIVER_DELEGATE_CLASS, ConfigHelper.getString(QUARTZ_JOBSTORE_DRIVER_DELEGATE_CLASS, "org.quartz.impl.jdbcjobstore.StdJDBCDelegate"));

        prop.put(QUARTZ_SCHEDULER_INSTANCE_NAME, ConfigHelper.getString(QUARTZ_SCHEDULER_INSTANCE_NAME, "AdminProScheduler"));
        prop.put(QUARTZ_SCHEDULER_INSTANCE_ID, ConfigHelper.getString(QUARTZ_SCHEDULER_INSTANCE_ID, "AUTO"));
        //线程池配置
        prop.put(QUARTZ_THREADPOOL_CLASS, ConfigHelper.getString(QUARTZ_THREADPOOL_CLASS, "org.quartz.simpl.SimpleThreadPool"));
        prop.put(QUARTZ_THREADPOOL_THREAD_COUNT, ConfigHelper.getString(QUARTZ_THREADPOOL_THREAD_COUNT, "20"));
        prop.put(QUARTZ_THREADPOOL_THREAD_PRIORITY, ConfigHelper.getString(QUARTZ_THREADPOOL_THREAD_PRIORITY, "5"));
        //JobStore配置
        prop.put(QUARTZ_JOBSTORE_CLASS, ConfigHelper.getString(QUARTZ_JOBSTORE_CLASS, "org.quartz.impl.jdbcjobstore.JobStoreTX"));
        //集群配置
        prop.put(QUARTZ_JOBSTORE_IS_CLUSTERED, ConfigHelper.getString(QUARTZ_JOBSTORE_IS_CLUSTERED, "true"));
        prop.put(QUARTZ_JOBSTORE_CLUSTER_CHECKIN_INTERVAL, ConfigHelper.getString(QUARTZ_JOBSTORE_CLUSTER_CHECKIN_INTERVAL, "15000"));
        prop.put(QUARTZ_JOBSTORE_MAX_MISFIRES_TO_HANDLE_AT_A_TIME, ConfigHelper.getString(QUARTZ_JOBSTORE_MAX_MISFIRES_TO_HANDLE_AT_A_TIME, "1"));

        prop.put(QUARTZ_JOBSTORE_MISFIRE_THRESHOLD, ConfigHelper.getString(QUARTZ_JOBSTORE_MISFIRE_THRESHOLD, "12000"));
        prop.put(QUARTZ_JOBSTORE_TABLE_PREFIX, ConfigHelper.getString(QUARTZ_JOBSTORE_TABLE_PREFIX, "QRTZ_"));

        prop.put(QUARTZ_JOBSTORE_DATASOURCE, ConfigHelper.getString(QUARTZ_JOBSTORE_DATASOURCE, "myDS"));
        prop.put(QUARTZ_THREADPOOL_THREADS_INHERIT_CONTEXT_CLASSLOADER, ConfigHelper.getString(QUARTZ_THREADPOOL_THREADS_INHERIT_CONTEXT_CLASSLOADER, "true"));
        prop.put(QUARTZ_JOBSTORE_USE_PROPERTIES, ConfigHelper.getString(QUARTZ_JOBSTORE_USE_PROPERTIES, "true"));

        propertiesFactoryBean.setProperties(prop);
        try {
            propertiesFactoryBean.afterPropertiesSet();
        } catch (IOException e) {
            return null;
        }
        return propertiesFactoryBean.getObject();
    }

}
