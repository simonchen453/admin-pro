package com.adminpro.system.rbac.domains.entity.role;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 角色 校验类
 *
 * @author simon
 * @date 2020-06-08
 */
@Component
@RequiredArgsConstructor
public class RoleCreateValidator extends BaseValidator<RoleEntity> {

    private final RoleService roleService;

    /**
     * 校验创建角色
     */
    @Override
    public void validate(RoleEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
        if (!msgBundle.hasErrorMessage("name")) {
            RoleEntity role = roleService.findByName(entity.getName());
            if (role != null) {
                msgBundle.addErrorMessage("name", "角色名称不能重复");
            }
        }
    }
}
