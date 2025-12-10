package com.adminpro.system.rbac.domains.vo.user;

import com.adminpro.framework.base.entity.BaseVO;
import lombok.Data;

import java.util.Date;

/**
 * 用户信息响应VO
 */
@Data
public class UserInfoResponseVo extends BaseVO {
    private String userId;
    private String userDomain;
    private String loginName;
    private String realName;
    private String mobileNo;
    private String email;
    private String avatarUrl;
    private String sex;
    private String status;
    private String deptNo;
    private String deptName;
    private String description;
    private Date latestLoginTime;
}

