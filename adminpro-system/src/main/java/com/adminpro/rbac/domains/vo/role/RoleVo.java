package com.adminpro.rbac.domains.vo.role;

import com.adminpro.core.base.entity.BaseAuditVO;

/**
 * View,Edit Role vo
 */
public class RoleVo extends BaseAuditVO {

    private String id;

    private String name;

    private String display;

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
}
