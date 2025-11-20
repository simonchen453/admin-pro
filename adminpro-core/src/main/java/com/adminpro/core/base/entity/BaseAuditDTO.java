package com.adminpro.core.base.entity;


import java.util.Date;

/**
 * 添加审查字段，创建者，创建时间，更新者，更新时间
 *
 * @author simon
 */
public abstract class BaseAuditDTO extends BaseDTO {
    private String createdByUserId;
    private String createdByUserDomain;
    private Date createdDate;

    private String updatedByUserId;
    private String updatedByUserDomain;
    private Date updatedDate;

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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
