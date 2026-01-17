package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.batchjob.ScheduleStatus;
import com.adminpro.system.core.batchjob.utils.CronUtils;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.rbac.domains.vo.job.*;

import com.adminpro.system.tools.domains.entity.job.ScheduleJobEntity;
import com.adminpro.system.tools.domains.entity.job.ScheduleJobLogService;
import com.adminpro.system.tools.domains.entity.job.ScheduleJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 定时任务管理控制器
 * <p>
 * 提供定时任务的增删改查和执行控制功能，包括：
 * <ul>
 * <li>定时任务列表查询（支持分页和条件筛选）</li>
 * <li>定时任务的新增、修改、删除</li>
 * <li>定时任务的执行控制（立即执行、暂停、恢复）</li>
 * <li>定时任务日志查询和管理</li>
 * <li>Cron表达式下次执行时间计算</li>
 * </ul>
 * </p>
 *
 * @author simon
 * @date 2020-06-18
 */
@Tag(name = "定时任务管理", description = "定时任务管理接口，提供任务的增删改查、执行控制和日志管理功能")
@RestController
@RequestMapping(JobController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:job')")
public class JobController extends BaseController {

    protected static final String PREFIX_URL = "/api/v1/jobs";
    protected static final String SEARCH_FORM_KEY = "jobSearchForm";

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleJobLogService scheduleJobLogService;

    @Autowired
    private JobVoConverter jobVoConverter;

    @Autowired
    private JobCreateValidator jobCreateValidator;

    @Autowired
    private JobUpdateValidator jobUpdateValidator;

    /**
     * 分页查询定时任务列表
     * <p>
     * 支持按条件查询定时任务列表，返回分页结果
     * </p>
     *
     * @param searchForm 搜索表单，包含分页信息和筛选条件
     * @return 包含定时任务列表和分页信息的查询结果集
     */
    @Operation(summary = "分页查询定时任务列表", description = "支持按条件查询定时任务列表，返回分页结果")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "搜索条件", required = true, content = @Content(schema = @Schema(implementation = SearchForm.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = R.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public R<QueryResultSet<JobVo>> paging(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String condition = searchForm.getCondition();
        SearchParam param = startPaging(searchForm);
        setSearchForm(request, searchForm);
        if (StringUtils.isNotEmpty(condition)) {
            param.addFilter("condition", condition);
        }
        QueryResultSet<JobVo> resultSet = scheduleJobService.search(param).map(JobVoConverter.class);
        return R.ok(resultSet);
    }

    /**
     * 创建定时任务
     * <p>
     * 创建新的定时任务，包含Bean名称、方法名、Cron表达式等配置
     * </p>
     *
     * @param jobVo 定时任务视图对象
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "创建定时任务", description = "创建新的定时任务，包含Bean名称、方法名、Cron表达式等配置")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "定时任务视图对象", required = true, content = @Content(schema = @Schema(implementation = JobVo.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public R create(@RequestBody JobVo jobVo) {
        BeanUtil.beanAttributeValueTrim(jobVo);
        MessageBundle bundle = getMessageBundle();
        jobCreateValidator.validate(jobVo, bundle);
        if (bundle.hasErrorMessage()) {
            return R.error(bundle);
        }

        ScheduleJobEntity entity = jobVoConverter.inverse(jobVo);
        entity.setStatus(ScheduleStatus.NORMAL.getValue());
        entity.setCreatedTime(new Date());
        scheduleJobService.create(entity);

        return R.ok();
    }

    /**
     * 更新定时任务
     * <p>
     * 根据任务ID更新定时任务的配置信息
     * </p>
     *
     * @param jobVo 定时任务视图对象，必须包含ID字段
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "更新定时任务", description = "根据任务ID更新定时任务的配置信息")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "定时任务视图对象，必须包含ID字段", required = true, content = @Content(schema = @Schema(implementation = JobVo.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "任务不存在")
    })
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody JobVo jobVo) {
        BeanUtil.beanAttributeValueTrim(jobVo);
        MessageBundle bundle = getMessageBundle();
        jobUpdateValidator.validate(jobVo, bundle);
        if (bundle.hasErrorMessage()) {
            return R.error(bundle);
        }

        ScheduleJobEntity entity = scheduleJobService.findById(jobVo.getId());
        entity.setBeanName(jobVo.getBeanName());
        entity.setMethodName(jobVo.getMethodName());
        entity.setCronExpression(jobVo.getCronExpression());
        entity.setParams(jobVo.getParams());
        entity.setRemark(jobVo.getRemark());
        scheduleJobService.update(entity);
        return R.ok();
    }

    /**
     * 删除定时任务
     * <p>
     * 支持批量删除定时任务，多个ID用逗号分隔
     * </p>
     *
     * @param ids 任务ID列表，多个ID用逗号分隔
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "删除定时任务", description = "支持批量删除定时任务，多个ID用逗号分隔")
    @Parameter(name = "ids", description = "任务ID列表，多个ID用逗号分隔", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteMany(@RequestParam String ids) {
        try {
            // 使用验证工具类解析和验证参数
            List<String> jobIdList = BatchOperationValidator.validateAndParseIds(ids);
            scheduleJobService.deleteMany(StringUtils.join(jobIdList, ","));
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
    }

    /**
     * 获取定时任务详情
     * <p>
     * 根据任务ID查询定时任务的详细信息
     * </p>
     *
     * @param id 任务ID
     * @return 定时任务视图对象
     */
    @Operation(summary = "获取定时任务详情", description = "根据任务ID查询定时任务的详细信息")
    @Parameter(name = "id", description = "任务ID", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = JobVo.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "任务不存在")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public R detail(@PathVariable String id) {
        ScheduleJobEntity entity = scheduleJobService.findById(id);
        JobVo convert = jobVoConverter.convert(entity);
        return R.ok(convert);
    }

    /**
     * 立即执行定时任务
     * <p>
     * 立即执行指定的定时任务，支持批量执行
     * </p>
     *
     * @param ids 任务ID列表，多个ID用逗号分隔
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "立即执行定时任务", description = "立即执行指定的定时任务，支持批量执行")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "任务ID列表，多个ID用逗号分隔", required = true, content = @Content(schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "执行成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @RequestMapping(value = "/run", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public R run(@RequestBody String ids) {
        try {
            String[] split = ids.split(",");
            if (ArrayUtils.isNotEmpty(split)) {
                scheduleJobService.run(split);
            }
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
    }

    /**
     * 暂停定时任务
     * <p>
     * 暂停指定的定时任务，支持批量暂停
     * </p>
     *
     * @param ids 任务ID列表，多个ID用逗号分隔
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "暂停定时任务", description = "暂停指定的定时任务，支持批量暂停")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "任务ID列表，多个ID用逗号分隔", required = true, content = @Content(schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "暂停成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @RequestMapping(value = "/pause", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public R pause(@RequestBody String ids) {
        try {
            String[] split = ids.split(",");
            if (ArrayUtils.isNotEmpty(split)) {
                scheduleJobService.pause(split);
            }
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
    }

    /**
     * 恢复定时任务
     * <p>
     * 恢复已暂停的定时任务，支持批量恢复
     * </p>
     *
     * @param ids 任务ID列表，多个ID用逗号分隔
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "恢复定时任务", description = "恢复已暂停的定时任务，支持批量恢复")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "任务ID列表，多个ID用逗号分隔", required = true, content = @Content(schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "恢复成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @RequestMapping(value = "/resume", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public R resume(@RequestBody String ids) {
        try {
            String[] split = ids.split(",");
            if (ArrayUtils.isNotEmpty(split)) {
                scheduleJobService.resume(split);
            }
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
    }

    /**
     * 分页查询定时任务日志
     * <p>
     * 查询定时任务的执行日志列表，支持分页和条件筛选
     * </p>
     *
     * @param searchForm 搜索表单，包含分页信息和筛选条件
     * @return 包含任务日志列表和分页信息的查询结果集
     */
    @Operation(summary = "分页查询定时任务日志", description = "查询定时任务的执行日志列表，支持分页和条件筛选")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "搜索条件", required = true, content = @Content(schema = @Schema(implementation = SearchForm.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = R.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @PostMapping(value = "/logs", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<QueryResultSet<JobLogVo>> logs(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String condition = searchForm.getCondition();
        SearchParam param = startPaging(searchForm);
        setSearchForm(request, searchForm);
        if (StringUtils.isNotEmpty(condition)) {
            param.addFilter("condition", condition);
        }
        QueryResultSet<JobLogVo> resultSet = scheduleJobLogService.search(param).map(JobLogVoConverter.class);
        return R.ok(resultSet);
    }

    /**
     * 删除定时任务日志
     * <p>
     * 支持批量删除定时任务日志，多个ID用逗号分隔
     * </p>
     *
     * @param ids 日志ID列表，多个ID用逗号分隔
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "删除定时任务日志", description = "支持批量删除定时任务日志，多个ID用逗号分隔")
    @Parameter(name = "ids", description = "日志ID列表，多个ID用逗号分隔", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @DeleteMapping(value = "/logs", produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteManyLogs(@RequestParam String ids) {
        try {
            // 使用验证工具类解析和验证参数
            List<String> logIdList = BatchOperationValidator.validateAndParseIds(ids);
            scheduleJobLogService.deleteMany(StringUtils.join(logIdList, ","));
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
    }

    /**
     * 删除所有定时任务日志
     * <p>
     * 清空所有定时任务的执行日志
     * </p>
     *
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "删除所有定时任务日志", description = "清空所有定时任务的执行日志")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @DeleteMapping(value = "/logs/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteAllLogs() {
        try {
            scheduleJobLogService.deleteByBeanName("");
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
    }

    /**
     * 获取Cron表达式下次执行时间
     * <p>
     * 根据Cron表达式计算下一次执行的时间
     * </p>
     *
     * @return 下次执行时间
     */
    @Operation(summary = "获取Cron表达式下次执行时间", description = "根据Cron表达式计算下一次执行的时间")
    @Parameter(name = "cronExpression", description = "Cron表达式", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "计算成功", content = @Content(schema = @Schema(implementation = Date.class))),
            @ApiResponse(responseCode = "400", description = "Cron表达式格式错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @RequestMapping(value = "/nextTime", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<Date> getNextValidTime() {
        String cronExpression = request.getParameter("cronExpression");
        Date nextExecution = CronUtils.getNextExecution(cronExpression);
        return R.ok(nextExecution);
    }

    @Data
    @lombok.EqualsAndHashCode(callSuper = false)
    public static class SearchForm extends BaseSearchForm {
        private String condition;

        @Override
        public String toString() {
            return "SearchForm{" +
                    "condition='" + condition + '\'' +
                    '}';
        }
    }

    public static SearchForm getSearchForm(HttpServletRequest request) {
        SearchForm searchForm = (SearchForm) request.getSession().getAttribute(SEARCH_FORM_KEY);
        if (searchForm == null) {
            searchForm = new SearchForm();
        }
        setSearchForm(request, searchForm);
        return searchForm;
    }

    public static void setSearchForm(HttpServletRequest request, SearchForm searchForm) {
        request.getSession().setAttribute(SEARCH_FORM_KEY, searchForm);
    }

    public static void cleanSearchForm(HttpServletRequest request) {
        request.getSession().removeAttribute(SEARCH_FORM_KEY);
    }
}
