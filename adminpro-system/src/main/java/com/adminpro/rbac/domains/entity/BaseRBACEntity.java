package com.adminpro.rbac.domains.entity;

import com.adminpro.core.base.entity.BaseAuditEntity;

public abstract class BaseRBACEntity extends BaseAuditEntity {
    private boolean system;

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }
}
