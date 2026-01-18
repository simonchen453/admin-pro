package com.adminpro.system.rbac.domains.entity.usermenu;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.framework.base.validator.IValidatorGroup;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 用户菜单分配 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
@RequiredArgsConstructor
public class UserMenuAssignUpdateValidator extends BaseValidator<UserMenuAssignEntity> {

    private final UserMenuAssignService userMenuAssignService;

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
