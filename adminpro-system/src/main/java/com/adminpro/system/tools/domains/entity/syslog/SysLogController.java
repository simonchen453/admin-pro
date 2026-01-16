package com.adminpro.system.tools.domains.entity.syslog;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.DateUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.web.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 系统日志 信息操作处理
 *
 * @author simon
 * @date 2018-11-29
 */
@RestController
@RequestMapping("/admin/syslog")
public class SysLogController extends BaseController {
    protected static final String PREFIX = "admin/syslog";
    protected static final String PREFIX_URL = "/admin/syslog";
    protected static final String SEARCH_FORM_KEY = SysLogController.class.getSimpleName();
    @Autowired
    private SysLogService sysLogService;

    /**
     * 查询系统日志列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<QueryResultSet<SysLogEntity>> list(@RequestBody SearchForm searchForm) {
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

        if (StringUtils.isNotEmpty(condition)) {
            param.addFilter("condition", condition);
        }

        if (startTime != null) {
            param.addFilter("startTime", startTime);
        }

        if (endTime != null) {
            param.addFilter("endTime", endTime);
        }

        QueryResultSet<SysLogDTO> resultSet = sysLogService.search(param);
        return R.ok(resultSet);
    }

    @RequestMapping(value = "/deletemany", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteManyLogs(@RequestParam String ids) {
        try {
            List<String> idList = BatchOperationValidator.validateAndParseIds(ids);
            sysLogService.deleteByIds(String.join(",", idList));
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
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

        @Override
        public String toString() {
            return "SearchForm{" +
                    "condition='" + condition + '\'' +
                    ", startTime='" + startTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    '}';
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
