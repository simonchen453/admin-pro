package com.adminpro.system.rbac.domains.vo.login;

import com.adminpro.framework.base.entity.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录请求VO
 *
 * <p>用于用户登录的请求参数，包含用户ID、密码、域和验证码</p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Data
@Schema(description = "用户登录请求VO")
public class LoginUserVo extends BaseVO {

    @Schema(description = "用户ID", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "用户域", example = "default")
    private String domain;

    @Schema(description = "验证码", example = "1234")
    private String captcha;
}
