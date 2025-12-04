package com.adminpro.config;

import com.adminpro.core.base.util.SpringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 读取代码生成相关配置
 *
 * @author simon
 */
@Configuration
public class GenConfig {
    public static GenConfig getInstance() {
        return SpringUtil.getBean(GenConfig.class);
    }

    /**
     * 作者
     */
    @Value("${gen.author}")
    public String author;
    /**
     * 生成包路径
     */
    @Value("${gen.packageName}")
    public String packageName;
    /**
     * 自动去除表前缀，默认是true
     */
    @Value("${gen.autoRemovePre}")
    public String autoRemovePre;
    /**
     * 表前缀(类名不会包含表前缀)
     */
    @Value("${gen.tablePrefix}")
    public String tablePrefix;

    @Value("${gen.tableSuffix}")
    public String tableSuffix;

    @Value("${gen.colPrefix}")
    public String colPrefix;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAutoRemovePre() {
        return autoRemovePre;
    }

    public void setAutoRemovePre(String autoRemovePre) {
        this.autoRemovePre = autoRemovePre;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String getTableSuffix() {
        return tableSuffix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    public String getColPrefix() {
        return colPrefix;
    }

    public void setColPrefix(String colPrefix) {
        this.colPrefix = colPrefix;
    }

    @Override
    public String toString() {
        return "GenConfig [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
    }
}
