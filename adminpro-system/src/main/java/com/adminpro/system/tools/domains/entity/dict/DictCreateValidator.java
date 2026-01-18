package com.adminpro.system.tools.domains.entity.dict;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 字典类型 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
@RequiredArgsConstructor
public class DictCreateValidator extends BaseValidator<DictEntity> {

    private final DictService dictService;

    /**
     * 校验创建字典类型
     */
    public void validate(DictEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
        if (!msgBundle.hasErrorMessage("key")) {
            DictEntity dictEntity = dictService.findByKey(entity.getKey());
            if (dictEntity != null) {
                msgBundle.addErrorMessage("key", "字典键值不能重复");
            }
        }
    }
}
