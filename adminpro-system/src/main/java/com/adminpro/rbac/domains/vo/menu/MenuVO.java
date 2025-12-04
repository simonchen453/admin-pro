package com.adminpro.rbac.domains.vo.menu;

import com.adminpro.core.base.entity.BaseVO;

public class MenuVO extends BaseVO {
    /**
     * ID
     */
    private String id;
    /**
     * 父级菜单ID
     */
    private String parentId;
    /**
     * 显示名称
     */
    private String display;
    /**
     * Left值
     */
    private Integer left;
    /**
     * 级别
     */
    private Integer level;
    /**
     * 菜单名称
     */
    private String name;
    /**
     * Right值
     */
    private Integer right;
    /**
     * 链接url
     */
    private String url;
    /**
     * 图标
     */
    private String icon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRight() {
        return right;
    }

    public void setRight(Integer right) {
        this.right = right;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
