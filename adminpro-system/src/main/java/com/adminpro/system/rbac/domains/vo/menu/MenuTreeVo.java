package com.adminpro.system.rbac.domains.vo.menu;

import com.adminpro.framework.base.entity.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 菜单树形结构VO
 *
 * <p>用于返回菜单的树形结构，支持多层级菜单展示</p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Schema(description = "菜单树形结构VO")
public class MenuTreeVo extends BaseVO {

    @Schema(description = "菜单ID", example = "MENU001")
    private String id;

    @Schema(description = "菜单图标", example = "el-icon-setting")
    private String icon;

    @Schema(description = "菜单链接URL", example = "/system/user")
    private String url;

    @Schema(description = "菜单索引/排序", example = "1")
    private String index;

    @Schema(description = "菜单标题", example = "用户管理", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "菜单类型", example = "menu")
    private String type;

    @Schema(description = "子菜单列表")
    private List<MenuTreeVo> subs;

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
     * 获取菜单类型
     *
     * @return 菜单类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置菜单类型
     *
     * @param type 菜单类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取菜单索引/排序
     *
     * @return 菜单索引/排序
     */
    public String getIndex() {
        return index;
    }

    /**
     * 设置菜单索引/排序
     *
     * @param index 菜单索引/排序
     */
    public void setIndex(String index) {
        this.index = index;
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
     * 获取菜单标题
     *
     * @return 菜单标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置菜单标题
     *
     * @param title 菜单标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取子菜单列表
     *
     * @return 子菜单列表
     */
    public List<MenuTreeVo> getSubs() {
        return subs;
    }

    /**
     * 设置子菜单列表
     *
     * @param subs 子菜单列表
     */
    public void setSubs(List<MenuTreeVo> subs) {
        this.subs = subs;
    }
}
