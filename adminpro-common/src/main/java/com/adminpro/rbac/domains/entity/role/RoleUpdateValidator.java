package com.adminpro.rbac.domains.entity.role;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.core.base.validator.IValidatorGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 角色 校验类
 *
 * @author simon
 * @date 2020-06-08
 */
@Component
public class RoleUpdateValidator extends BaseValidator<RoleEntity> {

    @Autowired
    private RoleService roleService;

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
            if(role != null && !StringUtils.equals(role.getId(), entity.getId())){
                msgBundle.addErrorMessage("name", "角色名称不能重复");
            }
        }
    }
}
