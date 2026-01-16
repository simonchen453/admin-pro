package com.adminpro.framework.base.entity;

import java.util.Date;

/**
 * 添加审查字段，创建者，创建时间，更新者，更新时间
 *
 * @author simon
 */
public abstract class BaseAuditDTO extends BaseDTO {
    private String createdBy;
    private Date createdAt;

    private String updatedBy;
    private Date updatedAt;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
