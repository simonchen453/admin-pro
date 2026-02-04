package com.adminpro.system.tools.domains.entity.syslog;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.DateUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.web.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/tools/sys-logs")
@Tag(name = "系统日志管理", description = "系统日志的查询和删除接口")
public class SysLogController extends BaseController {
    protected static final String PREFIX = "admin/syslog";
    protected static final String PREFIX_URL = "/api/v1/tools/sys-logs";
    protected static final String SEARCH_FORM_KEY = SysLogController.class.getSimpleName();
    @Autowired
    private SysLogService sysLogService;

    /**
     * 查询系统日志列表
     */
    @PostMapping("/search")
    @Operation(summary = "查询系统日志列表", description = "根据条件查询系统日志，支持分页、时间范围和条件过滤")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 QueryResultSet<SysLogDTO> 列表
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=500: 服务器错误
                """,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = R.class))
    )
    public R<QueryResultSet<SysLogDTO>> list(@RequestBody SearchForm searchForm) {
        String condition = searchForm.getCondition();
        String startTimeStr = searchForm.getStartTime();
        String endTimeStr = searchForm.getEndTime();

        Date startTime = DateUtil.parseDateTime(startTimeStr);
        Date endTime = DateUtil.parseDateTime(endTimeStr);

        SearchParam param = startPaging(searchForm);

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

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "批量删除系统日志", description = "根据ID列表批量删除系统日志记录")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 批量删除成功
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=500: 服务器错误
                """,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = R.class))
    )
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

}
