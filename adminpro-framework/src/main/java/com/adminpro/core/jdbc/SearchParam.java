package com.adminpro.core.jdbc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SearchParam implements Serializable {
    private static final long serialVersionUID = -8217912278426588308L;

    public static final String ASCENDING = "ASC";
    public static final String DESCENDING = "DESC";

    private Map<String, Object> params;     // for template SQL generation
    private Map<String, Object> filters;    // for SQL query
    private Map<String, String> orderBy;
    private int pageSize;
    private int pageNo;
    private boolean hidden = true;
    private String groupFilter;

    private String sortField;
    private String sortType;

    private boolean sortInMemory;

    public SearchParam() {
        clear();
        filters = new LinkedHashMap<String, Object>();
        params = new HashMap<String, Object>();
        orderBy = new LinkedHashMap<String, String>();
    }

    public SearchParam(String groupFilter, String groupColumn, Object[] groupValues) {
        this();
        this.groupFilter = groupFilter;
    }

    private SearchParam(int pageNo, int pageSize) {
        this();
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }

    public static SearchParam init(int pageNo, int pageSize) {
        return new SearchParam(pageNo, pageSize);
    }

    public void addParam(String name, Object value) {
        params.put(name, value);
    }

    public void addFilter(String name, Object value) {
        filters.put(name, value);
        params.put(name, value);
    }

    public void clear() {
        filters = null;
        params = null;
        orderBy = null;
        pageSize = 0;
        pageNo = 1;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setSort(String sortField, String sortType) {
        this.sortField = sortField;
        this.sortType = sortType;
        orderBy = new LinkedHashMap<String, String>();
        addSort(sortField, sortType);
    }

    public void addSort(String sortField, String sortType) {
        if (orderBy == null) {
            orderBy = new LinkedHashMap<String, String>();
        }
        orderBy.put(sortField, sortType);
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public Map<String, String> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Map<String, String> orderBy) {
        this.orderBy = orderBy;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getGroupFilter() {
        return groupFilter;
    }

    public void setGroupFilter(String groupFilter) {
        this.groupFilter = groupFilter;
    }

    public String getSortField() {
        return sortField;
    }

    public String getSortType() {
        return sortType;
    }

    public boolean isSortInMemory() {
        return sortInMemory;
    }

    public void setSortInMemory(boolean sortInMemory) {
        this.sortInMemory = sortInMemory;
    }
}
