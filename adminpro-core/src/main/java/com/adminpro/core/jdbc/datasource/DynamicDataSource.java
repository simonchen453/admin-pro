package com.adminpro.core.jdbc.datasource;

import com.adminpro.core.aspect.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 自定义动态数据源， AdminPro中数据源使用的是DynamicDataSource， 通过determineCurrentLookupKey()方法获取当前连接的是哪一个数据源
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceType = DynamicDataSourceContextHolder.getDataSourceType();
        return dataSourceType;
    }
}