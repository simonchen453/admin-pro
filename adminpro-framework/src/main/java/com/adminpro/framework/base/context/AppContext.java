package com.adminpro.framework.base.context;

import java.io.Serializable;

/**
 * 应用上下文
 * 用于在请求或线程中传递用户信息
 *
 * @author simon
 * @date 2021/2/3
 */
public class AppContext implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户主键ID
     */
    private String id;

    /**
     * 用户域
     */
    private String userDomain;

    /**
     * 登录账号
     */
    private String loginName;

    /**
     * 用户真实姓名
     */
    private String realName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserDomain() {
        return userDomain;
    }

    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
