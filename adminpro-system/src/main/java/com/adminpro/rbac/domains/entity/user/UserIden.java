package com.adminpro.rbac.domains.entity.user;

import com.adminpro.rbac.common.RbacConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Created by simon on 2017/5/29.
 */
public class UserIden implements Serializable {
    private String userId;
    private String userDomain;

    public UserIden() {
    }

    public UserIden(String userDomain, String userId) {
        this.userId = userId;
        this.userDomain = userDomain;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

        if (!StringUtils.equals(userIden.userId, userId)) {
            return false;
        }
        return StringUtils.equals(userIden.userDomain, userDomain);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + userDomain.hashCode();
        return result;
    }

    @JsonIgnore
    public String toSecurityUsername() {
        return userDomain + RbacConstants.SPRING_SECURITY_USERIDEN_SPLIT + userId;
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
