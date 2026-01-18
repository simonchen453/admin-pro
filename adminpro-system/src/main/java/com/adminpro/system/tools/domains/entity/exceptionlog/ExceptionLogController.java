package com.adminpro.system.tools.domains.entity.exceptionlog;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.util.DateUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.web.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 异常 信息操作处理
 *
 * @author simon
 * @date 2018-11-29
 */
@RestController
@RequestMapping("/api/v1/exception-logs")
@Tag(name = "异常日志管理", description = "异常日志查询接口")
public class ExceptionLogController extends BaseController {
    protected static final String PREFIX = "admin/exceptionlog";
    protected static final String PREFIX_URL = "/api/v1/exception-logs";
    protected static final String SEARCH_FORM_KEY = ExceptionLogController.class.getSimpleName();
    @Autowired
    private ExceptionLogService exceptionLogService;

    /**
     * 查询异常列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    @Operation(summary = "查询异常日志列表", description = "根据条件查询异常日志，支持分页、时间范围和条件过滤")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 QueryResultSet<ExceptionLogEntity> 列表
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=500: 服务器错误
                """,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = R.class))
    )
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

    @Data
    @lombok.EqualsAndHashCode(callSuper = false)
    public static class SearchForm extends BaseSearchForm {
        private String condition;
        private String startTime;
        private String endTime;

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
