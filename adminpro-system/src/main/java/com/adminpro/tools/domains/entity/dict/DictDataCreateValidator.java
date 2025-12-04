package com.adminpro.tools.domains.entity.dict;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 字典数据 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
public class DictDataCreateValidator extends BaseValidator<DictDataEntity> {

    @Autowired
    private DictDataService dictDataService;

    /**
     * 校验创建字典数据
     */
    public void validate(DictDataEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
    }
}
