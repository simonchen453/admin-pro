package com.adminpro.rbac.domains.vo.tree;

import com.adminpro.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.rbac.domains.entity.menu.MenuEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 *
 * @author simon
 */
public class TreeSelect implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    private String id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect() {
    }

    public TreeSelect(DeptEntity dept) {
        this.id = dept.getId();
        this.label = dept.getName();
        List<DeptEntity> children = dept.getChildren();
        if (children != null) {
            this.children = children.stream().map(TreeSelect::new).collect(Collectors.toList());
        } else {
            this.children = new ArrayList<>();
        }
    }

    public TreeSelect(MenuEntity menu) {
        this.id = menu.getName();
        this.label = menu.getDisplay();
        List<MenuEntity> c = menu.getChildren();
        if (c != null) {
            this.children = c.stream().map(TreeSelect::new).collect(Collectors.toList());
        } else {
            this.children = new ArrayList<>();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<TreeSelect> getChildren() {
        return children;
    }

    public void setChildren(List<TreeSelect> children) {
        this.children = children;
    }
}
