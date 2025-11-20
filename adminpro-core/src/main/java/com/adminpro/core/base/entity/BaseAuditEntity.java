package com.adminpro.core.base.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * 添加审查字段，创建者，创建时间，更新者，更新时间
 */
public abstract class BaseAuditEntity extends BaseEntity {
    private String createdByUserId;
    private String createdByUserDomain;
    private Date createdDate;

    private String updatedByUserId;
    private String updatedByUserDomain;
    private Date updatedDate;

    public static final String COL_CREATED_DATE = "COL_CREATED_DATE";
    public static final String COL_CREATED_BY_USER_ID = "COL_CREATED_BY_USER_ID";
    public static final String COL_CREATED_BY_USER_DOMAIN = "COL_CREATED_BY_USER_DOMAIN";
    public static final String COL_UPDATED_DATE = "COL_UPDATED_DATE";
    public static final String COL_UPDATED_BY_USER_ID = "COL_UPDATED_BY_USER_ID";
    public static final String COL_UPDATED_BY_USER_DOMAIN = "COL_UPDATED_BY_USER_DOMAIN";

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getCreatedByUserDomain() {
        return createdByUserDomain;
    }

    public void setCreatedByUserDomain(String createdByUserDomain) {
        this.createdByUserDomain = createdByUserDomain;
    }

    public String getUpdatedByUserId() {
        return updatedByUserId;
    }

    public void setUpdatedByUserId(String updatedByUserId) {
        this.updatedByUserId = updatedByUserId;
    }

    public String getUpdatedByUserDomain() {
        return updatedByUserDomain;
    }

    public void setUpdatedByUserDomain(String updatedByUserDomain) {
        this.updatedByUserDomain = updatedByUserDomain;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @JsonIgnore
    public void emptyAuditTime() {
        this.createdDate = null;
        this.updatedDate = null;
    }

    @JsonIgnore
    public void emptyAudit() {
        this.createdDate = null;
        this.updatedDate = null;
        this.createdByUserDomain = null;
        this.createdByUserId = null;
        this.updatedByUserDomain = null;
        this.updatedByUserId = null;
    }
}
