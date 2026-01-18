package com.adminpro.system.rbac.domains.entity.dept;

import com.adminpro.framework.base.enums.CommonStatus;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import com.adminpro.framework.base.validator.IValidatorGroup;
import com.adminpro.system.core.common.helper.StringHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 部门 校验类
 *
 * @author simon
 * @date 2020-05-24
 */
@Component
@RequiredArgsConstructor
public class DeptUpdateValidator extends BaseValidator<DeptEntity> {

    private final DeptService deptService;

    /**
     * 校验更新部门
     */
    public void validate(DeptEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle, IValidatorGroup.Update.class);
        if (!msgBundle.hasErrorMessage("id")) {
            if (!StringUtils.isEmpty(entity.getId())) {
                DeptEntity deptEntity = deptService.findById(entity.getId());
                if (deptEntity == null) {
                    msgBundle.addErrorMessage("id", "部门不存在");
                }
            }
        }
        if (!msgBundle.hasErrorMessage("no")) {
            DeptEntity deptEntity = deptService.findByNo(entity.getNo());
            if (deptEntity != null && !StringHelper.equals(deptEntity.getId(), entity.getId())) {
                msgBundle.addErrorMessage("no", "部门编号不能重复");
            }
        }
        if (!msgBundle.hasErrorMessage("status")) {
            if (!CommonStatus.isValidCode(entity.getStatus())) {
                msgBundle.addErrorMessage("status", "状态不合法");
            }
        }
    }
}
