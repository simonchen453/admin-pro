package com.adminpro.core.base.web;

import com.adminpro.core.base.IConstants;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.ResultOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 基础Controller控制器父类
 *
 * @author simon
 */
@Slf4j
public abstract class AbsController implements IController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    /**
     * 获取默认的message bundle，并绑定到request
     *
     * @return
     */
    protected MessageBundle getMessageBundle() {
        return new MessageBundle();
    }

    public SearchParam startPaging() {
        SearchParam searchParam = new SearchParam();
        PagingMeta pagingMeta = createPagingMeta(request);
        searchParam.setPageSize(pagingMeta.getPageSize());
        searchParam.setPageNo(pagingMeta.getPageNo());

        return searchParam;
    }

    public SearchParam startPaging(BaseSearchForm searchForm) {
        SearchParam searchParam = new SearchParam();
        PagingMeta pagingMeta = createPagingMeta(searchForm);
        searchParam.setPageSize(pagingMeta.getPageSize());
        searchParam.setPageNo(pagingMeta.getPageNo());

        Map<String, String> filedColumnMap = searchForm.getFiledColumnMap();
        String colName = filedColumnMap.get(pagingMeta.getSortBy());
        if(StringUtils.isNotEmpty(colName)){
            searchParam.setSort(colName, transformSortDir(pagingMeta.getSortDir()));
        }

        searchParam.setPageNo(pagingMeta.getPageNo());
        return searchParam;
    }

    protected String transformSortDir(String sortDir){
        if(StringUtils.equals(sortDir, "ascending")){
            return ResultOrder.SORT_ASC;
        }else if(StringUtils.equals(sortDir, "descending")){
            return ResultOrder.SORT_DESC;
        }else{
            return ResultOrder.SORT_DESC;
        }
    }

    protected static PagingMeta createPagingMeta(HttpServletRequest request) {
        PagingMeta meta = new PagingMeta();
        meta.setPageSize(NumberUtils.toInt(request.getParameter(IConstants.PARAM_PAGE_SIZE), IConstants.DEFAULT_PAGE_SIZE));
        meta.setPageNo(NumberUtils.toInt(request.getParameter(IConstants.PARAM_PAGE_NO), 1));

        meta.setSortBy(StringUtils.trimToNull(request.getParameter(IConstants.PARAM_SORT_BY)));
        meta.setSortDir(StringUtils.trimToNull(request.getParameter(IConstants.PARAM_SORT_DIR)));
        return meta;
    }

    protected static PagingMeta createPagingMeta(BaseSearchForm searchForm) {
        PagingMeta meta = new PagingMeta();
        meta.setPageSize(searchForm.getPageSize());
        meta.setPageNo(searchForm.getPageNo());

        meta.setSortBy(StringUtils.trimToNull(searchForm.getSortBy()));
        meta.setSortDir(StringUtils.trimToNull(searchForm.getSortDir()));

        return meta;
    }

    protected Integer getPageSize() {
        Integer pageSize = 10;
        String pageSizeStr = request.getParameter(IConstants.PARAM_PAGE_SIZE);
        if (!StringUtils.isEmpty(pageSizeStr)) {
            pageSize = Integer.valueOf(pageSizeStr);
        }
        return pageSize;
    }

    protected Integer getPageNo() {
        Integer pageNo = 0;
        String pageNoStr = request.getParameter(IConstants.PARAM_PAGE_NO);
        if (!StringUtils.isEmpty(pageNoStr)) {
            pageNo = Integer.valueOf(pageNoStr);
        }
        return pageNo;
    }

    protected Integer getOffSet() {
        return getPageNo() * getPageSize();
    }
}
