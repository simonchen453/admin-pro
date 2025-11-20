package com.adminpro.tools.domains.entity.dict;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.core.base.validator.IValidatorGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 字典类型 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
public class DictUpdateValidator extends BaseValidator<DictEntity> {

    @Autowired
    private DictService dictService;

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
