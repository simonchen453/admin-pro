package com.adminpro.rbac.domains.vo.user;

import com.adminpro.core.base.entity.BaseVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户列表返回
 */
public class UserListResponseVo extends BaseVO {
    private String userId;

    private String userDomain;

    private String realName;
    private String loginName;
    private String mobileNo;
    private String avatarUrl;

    private Integer status;

    private String description;

    private Date latestLoginTime;

    private List<Map<String, String>> assignedRoles;

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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLatestLoginTime() {
        return latestLoginTime;
    }

    public void setLatestLoginTime(Date latestLoginTime) {
        this.latestLoginTime = latestLoginTime;
    }

    public List<Map<String, String>> getAssignedRoles() {
        return assignedRoles;
    }

    public void setAssignedRoles(List<Map<String, String>> assignedRoles) {
        this.assignedRoles = assignedRoles;
    }
}
