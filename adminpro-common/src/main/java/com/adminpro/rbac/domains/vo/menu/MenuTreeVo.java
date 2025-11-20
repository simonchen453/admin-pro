package com.adminpro.rbac.domains.vo.menu;

import com.adminpro.core.base.entity.BaseVO;

import java.util.List;

public class MenuTreeVo extends BaseVO {

    private String id;

    private String icon;

    private String url;

    private String index;

    private String title;

    private String type;

    private List<MenuTreeVo> subs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MenuTreeVo> getSubs() {
        return subs;
    }

    public void setSubs(List<MenuTreeVo> subs) {
        this.subs = subs;
    }
}
