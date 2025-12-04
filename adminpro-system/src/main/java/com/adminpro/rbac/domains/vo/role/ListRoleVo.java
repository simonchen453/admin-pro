package com.adminpro.rbac.domains.vo.role;

import com.adminpro.core.base.entity.BaseSystemVO;

/**
 * listing page vo
 *
 * @author simon
 */
public class ListRoleVo extends BaseSystemVO {
    private String id;

    private String name;

    private String display;

    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
