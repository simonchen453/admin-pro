package com.adminpro.system.rbac.domains.entity.usermenu;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户菜单分配 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
public class UserMenuAssignCreateValidator extends BaseValidator<UserMenuAssignEntity> {

    @Autowired
    private UserMenuAssignService userMenuAssignService;

    /**
     * 校验创建用户菜单分配
     */
    public void validate(UserMenuAssignEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
    }
}
