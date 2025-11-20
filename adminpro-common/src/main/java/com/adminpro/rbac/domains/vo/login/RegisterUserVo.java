package com.adminpro.rbac.domains.vo.login;

import com.adminpro.core.base.entity.BaseVO;

public class RegisterUserVo extends BaseVO {
    private String userid;

    private String password;

    private String platform;

    private String invitationCode;

    private String registCode;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public String getRegistCode() {
        return registCode;
    }

    public void setRegistCode(String registCode) {
        this.registCode = registCode;
    }

}
