package com.adminpro.system.rbac.domains.vo.login;


import com.adminpro.framework.base.entity.BaseVO;
import lombok.Data;

/**
 * 登陆返回Vo
 */
@Data
public class LoginResponse extends BaseVO {

    /**
     * 用户UUID
     */
    private String id;
    /**
     * 外部SDK关联userid
     */
    private String extUserId;
    /**
     * 用户名
     */
    private String userId;
    /**
     * 显示名称
     */
    private String display;
    /**
     * token信息
     */
    private String token;
    /**
     * 是否设置支付密码
     */
    private Boolean hasPayPwd;
    /**
     * 是否实名认证
     */
    private Boolean authed;

    /**
     * 身份证号
     */
    private String idNo;

    /**
     * 真实姓名
     */
    private String realName;

    private String domain;

    private String mobileNo;

    /**
     * 职务
     */
    private String post;

    /**
     * 职务编号
     */
    private String postNo;

    /**
     * 头像
     */
    private String avatarUrl;

    private String softCardId;

    private String date;

    private String week;
    private String deptName;
    private String deptNo;
}
