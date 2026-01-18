package com.adminpro.system.rbac.domains.entity.domain;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.framework.base.validator.IValidatorGroup;
import com.adminpro.system.core.common.helper.StringHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 用户域 校验类
 *
 * @author simon
 * @date 2020-06-14
 */
@Component
@RequiredArgsConstructor
public class DomainUpdateValidator extends BaseValidator<DomainEntity> {

    private final DomainService domainService;

    /**
     * 校验更新用户域
     */
    @Override
    public void validate(DomainEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle, IValidatorGroup.Update.class);
        if (!msgBundle.hasErrorMessage("id")) {
            if (!StringUtils.isEmpty(entity.getId())) {
                DomainEntity userDomainEntity = domainService.findById(entity.getId());
                if (userDomainEntity == null) {
                    msgBundle.addErrorMessage("id", "用户域不存在");
                }
            }
        }
        if (!msgBundle.hasErrorMessage("name")) {
            DomainEntity domainEntity = domainService.findByName(entity.getName());
            if (domainEntity != null && !StringHelper.equals(domainEntity.getId(), entity.getId())) {
                msgBundle.addErrorMessage("name", "名称不能重复");
            }

            DomainEntity entity1 = domainService.findById(entity.getId());
            if (entity1 != null && !StringHelper.equals(entity1.getName(), entity.getName())) {
                msgBundle.addErrorMessage("name", "名称不能修改");
            }
        }
    }
}
