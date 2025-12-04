package com.adminpro.rbac.domains.vo.city;

import com.adminpro.core.base.entity.BaseVO;

/**
 * 行政区划Vo
 */
public class CityVo extends BaseVO {
    private String id;
    private String title;
    private String parent;
    private Integer level;
    private String keyword;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
