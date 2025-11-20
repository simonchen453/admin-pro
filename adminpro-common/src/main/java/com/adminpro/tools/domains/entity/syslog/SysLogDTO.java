package com.adminpro.tools.domains.entity.syslog;

import com.adminpro.framework.common.entity.BaseAuditDTO;

/**
 * @author simon
 */
public class SysLogDTO extends BaseAuditDTO {
    /**
     * ID
     */
    private String id;
    /**
     * 用户域
     */
    private String userDomain;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户显示
     */
    private String loginName;
    /**
     * 用户IP
     */
    private String ip;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 访问Request Method
     */
    private String method;
    /**
     * 操作系统
     */
    private String operation;
    /**
     * 访问参数
     */
    private String params;
    /**
     * Response
     */
    private String response;
    /**
     * 消耗时间
     */
    private Long time;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    public String getUserDomain() {
        return userDomain;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getParams() {
        return params;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTime() {
        return time;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
