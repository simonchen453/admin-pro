package com.adminpro.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全配置属性类
 * 支持通过 application.yml 配置公开接口和认证规则
 * 
 * 配置示例:
 * app:
 * security:
 * public-urls:
 * - /api/public/**
 * - /auth/login
 * - /common/**
 * anonymous-urls:
 * - /actuator/health
 * auth-required: true
 *
 * @author AdminPro
 */
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    /**
     * 公开接口列表（无需认证即可访问）
     * 默认包含基础的公开接口
     */
    private List<String> publicUrls = new ArrayList<>();

    /**
     * 匿名接口列表（可选，与publicUrls类似）
     */
    private List<String> anonymousUrls = new ArrayList<>();

    /**
     * 静态资源路径列表
     */
    private List<String> staticUrls = new ArrayList<>();

    /**
     * 是否启用认证（全局开关）
     * true: 默认所有接口需要认证，除了 publicUrls 中配置的接口
     * false: 默认所有接口都允许访问（不推荐用于生产环境）
     */
    private boolean authRequired = true;

    /**
     * 初始化默认值
     */
    public SecurityProperties() {
        // 默认公开接口
        this.publicUrls.add("/auth/login");
        this.publicUrls.add("/auth/logout");
        this.publicUrls.add("/auth/captcha/**");
        this.publicUrls.add("/common/**");
        this.publicUrls.add("/public/**");
        this.publicUrls.add("/error");
        this.publicUrls.add("/favicon.ico");

        // 默认静态资源
        this.staticUrls.add("/js/**");
        this.staticUrls.add("/plugins/**");
        this.staticUrls.add("/css/**");
        this.staticUrls.add("/images/**");
        this.staticUrls.add("/img/**");
        this.staticUrls.add("/icons/**");
        this.staticUrls.add("/static/**");
        this.staticUrls.add("/assets/**");

        // 默认匿名接口
        this.anonymousUrls.add("/actuator/health");
        this.anonymousUrls.add("/actuator/info");
    }

    public List<String> getPublicUrls() {
        return publicUrls;
    }

    public void setPublicUrls(List<String> publicUrls) {
        this.publicUrls = publicUrls;
    }

    public List<String> getAnonymousUrls() {
        return anonymousUrls;
    }

    public void setAnonymousUrls(List<String> anonymousUrls) {
        this.anonymousUrls = anonymousUrls;
    }

    public List<String> getStaticUrls() {
        return staticUrls;
    }

    public void setStaticUrls(List<String> staticUrls) {
        this.staticUrls = staticUrls;
    }

    public boolean isAuthRequired() {
        return authRequired;
    }

    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }

    /**
     * 获取所有公开接口（合并 publicUrls、anonymousUrls 和 staticUrls）
     */
    public String[] getAllPublicUrls() {
        List<String> allUrls = new ArrayList<>();
        allUrls.addAll(publicUrls);
        allUrls.addAll(anonymousUrls);
        allUrls.addAll(staticUrls);
        return allUrls.toArray(new String[0]);
    }
}
