package com.adminpro.system.rbac.domains.vo.role;

import com.adminpro.framework.base.entity.BaseAuditVO;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 角色视图和编辑VO
 *
 * <p>用于角色的查看和编辑操作，包含角色的基本信息</p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Schema(description = "角色视图和编辑VO")
public class RoleVo extends BaseAuditVO {

    @Schema(description = "角色ID", example = "ROLE001")
    private String id;

    @Schema(description = "角色名称", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "角色显示名称", example = "管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    private String display;

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
}
