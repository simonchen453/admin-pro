package com.adminpro.system.rbac.domains.entity.domain;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.system.core.common.helper.StringHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 环境配置 校验类
 *
 * @author simon
 * @date 2020-06-14
 */
@Component
@RequiredArgsConstructor
public class UserDomainEnvCreateValidator extends BaseValidator<UserDomainEnvEntity> {

    private final UserDomainEnvService userDomainEnvService;

    /**
     * 校验创建环境配置
     */
    @Override
    public void validate(UserDomainEnvEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
        if (!msgBundle.hasErrorMessage("userDomain")) {
            UserDomainEnvEntity domainEnvEntity = userDomainEnvService.findByUserDomain(entity.getUserDomain());
            if (domainEnvEntity != null && !StringHelper.equals(domainEnvEntity.getId(), entity.getId())) {
                msgBundle.addErrorMessage("userDomain", "用户域不能重复");
            }
        }
    }
}
