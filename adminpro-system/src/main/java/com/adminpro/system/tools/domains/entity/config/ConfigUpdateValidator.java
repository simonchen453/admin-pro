package com.adminpro.system.tools.domains.entity.config;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.framework.base.validator.IValidatorGroup;
import com.adminpro.system.core.common.helper.StringHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 参数配置 校验类
 *
 * @author simon
 * @date 2020-06-15
 */
@Component
@RequiredArgsConstructor
public class ConfigUpdateValidator extends BaseValidator<ConfigEntity> {

    private final ConfigService configService;

    /**
     * 校验更新参数配置
     */
    @Override
    public void validate(ConfigEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle, IValidatorGroup.Update.class);
        if (!msgBundle.hasErrorMessage("id")) {
            if (!StringUtils.isEmpty(entity.getId())) {
                ConfigEntity configEntity = configService.findById(entity.getId());
                if (configEntity == null) {
                    msgBundle.addErrorMessage("id", "参数配置不存在");
                }
            }
        }
        if (!msgBundle.hasErrorMessage("key")) {
            ConfigEntity entityDb = configService.findById(entity.getId());
            if (!StringHelper.equals(entityDb.getKey(), entity.getKey())) {
                msgBundle.addErrorMessage("key", "Key不能修改");
            }
        }
    }
}
