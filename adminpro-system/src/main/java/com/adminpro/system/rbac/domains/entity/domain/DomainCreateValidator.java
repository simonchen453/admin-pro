package com.adminpro.system.rbac.domains.entity.domain;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.system.core.common.helper.StringHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用户域 校验类
 *
 * @author simon
 * @date 2020-06-14
 */
@Component
@RequiredArgsConstructor
public class DomainCreateValidator extends BaseValidator<DomainEntity> {

    private final DomainService domainService;

    /**
     * 校验创建用户域
     */
    @Override
    public void validate(DomainEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
        if (!msgBundle.hasErrorMessage("name")) {
            DomainEntity domainEntity = domainService.findByName(entity.getName());
            if (domainEntity != null && !StringHelper.equals(domainEntity.getId(), entity.getId())) {
                msgBundle.addErrorMessage("name", "名称不能重复");
            }
        }
    }
}
