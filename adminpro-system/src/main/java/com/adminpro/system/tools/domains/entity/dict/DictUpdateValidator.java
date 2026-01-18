package com.adminpro.system.tools.domains.entity.dict;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.framework.base.validator.IValidatorGroup;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 字典类型 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
@RequiredArgsConstructor
public class DictUpdateValidator extends BaseValidator<DictEntity> {

    private final DictService dictService;

    /**
     * 校验更新字典类型
     */
    public void validate(DictEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle, IValidatorGroup.Update.class);
        if (!msgBundle.hasErrorMessage("id")) {
            if (!StringUtils.isEmpty(entity.getId())) {
                DictEntity dictEntity = dictService.findById(entity.getId());
                if (dictEntity == null) {
                    msgBundle.addErrorMessage("id", "字典类型不存在");
                }
            }
        }
        if (!msgBundle.hasErrorMessage("key")) {
            DictEntity dictEntity = dictService.findByKey(entity.getKey());
            if (dictEntity != null && !StringUtils.equals(dictEntity.getKey(), entity.getKey())) {
                msgBundle.addErrorMessage("key", "字典键值不能重复");
            }
        }
    }
}
