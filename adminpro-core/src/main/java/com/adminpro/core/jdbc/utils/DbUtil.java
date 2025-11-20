package com.adminpro.core.jdbc.utils;

import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.base.util.StringUtil;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

public final class DbUtil {

    public static final String SQL_SERVER = "SQLServer";
    public static final String ORACLE = "Oracle";
    public static final String DB2 = "DB2";
    public static final String MYSQL = "MySQL";
    public static final String POSTGRESQL = "PostgreSQL";

    public static boolean isSqlServer() {
        return SQL_SERVER.equalsIgnoreCase(DbUtil.getDbType());
    }

    public static boolean isOracle() {
        return ORACLE.equalsIgnoreCase(DbUtil.getDbType());
    }

    public static boolean isDb2() {
        return DB2.equalsIgnoreCase(DbUtil.getDbType());
    }

    public static boolean isMySql() {
        return MYSQL.equalsIgnoreCase(DbUtil.getDbType());
    }

    public static boolean isPostgreSql() {
        return POSTGRESQL.equalsIgnoreCase(DbUtil.getDbType());
    }

    /**
     * @return
     */
    public static String getDbType() {
        Environment env = SpringUtil.getBean(Environment.class);
        String dbType = env.getProperty("database.type");
        if (StringUtils.isEmpty(dbType)) {
            return MYSQL;
        }
        return dbType;
    }

    /**
     * this method is to create the datasource with Hikari connection pool.
     * Note: this method can only be called after system is initialized,
     * otherwise, please the getDataSource(final String prefix, Environment env)
     *
     * @param prefix
     * @return
     */
    public static DataSource getDataSource(final String prefix) {
        return getDataSource(prefix, SpringUtil.getBean(Environment.class));
    }

    /**
     * to be sure the datasource is managed by spring, please create the datasource in spring configuration.
     *
     * @param prefix
     * @param env
     * @return
     */
    public static DataSource getDataSource(final String prefix, Environment env) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(env.getProperty(String.format("%s.driver-class-name", prefix)));
        config.setJdbcUrl(env.getProperty(String.format("%s.url", prefix)));
        config.setUsername(env.getProperty(String.format("%s.username", prefix)));
        config.setPassword(env.getProperty(String.format("%s.password", prefix)));
        config.setConnectionTimeout(StringUtil.getLong(env.getProperty(String.format("%s.hikari.connection-timeout", prefix)), 5 * 60 * 1000L));
        config.setMaximumPoolSize(StringUtil.getInt(env.getProperty(String.format("%s.hikari.maximum-pool-size", prefix)), 10));
        config.setMinimumIdle(StringUtil.getInt(env.getProperty(String.format("%s.hikari.minimum-idle", prefix)), 5));
        return new HikariDataSource(config);
    }

    private DbUtil() {
    }
}
