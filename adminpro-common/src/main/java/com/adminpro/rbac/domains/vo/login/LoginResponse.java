package com.adminpro.rbac.domains.vo.login;


import com.adminpro.core.base.entity.BaseVO;

/**
 * 登陆返回Vo
 */
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

    private String platform;

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

    public String getExtUserId() {
        return extUserId;
    }

    public void setExtUserId(String extUserId) {
        this.extUserId = extUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getHasPayPwd() {
        return hasPayPwd;
    }

    public void setHasPayPwd(Boolean hasPayPwd) {
        this.hasPayPwd = hasPayPwd;
    }

    public Boolean getAuthed() {
        return authed;
    }

    public void setAuthed(Boolean authed) {
        this.authed = authed;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPostNo() {
        return postNo;
    }

    public void setPostNo(String postNo) {
        this.postNo = postNo;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getSoftCardId() {
        return softCardId;
    }

    public void setSoftCardId(String softCardId) {
        this.softCardId = softCardId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }
}
