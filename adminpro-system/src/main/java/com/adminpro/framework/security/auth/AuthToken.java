package com.adminpro.framework.security.auth;

import java.io.Serializable;

public class AuthToken implements Serializable {
    private String userDomain;
    private String userId;
    private String token;

    public AuthToken(String userDomain, String userId) {
        this.userDomain = userDomain;
        this.userId = userId;
    }

    public AuthToken(String userDomain, String userId, String token) {
        this.userDomain = userDomain;
        this.userId = userId;
        this.token = token;
    }

    public String getUserDomain() {
        return userDomain;
    }

    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
