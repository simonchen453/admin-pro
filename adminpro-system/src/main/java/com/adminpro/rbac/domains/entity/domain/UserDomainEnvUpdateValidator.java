package com.adminpro.rbac.domains.entity.domain;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.core.base.validator.IValidatorGroup;
import com.adminpro.framework.common.helper.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 环境配置 校验类
 *
 * @author simon
 * @date 2020-06-14
 */
@Component
public class UserDomainEnvUpdateValidator extends BaseValidator<UserDomainEnvEntity> {

    @Autowired
    private UserDomainEnvService userDomainEnvService;

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
