package com.adminpro.system.tools.domains.entity.dict;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 字典数据 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
@RequiredArgsConstructor
public class DictDataCreateValidator extends BaseValidator<DictDataEntity> {

    private final DictDataService dictDataService;

    /**
     * 校验创建字典数据
     */
    public void validate(DictDataEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
    }
}
