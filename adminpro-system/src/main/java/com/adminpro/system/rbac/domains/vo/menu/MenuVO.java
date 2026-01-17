package com.adminpro.system.rbac.domains.vo.menu;

import com.adminpro.framework.base.entity.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 菜单VO
 *
 * <p>用于菜单的基本信息展示，支持嵌套集合模型（Nested Set Model）</p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Schema(description = "菜单VO")
public class MenuVO extends BaseVO {
    /**
     * ID
     */
    @Schema(description = "菜单ID", example = "MENU001")
    private String id;

    /**
     * 父级菜单ID
     */
    @Schema(description = "父级菜单ID", example = "MENU000")
    private String parentId;

    /**
     * 显示名称
     */
    @Schema(description = "显示名称", example = "用户管理")
    private String display;

    /**
     * Left值
     */
    @Schema(description = "左值（用于嵌套集合模型）", example = "1")
    private Integer left;

    /**
     * 级别
     */
    @Schema(description = "菜单级别", example = "1")
    private Integer level;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", example = "user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * Right值
     */
    @Schema(description = "右值（用于嵌套集合模型）", example = "10")
    private Integer right;

    /**
     * 链接url
     */
    @Schema(description = "菜单链接URL", example = "/system/user")
    private String url;

    /**
     * 图标
     */
    @Schema(description = "菜单图标", example = "el-icon-setting")
    private String icon;

    /**
     * 获取菜单ID
     *
     * @return 菜单ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置菜单ID
     *
     * @param id 菜单ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取父级菜单ID
     *
     * @return 父级菜单ID
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * 设置父级菜单ID
     *
     * @param parentId 父级菜单ID
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取显示名称
     *
     * @return 显示名称
     */
    public String getDisplay() {
        return display;
    }

    /**
     * 设置显示名称
     *
     * @param display 显示名称
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * 获取左值
     *
     * @return 左值（用于嵌套集合模型）
     */
    public Integer getLeft() {
        return left;
    }

    /**
     * 设置左值
     *
     * @param left 左值（用于嵌套集合模型）
     */
    public void setLeft(Integer left) {
        this.left = left;
    }

    /**
     * 获取菜单级别
     *
     * @return 菜单级别
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * 设置菜单级别
     *
     * @param level 菜单级别
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * 获取菜单名称
     *
     * @return 菜单名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置菜单名称
     *
     * @param name 菜单名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取右值
     *
     * @return 右值（用于嵌套集合模型）
     */
    public Integer getRight() {
        return right;
    }

    /**
     * 设置右值
     *
     * @param right 右值（用于嵌套集合模型）
     */
    public void setRight(Integer right) {
        this.right = right;
    }

    /**
     * 获取菜单链接URL
     *
     * @return 菜单链接URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置菜单链接URL
     *
     * @param url 菜单链接URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取菜单图标
     *
     * @return 菜单图标
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置菜单图标
     *
     * @param icon 菜单图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }
}
