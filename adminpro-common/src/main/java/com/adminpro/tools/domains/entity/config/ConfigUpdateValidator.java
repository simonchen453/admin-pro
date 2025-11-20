package com.adminpro.tools.domains.entity.config;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.core.base.validator.IValidatorGroup;
import com.adminpro.framework.common.helper.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 参数配置 校验类
 *
 * @author simon
 * @date 2020-06-15
 */
@Component
public class ConfigUpdateValidator extends BaseValidator<ConfigEntity> {

    @Autowired
    private ConfigService configService;

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
