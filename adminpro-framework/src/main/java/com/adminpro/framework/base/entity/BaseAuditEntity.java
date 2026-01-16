package com.adminpro.framework.base.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * 添加审查字段，创建者，创建时间，更新者，更新时间
 */
public abstract class BaseAuditEntity extends BaseEntity {
    private String createdBy;
    private Date createdAt;

    private String updatedBy;
    private Date updatedAt;

    public static final String COL_CREATED_AT = "COL_CREATED_AT";
    public static final String COL_CREATED_BY = "COL_CREATED_BY";
    public static final String COL_UPDATED_AT = "COL_UPDATED_AT";
    public static final String COL_UPDATED_BY = "COL_UPDATED_BY";

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonIgnore
    public void emptyAuditTime() {
        this.createdAt = null;
        this.updatedAt = null;
    }

    @JsonIgnore
    public void emptyAudit() {
        this.createdAt = null;
        this.updatedAt = null;
        this.createdBy = null;
        this.updatedBy = null;
    }
}
