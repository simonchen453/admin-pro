package com.adminpro.system.rbac.domains.entity.user;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用户标签 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
@RequiredArgsConstructor
public class UserTagCreateValidator extends BaseValidator<UserTagEntity> {

    private final UserTagService userTagService;

    /**
     * 校验创建用户标签
     */
    public void validate(UserTagEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
    }
}
