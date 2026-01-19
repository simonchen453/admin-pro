package com.adminpro.system.rbac.domains.vo.device;

import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.system.rbac.domains.entity.device.UserDeviceEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceSearchForm extends BaseSearchForm<UserDeviceEntity> {
    private String userId;
    private String deviceName;
    private Integer isActive;
}
