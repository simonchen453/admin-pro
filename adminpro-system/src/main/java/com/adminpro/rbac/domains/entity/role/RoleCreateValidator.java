package com.adminpro.rbac.domains.entity.role;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 角色 校验类
 *
 * @author simon
 * @date 2020-06-08
 */
@Component
public class RoleCreateValidator extends BaseValidator<RoleEntity> {

    @Autowired
    private RoleService roleService;

    /**
     * 校验创建角色
     */
    @Override
    public void validate(RoleEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
        if (!msgBundle.hasErrorMessage("name")) {
            RoleEntity role = roleService.findByName(entity.getName());
            if(role != null){
                msgBundle.addErrorMessage("name", "角色名称不能重复");
            }
        }
    }
}
