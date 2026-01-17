package com.adminpro.system.rbac.domains.vo.user;

import com.adminpro.framework.base.entity.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户个人信息更新VO
 *
 * <p>用于用户更新个人基本信息的请求参数，包含姓名、联系方式、头像等</p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Data
@Schema(description = "用户个人信息更新VO")
public class UpdateProfileVo extends BaseVO {

    @Schema(description = "真实姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 255)
    private String realName;

    @Schema(description = "手机号码", example = "13800138000")
    @Size(max = 255)
    private String mobileNo;

    @Schema(description = "电子邮箱", example = "zhangsan@example.com")
    @Email
    @Size(max = 255)
    private String email;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    @Size(max = 255)
    private String avatarUrl;

    @Schema(description = "性别：0-女，1-男，2-未知", example = "1")
    @Size(max = 12)
    private String sex;

    /**
     * 个人简介
     */
    @Schema(description = "个人简介", example = "软件工程师，擅长Java开发")
    @Size(max = 255)
    private String description;
}

