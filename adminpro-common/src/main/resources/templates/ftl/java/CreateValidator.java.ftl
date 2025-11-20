package ${package};
import com.adminpro.core.base.enums.CommonStatus;
import com.adminpro.core.base.message.MessageBundle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Component;
import com.adminpro.core.base.validator.BaseValidator;

/**
 * ${tableComment} 校验类
 *
 * @author ${author}
 * @date ${datetime}
 */
@Component
public class ${className}CreateValidator extends BaseValidator<${className}Entity> {

    @Autowired
    private ${className}Service ${classname}Service;

    /**
     * 校验创建${tableComment}
     */
    @Override
    public void validate(${className}Entity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
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

