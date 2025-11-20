package com.adminpro.core.base.entity;

public abstract class BaseSystemVO extends BaseAuditVO {
    private boolean system;

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }
}
