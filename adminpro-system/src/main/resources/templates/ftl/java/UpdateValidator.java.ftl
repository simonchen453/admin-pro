package ${package};
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.enums.CommonStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Component;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.core.base.validator.IValidatorGroup;

/**
 * ${tableComment} 校验类
 *
 * @author ${author}
 * @date ${datetime}
 */
@Component
public class ${className}UpdateValidator extends BaseValidator<${className}Entity> {

    @Autowired
    private ${className}Service ${classname}Service;

    /**
     * 校验更新${tableComment}
     */
    @Override
    public void validate(${className}Entity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle, IValidatorGroup.Update.class);
        if (!msgBundle.hasErrorMessage("${primaryKey.attrname}")) {
            if (!StringUtils.isEmpty(entity.get${primaryKey.attrName}())) {
                ${className}Entity ${classname}Entity = ${classname}Service.findById(entity.get${primaryKey.attrName}());
                if (${classname}Entity == null) {
                    msgBundle.addErrorMessage("id", "${tableComment}不存在");
                }
            }
        }

        <#list entityColumns as column>
            <#if column.attrname == "status" >
                if (!msgBundle.hasErrorMessage("status")) {
                    if (!CommonStatus.isValidCode(entity.getStatus())) {
                        msgBundle.addErrorMessage("status", "状态不合法");
                    }
                }
            </#if>
        </#list>
    }
}

