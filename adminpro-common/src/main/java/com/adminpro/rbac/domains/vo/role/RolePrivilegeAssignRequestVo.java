package com.adminpro.rbac.domains.vo.role;

import com.adminpro.core.base.entity.BaseVO;

public class RolePrivilegeAssignRequestVo extends BaseVO {

    private String roleId;

    private String[] privilegeIds;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String[] getPrivilegeIds() {
        return privilegeIds;
    }

    public void setPrivilegeIds(String[] privilegeIds) {
        this.privilegeIds = privilegeIds;
    }
}
