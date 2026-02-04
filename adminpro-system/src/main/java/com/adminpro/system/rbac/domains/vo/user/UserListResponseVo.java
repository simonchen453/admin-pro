package com.adminpro.system.rbac.domains.vo.user;

import com.adminpro.framework.base.entity.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
@Data
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

    @Schema(description = "用户状态：active-正常，inactive-停用，locked-锁定", example = "active", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "用户描述", example = "系统管理员")
    private String description;

    @Schema(description = "最后登录时间", example = "2024-01-17T10:30:00")
    private Date latestLoginTime;

    @Schema(description = "已分配的角色列表")
    private List<Map<String, String>> assignedRoles;

    @Schema(description = "部门名称", example = "研发部")
    private String deptName;

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;
}
