package com.adminpro.rbac.domains.entity.dept;

import com.adminpro.core.base.enums.CommonStatus;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.framework.common.helper.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 部门 校验类
 *
 * @author simon
 * @date 2020-05-24
 */
@Component
public class DeptCreateValidator extends BaseValidator<DeptEntity> {

    @Autowired
    private DeptService deptService;

    /**
     * 校验创建部门
     */
    public void validate(DeptEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
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
