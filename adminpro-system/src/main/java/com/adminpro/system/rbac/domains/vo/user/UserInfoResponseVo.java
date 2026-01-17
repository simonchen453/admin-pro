package com.adminpro.system.rbac.domains.vo.user;

import com.adminpro.framework.base.entity.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 用户信息响应VO
 *
 * <p>
 * 用于返回用户详细信息，包含用户的基本信息、联系方式、部门信息及登录记录等
 * </p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Data
@Schema(description = "用户信息响应VO")
public class UserInfoResponseVo extends BaseVO {

    @Schema(description = "用户ID", example = "10001")
    private String id;

    @Schema(description = "用户域", example = "default")
    private String userDomain;

    @Schema(description = "登录名", example = "zhangsan")
    private String loginName;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "手机号码", example = "13800138000")
    private String mobileNo;

    @Schema(description = "电子邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "性别：0-女，1-男，2-未知", example = "1")
    private String sex;

    @Schema(description = "用户状态：0-禁用，1-启用", example = "1")
    private String status;

    @Schema(description = "部门编号", example = "DEPT001")
    private String deptNo;

    @Schema(description = "部门名称", example = "技术部")
    private String deptName;

    @Schema(description = "用户描述", example = "系统管理员")
    private String description;

    @Schema(description = "最后登录时间", example = "2024-01-17T10:30:00")
    private Date latestLoginTime;
}
