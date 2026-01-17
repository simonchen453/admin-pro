package com.adminpro.system.rbac.domains.vo.role;

import com.adminpro.framework.base.entity.BaseAuditVO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * 角色响应VO
 *
 * <p>用于返回角色详细信息，包含角色基本信息及已分配的权限列表</p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Schema(description = "角色响应VO")
public class RoleResponseVo extends BaseAuditVO {

    @Schema(description = "角色ID", example = "ROLE001")
    private String id;

    @Schema(description = "角色名称", example = "admin")
    private String name;

    @Schema(description = "角色显示名称", example = "管理员")
    private String display;

    /**
     * 已经关联的权限
     */
    @Schema(description = "已关联的权限列表")
    private List<Map<String, String>> assignedPrivileges;

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
     * 获取已关联的权限列表
     *
     * @return 已关联的权限列表
     */
    public List<Map<String, String>> getAssignedPrivileges() {
        return assignedPrivileges;
    }

    /**
     * 设置已关联的权限列表
     *
     * @param assignedPrivileges 已关联的权限列表
     */
    public void setAssignedPrivileges(List<Map<String, String>> assignedPrivileges) {
        this.assignedPrivileges = assignedPrivileges;
    }
}
