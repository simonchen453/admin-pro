package com.adminpro.system.core.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

/**
 * 添加审查字段，创建者，创建时间，更新者，更新时间
 */
public abstract class BaseAuditDTO extends com.adminpro.framework.base.entity.BaseAuditDTO {

    @JsonIgnore
    public boolean isOwner(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return false;
        }
        if (StringUtils.equals(userId, getCreatedBy())) {
            return true;
        } else {
            return false;
        }
    }
}
