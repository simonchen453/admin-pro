package com.adminpro.framework.batchjob.config;

import com.adminpro.framework.common.helper.ConfigHelper;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

@Configuration
@DependsOn({"springQuartzJobFactory"})
public class SchedulerConfig {

    public static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

    @Autowired
    @Qualifier(value = "springQuartzJobFactory")
    private JobFactory jobFactory;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setStartupDelay(30);
        factory.setApplicationContextSchedulerContextKey("applicationContextKey");
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        Properties quartzProperties = quartzProperties();
        if (quartzProperties == null) {
            return factory;
        }
        factory.setQuartzProperties(quartzProperties);
        factory.setSchedulerName(quartzProperties.getProperty("org.quartz.scheduler.instanceName"));
        return factory;
    }

    private Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        Properties prop = new Properties();
        prop.setProperty("org.quartz.dataSource.myDS.connectionProvider.class", "com.adminpro.framework.batchjob.config.HikariConnectionProvider");
        prop.setProperty("org.quartz.jobStore.driverDelegateClass", ConfigHelper.getString("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate"));

        prop.put("org.quartz.scheduler.instanceName", ConfigHelper.getString("org.quartz.scheduler.instanceName", "AdminProScheduler"));
        prop.put("org.quartz.scheduler.instanceId", ConfigHelper.getString("org.quartz.scheduler.instanceId", "AUTO"));
        //线程池配置
        prop.put("org.quartz.threadPool.class", ConfigHelper.getString("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool"));
        prop.put("org.quartz.threadPool.threadCount", ConfigHelper.getString("org.quartz.threadPool.threadCount", "20"));
        prop.put("org.quartz.threadPool.threadPriority", ConfigHelper.getString("org.quartz.threadPool.threadPriority", "5"));
        //JobStore配置
        prop.put("org.quartz.jobStore.class", ConfigHelper.getString("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX"));
        //集群配置
        prop.put("org.quartz.jobStore.isClustered", ConfigHelper.getString("org.quartz.jobStore.isClustered", "true"));
        prop.put("org.quartz.jobStore.clusterCheckinInterval", ConfigHelper.getString("org.quartz.jobStore.clusterCheckinInterval", "15000"));
        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", ConfigHelper.getString("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1"));

        prop.put("org.quartz.jobStore.misfireThreshold", ConfigHelper.getString("org.quartz.jobStore.misfireThreshold", "12000"));
        prop.put("org.quartz.jobStore.tablePrefix", ConfigHelper.getString("org.quartz.jobStore.tablePrefix", "QRTZ_"));

        prop.put("org.quartz.jobStore.dataSource", ConfigHelper.getString("org.quartz.jobStore.dataSource", "myDS"));
        prop.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", ConfigHelper.getString("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true"));
        prop.put("org.quartz.jobStore.useProperties", ConfigHelper.getString("org.quartz.jobStore.useProperties", "true"));

        propertiesFactoryBean.setProperties(prop);
        try {
            propertiesFactoryBean.afterPropertiesSet();
        } catch (IOException e) {
            return null;
        }
        return propertiesFactoryBean.getObject();
    }

}
