package com.adminpro.system.rbac.domains.vo.login;

import com.adminpro.framework.base.entity.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户注册请求VO
 *
 * <p>用于用户注册的请求参数，包含用户ID、密码、平台及注册码等信息</p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Schema(description = "用户注册请求VO")
public class RegisterUserVo extends BaseVO {

    @Schema(description = "用户ID", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userid;

    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "平台", example = "web")
    private String platform;

    @Schema(description = "邀请码", example = "INV123")
    private String invitationCode;

    @Schema(description = "注册码", example = "REG456")
    private String registCode;

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public String getUserid() {
        return userid;
    }

    /**
     * 设置用户ID
     *
     * @param userid 用户ID
     */
    public void setUserid(String userid) {
        this.userid = userid;
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
     * 获取平台
     *
     * @return 平台
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 设置平台
     *
     * @param platform 平台
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * 获取邀请码
     *
     * @return 邀请码
     */
    public String getInvitationCode() {
        return invitationCode;
    }

    /**
     * 设置邀请码
     *
     * @param invitationCode 邀请码
     */
    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    /**
     * 获取注册码
     *
     * @return 注册码
     */
    public String getRegistCode() {
        return registCode;
    }

    /**
     * 设置注册码
     *
     * @param registCode 注册码
     */
    public void setRegistCode(String registCode) {
        this.registCode = registCode;
    }

}
