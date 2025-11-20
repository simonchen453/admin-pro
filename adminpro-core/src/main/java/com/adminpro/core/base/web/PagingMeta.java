package com.adminpro.core.base.web;

import com.adminpro.core.base.IConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 存储基本的分页查询信息：当前页，排序，页面显示记录
 */
public class PagingMeta {
    /**
     * if 0, means all. can not less than 0
     */
    private int pageSize;
    /**
     * spring data jpa start from 0. equal 0 when < 0
     */
    private int pageNo;
    private String sortBy;
    private String sortDir;

    private static PagingMeta createPagingMeta(HttpServletRequest request) {
        PagingMeta meta = new PagingMeta();
        meta.setPageSize(NumberUtils.toInt(request.getParameter(IConstants.PARAM_PAGE_SIZE), IConstants.DEFAULT_PAGE_SIZE));
        meta.setPageNo(NumberUtils.toInt(request.getParameter(IConstants.PARAM_PAGE_NO), 0));
        meta.setSortBy(StringUtils.trimToNull(request.getParameter(IConstants.PARAM_SORT_BY)));
        meta.setSortDir(StringUtils.trimToNull(request.getParameter(IConstants.PARAM_SORT_DIR)));

        return meta;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public int getPageSize() {
        if (pageSize <= 0) {
            return IConstants.DEFAULT_PAGE_SIZE;
        }

        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        if (pageNo < 0) {
            return 0;
        }

        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

}
