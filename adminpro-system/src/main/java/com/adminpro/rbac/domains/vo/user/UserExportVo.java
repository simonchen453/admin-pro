package com.adminpro.rbac.domains.vo.user;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 用户导出VO
 */
@Data
public class UserExportVo {
    
    @Excel(name = "用户域", orderNum = "0", width = 15)
    private String userDomain;
    
    @Excel(name = "登录名", orderNum = "1", width = 20)
    private String loginName;
    
    @Excel(name = "用户姓名", orderNum = "2", width = 15)
    private String realName;
    
    @Excel(name = "显示昵称", orderNum = "3", width = 15)
    private String display;
    
    @Excel(name = "手机号", orderNum = "4", width = 15)
    private String mobileNo;
    
    @Excel(name = "邮箱", orderNum = "5", width = 25)
    private String email;
    
    @Excel(name = "状态", orderNum = "6", width = 10, replace = {"正常_active", "停用_inactive", "锁定_locked"})
    private String status;
    
    @Excel(name = "性别", orderNum = "7", width = 10, replace = {"男_male", "女_female"})
    private String sex;
    
    @Excel(name = "描述", orderNum = "8", width = 30)
    private String description;
    
    @Excel(name = "部门编号", orderNum = "9", width = 15)
    private String deptNo;
    
    @Excel(name = "工号", orderNum = "10", width = 15)
    private String jobNo;
    
    @Excel(name = "地址", orderNum = "11", width = 30)
    private String address;
    
    @Excel(name = "生日", orderNum = "12", width = 15, format = "yyyy-MM-dd")
    private Date birthday;
    
    @Excel(name = "身份证号", orderNum = "13", width = 20)
    private String idNo;
    
    @Excel(name = "最后登录时间", orderNum = "14", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Date latestLoginTime;
    
    @Excel(name = "创建时间", orderNum = "15", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;
}

