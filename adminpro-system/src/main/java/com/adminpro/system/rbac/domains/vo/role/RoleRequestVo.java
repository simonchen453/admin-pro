package com.adminpro.system.rbac.domains.vo.role;

import com.adminpro.framework.base.entity.BaseAuditVO;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 角色创建和更新请求VO
 *
 * <p>用于创建和更新角色的请求参数，包含角色基本信息及权限配置</p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Schema(description = "角色创建和更新请求VO")
public class RoleRequestVo extends BaseAuditVO {

    @Schema(description = "角色ID", example = "ROLE001")
    private String id;

    @Schema(description = "角色名称", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "角色显示名称", example = "管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    private String display;

    @Schema(description = "权限名称列表", example = "[\"user:read\", \"user:write\"]")
    private String[] privilegeNames;

    /**
     * 获取角色ID
     *
     * @return 角色ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置角色ID
     *
     * @param id 角色ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取角色名称
     *
     * @return 角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置角色名称
     *
     * @param name 角色名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取角色显示名称
     *
     * @return 角色显示名称
     */
    public String getDisplay() {
        return display;
    }

    /**
     * 设置角色显示名称
     *
     * @param display 角色显示名称
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * 获取权限名称列表
     *
     * @return 权限名称列表
     */
    public String[] getPrivilegeNames() {
        return privilegeNames;
    }

    /**
     * 设置权限名称列表
     *
     * @param privilegeNames 权限名称列表
     */
    public void setPrivilegeNames(String[] privilegeNames) {
        this.privilegeNames = privilegeNames;
    }
}
