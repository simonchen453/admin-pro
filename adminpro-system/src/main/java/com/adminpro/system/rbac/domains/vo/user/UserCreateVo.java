package com.adminpro.system.rbac.domains.vo.user;

import com.adminpro.framework.base.entity.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 用户创建请求VO
 *
 * <p>
 * 用于创建新用户的请求参数，包含用户的基本信息、登录信息、角色分配等
 * </p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Schema(description = "用户创建请求VO")
public class UserCreateVo extends BaseVO {

    @Schema(description = "用户ID", example = "10001")
    private String id;

    @Schema(description = "用户域", example = "default", requiredMode = Schema.RequiredMode.REQUIRED)
    @jakarta.validation.constraints.NotBlank(message = "用户域不能为空")
    private String userDomain;

    @Schema(description = "真实姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String realName;

    @Schema(description = "登录名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginName;

    @Schema(description = "用户描述", example = "系统管理员")
    private String description;

    @Schema(description = "手机号码", example = "13800138000")
    private String mobileNo;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "确认密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;

    @Schema(description = "部门ID", example = "DEPT001")
    private String deptId;

    @Schema(description = "用户状态：0-禁用，1-启用", example = "1")
    private String status;

    @Schema(description = "性别：0-女，1-男，2-未知", example = "1")
    private String sex;

    @Schema(description = "电子邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "备注", example = "新建用户")
    private String remark;

    @Schema(description = "角色ID列表", example = "[\"ROLE001\", \"ROLE002\"]")
    private List<String> roleIds;

    @Schema(description = "岗位ID列表", example = "[\"POST001\", \"POST002\"]")
    private List<String> postIds;

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
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取确认密码
     *
     * @return 确认密码
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * 设置确认密码
     *
     * @param confirmPassword 确认密码
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * 获取部门ID
     *
     * @return 部门ID
     */
    public String getDeptId() {
        return deptId;
    }

    /**
     * 设置部门ID
     *
     * @param deptId 部门ID
     */
    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    /**
     * 获取角色ID列表
     *
     * @return 角色ID列表
     */
    public List<String> getRoleIds() {
        return roleIds;
    }

    /**
     * 设置角色ID列表
     *
     * @param roleIds 角色ID列表
     */
    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    /**
     * 获取岗位ID列表
     *
     * @return 岗位ID列表
     */
    public List<String> getPostIds() {
        return postIds;
    }

    /**
     * 设置岗位ID列表
     *
     * @param postIds 岗位ID列表
     */
    public void setPostIds(List<String> postIds) {
        this.postIds = postIds;
    }

    /**
     * 获取用户状态
     *
     * @return 用户状态：0-禁用，1-启用
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置用户状态
     *
     * @param status 用户状态：0-禁用，1-启用
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取电子邮箱
     *
     * @return 电子邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置电子邮箱
     *
     * @param email 电子邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取性别
     *
     * @return 性别：0-女，1-男，2-未知
     */
    public String getSex() {
        return sex;
    }

    /**
     * 设置性别
     *
     * @param sex 性别：0-女，1-男，2-未知
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 获取备注
     *
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
