package com.adminpro.core.config;

import com.adminpro.core.base.enums.DataSourceType;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TransactionAutoConfiguration {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.datasource.master.type:com.zaxxer.hikari.HikariDataSource}")
    private String masterDSType;

    @Value("${spring.datasource.slave.type:com.zaxxer.hikari.HikariDataSource}")
    private String slaveDSType;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.master")
    @DependsOn({"springUtil"})
    public DataSource masterDataSource() {
        logger.debug("## Loading master dataSource");
        Class<? extends DataSource> type;
        try {
            type = (Class<? extends DataSource>) Class.forName(masterDSType);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            type = HikariDataSource.class;
        }

        return DataSourceBuilder.create().type(type).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    @ConditionalOnProperty(prefix = "spring.datasource.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource() {
        logger.debug("## Loading slave dataSource");
        Class<? extends DataSource> type;
        try {
            type = (Class<? extends DataSource>) Class.forName(slaveDSType);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            type = HikariDataSource.class;
        }

        return DataSourceBuilder.create().type(type).build();
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource masterDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.MASTER.name(), masterDataSource);
        setDataSource(targetDataSources, DataSourceType.SLAVE.name(), "slaveDataSource");
        return new DynamicDataSource(masterDataSource, targetDataSources);
    }

    /**
     * 设置数据源
     *
     * @param targetDataSources 备选数据源集合
     * @param sourceName        数据源名称
     * @param beanName          bean名称
     */
    public void setDataSource(Map<Object, Object> targetDataSources, String sourceName, String beanName) {
        try {
            DataSource dataSource = SpringUtil.getBean(beanName, DataSource.class);
            targetDataSources.put(sourceName, dataSource);
        } catch (Exception e) {
        }
    }
}
