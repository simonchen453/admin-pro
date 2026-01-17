package com.adminpro.system.rbac.domains.vo.user;

import com.adminpro.framework.base.entity.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户列表响应VO
 *
 * <p>
 * 用于返回用户列表查询结果，包含用户的基本信息、状态、登录时间及角色分配情况
 * </p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Schema(description = "用户列表响应VO")
public class UserListResponseVo extends BaseVO {

    @Schema(description = "用户ID", example = "10001")
    private String id;

    @Schema(description = "用户域", example = "default")
    private String userDomain;

    @Schema(description = "真实姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String realName;

    @Schema(description = "登录名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginName;

    @Schema(description = "手机号码", example = "13800138000")
    private String mobileNo;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "用户状态：0-禁用，1-启用", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "用户描述", example = "系统管理员")
    private String description;

    @Schema(description = "最后登录时间", example = "2024-01-17T10:30:00")
    private Date latestLoginTime;

    @Schema(description = "已分配的角色列表")
    private List<Map<String, String>> assignedRoles;

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置用户ID
     *
     * @param id 用户ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取用户域
     *
     * @return 用户域
     */
    public String getUserDomain() {
        return userDomain;
    }

    /**
     * 设置用户域
     *
     * @param userDomain 用户域
     */
    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    /**
     * 获取真实姓名
     *
     * @return 真实姓名
     */
    public String getRealName() {
        return realName;
    }

    /**
     * 设置真实姓名
     *
     * @param realName 真实姓名
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * 获取登录名
     *
     * @return 登录名
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * 设置登录名
     *
     * @param loginName 登录名
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * 获取手机号码
     *
     * @return 手机号码
     */
    public String getMobileNo() {
        return mobileNo;
    }

    /**
     * 设置手机号码
     *
     * @param mobileNo 手机号码
     */
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    /**
     * 获取头像URL
     *
     * @return 头像URL
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * 设置头像URL
     *
     * @param avatarUrl 头像URL
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * 获取用户状态
     *
     * @return 用户状态：0-禁用，1-启用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置用户状态
     *
     * @param status 用户状态：0-禁用，1-启用
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取用户描述
     *
     * @return 用户描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置用户描述
     *
     * @param description 用户描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取最后登录时间
     *
     * @return 最后登录时间
     */
    public Date getLatestLoginTime() {
        return latestLoginTime;
    }

    /**
     * 设置最后登录时间
     *
     * @param latestLoginTime 最后登录时间
     */
    public void setLatestLoginTime(Date latestLoginTime) {
        this.latestLoginTime = latestLoginTime;
    }

    /**
     * 获取已分配的角色列表
     *
     * @return 已分配的角色列表
     */
    public List<Map<String, String>> getAssignedRoles() {
        return assignedRoles;
    }

    /**
     * 设置已分配的角色列表
     *
     * @param assignedRoles 已分配的角色列表
     */
    public void setAssignedRoles(List<Map<String, String>> assignedRoles) {
        this.assignedRoles = assignedRoles;
    }
}
