package com.adminpro.tools.domains.entity.dict;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.core.base.validator.IValidatorGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 字典数据 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
public class DictDataUpdateValidator extends BaseValidator<DictDataEntity> {

    @Autowired
    private DictDataService dictDataService;

    /**
     * 校验更新字典数据
     */
    public void validate(DictDataEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle, IValidatorGroup.Update.class);
        if (!msgBundle.hasErrorMessage("id")) {
            if (!StringUtils.isEmpty(entity.getId())) {
                DictDataEntity dictDataEntity = dictDataService.findById(entity.getId());
                if (dictDataEntity == null) {
                    msgBundle.addErrorMessage("id", "字典数据不存在");
                }
            }
        }
    }
}
