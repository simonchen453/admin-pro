package com.adminpro.system.rbac.domains.entity.role;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.framework.base.validator.IValidatorGroup;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 角色 校验类
 *
 * @author simon
 * @date 2020-06-08
 */
@Component
@RequiredArgsConstructor
public class RoleUpdateValidator extends BaseValidator<RoleEntity> {

    private final RoleService roleService;

    /**
     * 校验更新角色
     */
    @Override
    public void validate(RoleEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle, IValidatorGroup.Update.class);
        if (!msgBundle.hasErrorMessage("id")) {
            if (!StringUtils.isEmpty(entity.getId())) {
                RoleEntity roleEntity = roleService.findById(entity.getId());
                if (roleEntity == null) {
                    msgBundle.addErrorMessage("id", "角色不存在");
                }
            }
        }
        if (!msgBundle.hasErrorMessage("name")) {
            RoleEntity role = roleService.findByName(entity.getName());
            if (role != null && !StringUtils.equals(role.getId(), entity.getId())) {
                msgBundle.addErrorMessage("name", "角色名称不能重复");
            }
        }
    }
}
