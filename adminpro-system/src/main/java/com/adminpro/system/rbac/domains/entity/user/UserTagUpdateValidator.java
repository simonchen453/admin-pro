package com.adminpro.system.rbac.domains.entity.user;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.framework.base.validator.IValidatorGroup;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 用户标签 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
@RequiredArgsConstructor
public class UserTagUpdateValidator extends BaseValidator<UserTagEntity> {

    private final UserTagService userTagService;

    /**
     * 校验更新用户标签
     */
    public void validate(UserTagEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle, IValidatorGroup.Update.class);
        if (!msgBundle.hasErrorMessage("id")) {
            if (!StringUtils.isEmpty(entity.getId())) {
                UserTagEntity userTagEntity = userTagService.findById(entity.getId());
                if (userTagEntity == null) {
                    msgBundle.addErrorMessage("id", "用户标签不存在");
                }
            }
        }
    }
}
