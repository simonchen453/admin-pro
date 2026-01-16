package com.adminpro.system.rbac.domains.entity.user;

import com.adminpro.system.rbac.common.RbacConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Created by simon on 2017/5/29.
 */
public class UserIden implements Serializable {
    private String loginName;
    private String userDomain;

    public UserIden() {
    }

    public UserIden(String userDomain, String loginName) {
        this.loginName = loginName;
        this.userDomain = userDomain;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserDomain() {
        return userDomain;
    }

    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    @JsonIgnore
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserIden userIden = (UserIden) o;

        if (!StringUtils.equals(userIden.loginName, loginName)) {
            return false;
        }
        return StringUtils.equals(userIden.userDomain, userDomain);
    }

    @Override
    public int hashCode() {
        int result = loginName != null ? loginName.hashCode() : 0;
        result = 31 * result + (userDomain != null ? userDomain.hashCode() : 0);
        return result;
    }

    @JsonIgnore
    public String toSecurityUsername() {
        return userDomain + RbacConstants.SPRING_SECURITY_USERIDEN_SPLIT + loginName;
    }

    @JsonIgnore
    public static UserIden fromSecurityUserName(String username) {
        if (StringUtils.isNotEmpty(username)) {
            String[] split = username.split(RbacConstants.SPRING_SECURITY_USERIDEN_SPLIT);
            if (ArrayUtils.isNotEmpty(split) && split.length == 2) {
                return new UserIden(split[0], split[1]);
            }
        }

        return null;
    }
}
