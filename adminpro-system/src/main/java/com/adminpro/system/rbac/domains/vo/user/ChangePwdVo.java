package com.adminpro.system.rbac.domains.vo.user;

import com.adminpro.framework.base.entity.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户修改密码请求VO
 *
 * <p>用于用户修改自己密码的请求参数，需要提供旧密码和新密码</p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Schema(description = "用户修改密码请求VO")
public class ChangePwdVo extends BaseVO {

    @Schema(description = "旧密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPwd;

    @Schema(description = "新密码", example = "654321", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPwd;

    @Schema(description = "确认新密码", example = "654321", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmNewPwd;

    /**
     * 获取旧密码
     *
     * @return 旧密码
     */
    public String getOldPwd() {
        return oldPwd;
    }

    /**
     * 设置旧密码
     *
     * @param oldPwd 旧密码
     */
    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    /**
     * 获取新密码
     *
     * @return 新密码
     */
    public String getNewPwd() {
        return newPwd;
    }

    /**
     * 设置新密码
     *
     * @param newPwd 新密码
     */
    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    /**
     * 获取确认新密码
     *
     * @return 确认新密码
     */
    public String getConfirmNewPwd() {
        return confirmNewPwd;
    }

    /**
     * 设置确认新密码
     *
     * @param confirmNewPwd 确认新密码
     */
    public void setConfirmNewPwd(String confirmNewPwd) {
        this.confirmNewPwd = confirmNewPwd;
    }
}
