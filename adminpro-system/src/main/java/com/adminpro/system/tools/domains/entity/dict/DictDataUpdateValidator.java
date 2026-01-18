package com.adminpro.system.tools.domains.entity.dict;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.framework.base.validator.IValidatorGroup;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 字典数据 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
@RequiredArgsConstructor
public class DictDataUpdateValidator extends BaseValidator<DictDataEntity> {

    private final DictDataService dictDataService;

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
