package com.adminpro.system.rbac.domains.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户重置密码请求VO
 *
 * <p>用于管理员重置用户密码的请求参数</p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Schema(description = "用户重置密码请求VO")
public class UserResetPwdRequestVo {

    @Schema(description = "用户ID", example = "10001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(description = "用户域", example = "default")
    private String userDomain;

    @Schema(description = "新密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;

    @Schema(description = "确认密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
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
     * 获取新密码
     *
     * @return 新密码
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * 设置新密码
     *
     * @param newPassword 新密码
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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
}
