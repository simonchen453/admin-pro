package com.adminpro.system.tools.domains.entity.config;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.system.core.common.helper.StringHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 参数配置 校验类
 *
 * @author simon
 * @date 2020-06-15
 */
@Component
@RequiredArgsConstructor
public class ConfigCreateValidator extends BaseValidator<ConfigEntity> {

    private final ConfigService configService;

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
