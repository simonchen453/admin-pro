package com.adminpro.rbac.domains.entity.usermenu;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.core.base.validator.IValidatorGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户菜单分配 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
public class UserMenuAssignUpdateValidator extends BaseValidator<UserMenuAssignEntity> {

    @Autowired
    private UserMenuAssignService userMenuAssignService;

    /**
     * 校验更新用户菜单分配
     */
    public void validate(UserMenuAssignEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle, IValidatorGroup.Update.class);
        if (!msgBundle.hasErrorMessage("id")) {
            if (!StringUtils.isEmpty(entity.getId())) {
                UserMenuAssignEntity userMenuAssignEntity = userMenuAssignService.findById(entity.getId());
                if (userMenuAssignEntity == null) {
                    msgBundle.addErrorMessage("id", "用户菜单分配不存在");
                }
            }
        }
    }
}
