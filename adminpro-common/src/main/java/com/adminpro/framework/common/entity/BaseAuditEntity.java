package com.adminpro.framework.common.entity;

import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.rbac.domains.entity.user.UserIden;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

/**
 * 添加审查字段，创建者，创建时间，更新者，更新时间
 */
public abstract class BaseAuditEntity extends com.adminpro.core.base.entity.BaseAuditEntity {
    public UserIden getCreatedUserIden() {
        if (StringHelper.isNotEmpty(getCreatedByUserDomain()) && StringHelper.isNotEmpty(getCreatedByUserId())) {
            return new UserIden(getCreatedByUserDomain(), getCreatedByUserId());
        } else {
            return null;
        }
    }

    public UserIden getUpdatedUserIden() {
        if (StringHelper.isNotEmpty(getUpdatedByUserDomain()) && StringHelper.isNotEmpty(getUpdatedByUserId())) {
            return new UserIden(getUpdatedByUserDomain(), getUpdatedByUserId());
        } else {
            return null;
        }
    }

    @Override
    @JsonIgnore
    public void emptyAuditTime() {
        this.setCreatedDate(null);
        this.setUpdatedDate(null);
    }

    @JsonIgnore
    public boolean isOwner(UserIden userIden) {
        if (userIden == null) {
            return false;
        }

        if (StringUtils.equals(userIden.getUserDomain(), getCreatedByUserDomain()) && StringUtils.equals(userIden.getUserId(), getCreatedByUserId())) {
            return true;
        } else {
            return false;
        }
    }
}
