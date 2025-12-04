package com.adminpro.system.rbac.domains.vo.user;

import com.adminpro.framework.base.entity.BaseVO;

public class ChangePwdVo extends BaseVO {
    private String oldPwd;
    private String newPwd;
    private String confirmNewPwd;

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public String getConfirmNewPwd() {
        return confirmNewPwd;
    }

    public void setConfirmNewPwd(String confirmNewPwd) {
        this.confirmNewPwd = confirmNewPwd;
    }
}
