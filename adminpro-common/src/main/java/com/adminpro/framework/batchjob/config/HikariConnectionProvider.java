package com.adminpro.framework.batchjob.config;

import com.adminpro.core.base.util.SpringUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.quartz.utils.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariConnectionProvider implements ConnectionProvider {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private HikariDataSource dataSource;

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void shutdown() {
        dataSource.close();
    }

    @Override
    public void initialize() {
        logger.debug("=====initialize quartz HikariConnectionProvider=====");
        dataSource = (HikariDataSource) SpringUtil.getBean("masterDataSource");
    }
}
