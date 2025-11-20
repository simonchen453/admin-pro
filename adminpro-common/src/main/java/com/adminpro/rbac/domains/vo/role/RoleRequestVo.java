package com.adminpro.rbac.domains.vo.role;

import com.adminpro.core.base.entity.BaseAuditVO;

/**
 * create,update Role vo
 */
public class RoleRequestVo extends BaseAuditVO {

    private String id;

    private String name;

    private String display;

    private String[] privilegeNames;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String[] getPrivilegeNames() {
        return privilegeNames;
    }

    public void setPrivilegeNames(String[] privilegeNames) {
        this.privilegeNames = privilegeNames;
    }
}
