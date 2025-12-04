package com.adminpro.rbac.domains.vo.role;

import com.adminpro.core.base.entity.BaseAuditVO;

import java.util.List;
import java.util.Map;

/**
 * view Role vo
 */
public class RoleResponseVo extends BaseAuditVO {

    private String id;

    private String name;

    private String display;


    /**
     * 已经关联的权限
     */
    private List<Map<String, String>> assignedPrivileges;

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

    public List<Map<String, String>> getAssignedPrivileges() {
        return assignedPrivileges;
    }

    public void setAssignedPrivileges(List<Map<String, String>> assignedPrivileges) {
        this.assignedPrivileges = assignedPrivileges;
    }
}
