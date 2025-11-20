package com.adminpro.tools.domains.entity.exceptionlog;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 异常 信息操作处理
 *
 * @author simon
 * @date 2018-11-29
 */
@Controller
@RequestMapping("/admin/exceptionlog")
public class ExceptionLogController extends BaseRoutingController {
    protected static final String PREFIX = "admin/exceptionlog";
    protected static final String PREFIX_URL = "/admin/exceptionlog";
    protected static final String SEARCH_FORM_KEY = ExceptionLogController.class.getSimpleName();
    @Autowired
    private ExceptionLogService exceptionLogService;

    @GetMapping()
    public String prepareList() {
        cleanSearchForm();
        return "forward:" + PREFIX_URL + "/list";
    }

    @GetMapping("/list")
    public String exceptionLog() {
        prepareData();
        getSearchForm();
        return PREFIX + "/list";
    }

    /**
     * 查询异常列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public R<QueryResultSet<ExceptionLogEntity>> list(@RequestBody SearchForm searchForm) {
        String condition = searchForm.getCondition();
        String startTimeStr = searchForm.getStartTime();
        String endTimeStr = searchForm.getEndTime();

        Date startTime = DateUtil.parseDateTime(startTimeStr);
        Date endTime = DateUtil.parseDateTime(endTimeStr);

        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);

        if (StringUtils.isNotEmpty(condition)) {
            param.addFilter("condition", condition);
        }

        if (startTime != null) {
            param.addFilter("startTime", startTime);
        }

        if (endTime != null) {
            param.addFilter("endTime", endTime);
        }

        QueryResultSet<ExceptionLogEntity> resultSet = exceptionLogService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 查看异常
     */
    @GetMapping("/view")
    public String edit(@RequestParam("id") String id) {
        prepareData();
        ExceptionLogEntity exceptionLog = exceptionLogService.findById(id);
        request.setAttribute("exceptionLog", exceptionLog);
        return PREFIX + "/view";
    }

    public static class SearchForm extends BaseSearchForm {
        private String condition;
        private String startTime;
        private String endTime;

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }

    private SearchForm getSearchForm() {
        SearchForm searchForm = (SearchForm) request.getSession().getAttribute(SEARCH_FORM_KEY);
        if (searchForm == null) {
            searchForm = new SearchForm();
        }
        setSearchForm(searchForm);
        return searchForm;
    }

    private void setSearchForm(SearchForm searchForm) {
        request.getSession().setAttribute(SEARCH_FORM_KEY, searchForm);
    }

    private void cleanSearchForm() {
        request.getSession().removeAttribute(SEARCH_FORM_KEY);
    }

}
