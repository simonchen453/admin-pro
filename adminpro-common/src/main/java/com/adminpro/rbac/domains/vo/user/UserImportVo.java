package com.adminpro.rbac.domains.vo.user;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 用户导入VO
 */
@Data
public class UserImportVo {
    
    @Excel(name = "用户域", orderNum = "0", isImportField = "true")
    private String userDomain;
    
    @Excel(name = "登录名", orderNum = "1", isImportField = "true")
    private String loginName;
    
    @Excel(name = "用户姓名", orderNum = "2", isImportField = "true")
    private String realName;
    
    @Excel(name = "显示昵称", orderNum = "3", isImportField = "true")
    private String display;
    
    @Excel(name = "手机号", orderNum = "4", isImportField = "true")
    private String mobileNo;
    
    @Excel(name = "邮箱", orderNum = "5", isImportField = "true")
    private String email;
    
    @Excel(name = "状态", orderNum = "6", isImportField = "true", replace = {"正常_active", "停用_inactive", "锁定_locked"})
    private String status;
    
    @Excel(name = "性别", orderNum = "7", isImportField = "true")
    private String sex;
    
    @Excel(name = "描述", orderNum = "8", isImportField = "true")
    private String description;
    
    @Excel(name = "部门编号", orderNum = "9", isImportField = "true")
    private String deptNo;
    
    @Excel(name = "工号", orderNum = "10", isImportField = "true")
    private String jobNo;
    
    @Excel(name = "地址", orderNum = "11", isImportField = "true")
    private String address;
    
    @Excel(name = "生日", orderNum = "12", isImportField = "true", format = "yyyy-MM-dd")
    private Date birthday;
    
    @Excel(name = "身份证号", orderNum = "13", isImportField = "true")
    private String idNo;
    
    @Excel(name = "密码", orderNum = "14", isImportField = "true")
    private String password;
}

