package com.adminpro.rbac.domains.vo.user;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.ValidationUtil;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.rbac.api.PasswordValidator;
import com.adminpro.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.rbac.domains.entity.dept.DeptService;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.rbac.domains.entity.user.UserService;
import com.adminpro.rbac.enums.UserStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class UserCreateValidator extends BaseValidator<UserCreateVo> {

    @Autowired
    UserService userService;

    @Override
    public void validate(UserCreateVo model, MessageBundle msgBundle) {
        String password = model.getPassword();
        String confirmPassword = model.getConfirmPassword();
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(confirmPassword)) {
            msgBundle.addErrorMessage("password", "密码不能为空");
        } else {
            if (!StringUtils.equals(password, confirmPassword)) {
                msgBundle.addErrorMessage("confirmPassword", "两次输入的密码不一致");
            } else {
                List<String> passwordErrors = PasswordValidator.validatePassword(password);
                if (passwordErrors != null && !passwordErrors.isEmpty()) {
                    msgBundle.addErrorMessage("password", String.join("；", passwordErrors));
                }
            }
        }

        if (!msgBundle.hasErrorMessage("deptId")) {
            if (StringUtils.isEmpty(model.getDeptId())) {
                msgBundle.addErrorMessage("deptId", "所属部门不能为空");
            } else {
                DeptEntity deptEntity = DeptService.getInstance().findById(model.getDeptId());
                if (deptEntity == null) {
                    msgBundle.addErrorMessage("deptId", "所属部门不存在");
                }
            }
        }

        if (!msgBundle.hasErrorMessage("realName")) {
            if (StringUtils.isEmpty(model.getRealName())) {
                msgBundle.addErrorMessage("realName", "用户姓名不能为空");
            }
        }
        /*if (!msgBundle.hasErrorMessage("mobileNo")) {
            if (StringUtils.isEmpty(model.getMobileNo())) {
                msgBundle.addErrorMessage("mobileNo", "手机号码不能为空");
            } else {
                UserEntity userEntity = userService.findByDomainAndMobileNo(model.getUserDomain(), model.getMobileNo());
                if (userEntity != null && !userEntity.getUserIden().equals(new UserIden(model.getUserDomain(), model.getUserId()))) {
                    msgBundle.addErrorMessage("mobileNo", "手机号码不能重复");
                }
            }
        }*/
        if (!msgBundle.hasErrorMessage("userDomain")) {
            if (StringUtils.isEmpty(model.getUserDomain())) {
                msgBundle.addErrorMessage("userDomain", "用户域不能为空");
            }
        }
        if (!msgBundle.hasErrorMessage("status")) {
            if (StringUtils.isEmpty(model.getStatus())) {
                msgBundle.addErrorMessage("status", "状态不能为空");
            } else if (!UserStatus.isValidStatus(model.getStatus())) {
                msgBundle.addErrorMessage("status", "状态值不合法");
            }
        }
        if (!msgBundle.hasErrorMessage("roleIds")) {
            if (CollectionUtils.isEmpty(model.getRoleIds())) {
                msgBundle.addErrorMessage("roleIds", "角色不能为空");
            }
        }
        /*if (!msgBundle.hasErrorMessage("postIds")) {
            if (CollectionUtils.isEmpty(model.getPostIds())) {
                msgBundle.addErrorMessage("postIds", "职位不能为空");
            }
        }*/
        if (!msgBundle.hasErrorMessage("loginName")) {
            //create
            if (StringUtils.isEmpty(model.getUserId())) {
                if (StringUtils.isEmpty(model.getLoginName())) {
                    msgBundle.addErrorMessage("loginName", "登录账号不能为空");
                } else {
                    UserEntity userEntity = userService.findByUserDomainAndLoginName(model.getUserDomain(), model.getLoginName());
                    if (userEntity != null) {
                        msgBundle.addErrorMessage("loginName", "登录账号已存在");
                    }
                }
            } else {
                //update
                if (StringUtils.isEmpty(model.getLoginName())) {
                    msgBundle.addErrorMessage("loginName", "登录账号不能为空");
                } else {
                    UserEntity userEntity = userService.findByUserDomainAndLoginName(model.getUserDomain(), model.getLoginName());
                    if (userEntity != null && !userEntity.getUserIden().getUserId().equals(model.getUserId())) {
                        msgBundle.addErrorMessage("loginName", "登录账号已存在");
                    }
                }
            }
        }

    }
}
