package com.adminpro.system.rbac.domains.entity.domain;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.framework.base.validator.IValidatorGroup;
import com.adminpro.system.core.common.helper.StringHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 环境配置 校验类
 *
 * @author simon
 * @date 2020-06-14
 */
@Component
@RequiredArgsConstructor
public class UserDomainEnvUpdateValidator extends BaseValidator<UserDomainEnvEntity> {

    private final UserDomainEnvService userDomainEnvService;

    /**
     * 校验更新环境配置
     */
    @Override
    public void validate(UserDomainEnvEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle, IValidatorGroup.Update.class);
        if (!msgBundle.hasErrorMessage("userDomain")) {
            if (!StringUtils.isEmpty(entity.getId())) {
                UserDomainEnvEntity userDomainEnvEntity = userDomainEnvService.findById(entity.getId());
                if (userDomainEnvEntity == null) {
                    msgBundle.addErrorMessage("userDomain", "环境配置不存在");
                }
            }
        }
        if (!msgBundle.hasErrorMessage("userDomain")) {
            UserDomainEnvEntity domainEnvEntity = userDomainEnvService.findByUserDomain(entity.getUserDomain());
            if (domainEnvEntity != null && !StringHelper.equals(domainEnvEntity.getId(), entity.getId())) {
                msgBundle.addErrorMessage("userDomain", "用户域不能重复");
            }
        }
    }
}
