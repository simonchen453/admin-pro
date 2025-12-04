package com.adminpro.tools.domains.entity.config;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.framework.common.helper.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 参数配置 校验类
 *
 * @author simon
 * @date 2020-06-15
 */
@Component
public class ConfigCreateValidator extends BaseValidator<ConfigEntity> {

    @Autowired
    private ConfigService configService;

    /**
     * 校验创建参数配置
     */
    @Override
    public void validate(ConfigEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
        if (!msgBundle.hasErrorMessage("key")) {
            ConfigEntity configEntity = configService.findByKey(entity.getKey());
            if (configEntity != null && !StringHelper.equals(configEntity.getId(), entity.getId())) {
                msgBundle.addErrorMessage("key", "Key不能重复");
            }
        }
    }
}
