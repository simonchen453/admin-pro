package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.util.DateUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.tools.domains.entity.auditlog.AuditLogDTO;
import com.adminpro.system.tools.domains.entity.auditlog.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 审计日志管理控制器
 * <p>
 * 提供系统审计日志的查询功能，包括：
 * <ul>
 * <li>审计日志列表查询（支持分页和多条件筛选）</li>
 * <li>支持按状态、事件、模块、类别、用户、时间范围等条件筛选</li>
 * </ul>
 * </p>
 *
 * @author dongqin
 * @date 2019-10-21
 */
@Tag(name = "审计日志管理", description = "系统审计日志管理接口，提供日志查询功能")
@RestController
@RequestMapping(AuditLogController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:audit')")
public class AuditLogController extends BaseController {

    protected static final String PREFIX_URL = "/api/v1/tools/audit-logs";
    protected static final String SEARCH_FORM_KEY = "auditLogSearchForm";

    @Autowired
    private AuditLogService auditLogService;

    /**
     * 查询审计日志列表
     * <p>
     * 支持按状态、事件、模块、类别、用户、时间范围等条件进行分页查询审计日志
     * </p>
     *
     * @param searchForm 搜索表单，包含分页信息和筛选条件
     * @return 包含审计日志列表和分页信息的查询结果集
     */
    @Operation(summary = "查询审计日志列表", description = "支持按状态、事件、模块、类别、用户、时间范围等条件进行分页查询审计日志")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "搜索条件", required = true, content = @Content(schema = @Schema(implementation = SearchForm.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = R.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @PostMapping("/search")
    public R<QueryResultSet<AuditLogDTO>> search(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        String status = searchForm.getStatus();
        String event = searchForm.getEvent();
        String module = searchForm.getModule();
        String category = searchForm.getCategory();
        String user = searchForm.getUser();
        String startDateStr = searchForm.getStartDate();
        String endDateStr = searchForm.getEndDate();
        Date startDate = DateUtil.parseDateTime(startDateStr);
        Date endDate = DateUtil.parseDateTime(endDateStr);

        if (StringUtils.isNotEmpty(status)) {
            param.addFilter("status", StringUtils.trimToNull(status));
        }
        if (StringUtils.isNotEmpty(event)) {
            param.addFilter("event", StringUtils.trimToNull(event));
        }
        if (StringUtils.isNotEmpty(module)) {
            param.addFilter("module", StringUtils.trimToNull(module));
        }
        if (StringUtils.isNotEmpty(category)) {
            param.addFilter("category", StringUtils.trimToNull(category));
        }
        if (StringUtils.isNotEmpty(user)) {
            param.addFilter("user", StringUtils.trimToNull(user));
        }
        if (startDate != null) {
            param.addFilter("startDate", startDate);
        }
        if (endDate != null) {
            param.addFilter("endDate", endDate);
        }

        QueryResultSet<AuditLogDTO> resultSet = auditLogService.search(param);
        return R.ok(resultSet);
    }

    @Data
    @lombok.EqualsAndHashCode(callSuper = false)
    public static class SearchForm extends BaseSearchForm {
        private String user;
        private String module;
        private String category;
        private String event;
        private String status;
        private String startDate;
        private String endDate;
    }

    public SearchForm getSearchForm() {
        SearchForm searchForm = (SearchForm) request.getSession().getAttribute(SEARCH_FORM_KEY);
        if (searchForm == null) {
            searchForm = new SearchForm();
        }
        setSearchForm(searchForm);
        return searchForm;
    }

    public void setSearchForm(SearchForm searchForm) {
        request.getSession().setAttribute(SEARCH_FORM_KEY, searchForm);
    }

    public void cleanSearchForm() {
        request.getSession().removeAttribute(SEARCH_FORM_KEY);
    }
}
