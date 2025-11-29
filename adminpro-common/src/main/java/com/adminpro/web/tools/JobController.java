package com.adminpro.web.tools;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.batchjob.ScheduleStatus;
import com.adminpro.framework.batchjob.utils.CronUtils;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.rbac.domains.vo.job.*;
import com.adminpro.rbac.domains.vo.role.ListRoleVo;
import com.adminpro.tools.domains.entity.job.ScheduleJobEntity;
import com.adminpro.tools.domains.entity.job.ScheduleJobLogService;
import com.adminpro.tools.domains.entity.job.ScheduleJobService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/admin/job")
@PreAuthorize("@ss.hasPermission('system:job')")
public class JobController extends BaseRoutingController {

    protected static final String PREFIX = "admin/job";
    protected static final String PREFIX_URL = "/admin/job";
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

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<QueryResultSet<ListRoleVo>> paging(@RequestBody SearchForm searchForm) {
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

    @RequestMapping(method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteMany(@RequestParam String ids) {
        try {
            scheduleJobService.deleteMany(ids);
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
    }

    @RequestMapping(value = "/detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public R detail(@PathVariable String id) {
        ScheduleJobEntity entity = scheduleJobService.findById(id);
        JobVo convert = jobVoConverter.convert(entity);
        return R.ok(convert);
    }

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

    @RequestMapping(value = "/log/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<QueryResultSet<ListRoleVo>> logs(@RequestBody SearchForm searchForm) {
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

    @RequestMapping(value = "/log/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteManyLogs(@RequestParam String ids) {
        try {
            scheduleJobLogService.deleteMany(ids);
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
    }

    @RequestMapping(value = "/log/delete/all", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteAllLogs() {
        try {
            scheduleJobLogService.deleteByBeanName("");
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
    }

    @RequestMapping(value = "/nextTime", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<Date> getNextValidTime() {
        String cronExpression = request.getParameter("cronExpression");
        Date nextExecution = CronUtils.getNextExecution(cronExpression);
        return R.ok(nextExecution);
    }

    public static class SearchForm extends BaseSearchForm {
        private String condition;

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
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
