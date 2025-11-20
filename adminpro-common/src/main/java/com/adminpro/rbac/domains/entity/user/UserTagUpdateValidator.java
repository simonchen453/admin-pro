package com.adminpro.rbac.domains.entity.user;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.core.base.validator.IValidatorGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户标签 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
public class UserTagUpdateValidator extends BaseValidator<UserTagEntity> {

    @Autowired
    private UserTagService userTagService;

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
