package com.adminpro.rbac.domains.entity.user;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户标签 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
public class UserTagCreateValidator extends BaseValidator<UserTagEntity> {

    @Autowired
    private UserTagService userTagService;

    /**
     * 校验创建用户标签
     */
    public void validate(UserTagEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
    }
}
