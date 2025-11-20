package com.adminpro.rbac.domains.entity.authcode;

import com.adminpro.core.base.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * 短信验证码类，放在redis中
 */
@Data
public class AuthCodeEntity extends BaseEntity {

    private String mobileNo;

    private String code;

    private String type;

    private String platform;

    private Date expireTime;

    @JsonIgnore
    public boolean isExpired() {
        Date now = new Date();
        return getExpireTime().before(now);
    }
}
