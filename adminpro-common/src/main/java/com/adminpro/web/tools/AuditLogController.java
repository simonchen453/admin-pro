package com.adminpro.web.tools;


import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.tools.domains.entity.auditlog.AuditLogDTO;
import com.adminpro.tools.domains.entity.auditlog.AuditLogEntity;
import com.adminpro.tools.domains.entity.auditlog.AuditLogService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 日志 日志管理
 *
 * @author dongqin
 * @date 2019-10-21
 */
@Controller
@RequestMapping("/admin/audit")
@PreAuthorize("@ss.hasPermission('system:audit')")
public class AuditLogController extends BaseRoutingController {

    private static final String PREFIX = "admin/auditlog";
    protected static final String PREFIX_URL = "/admin/audit";
    protected static final String SEARCH_FORM_KEY = "auditLogSearchForm";

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping()
    public String prepareList() {
        cleanSearchForm();
        return "forward:" + PREFIX_URL + "/list";
    }

    @GetMapping("/list")
    public String auditlog() {
        prepareData();
        getSearchForm();
        return PREFIX + "/list";
    }


    /**
     * 查询日志
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public R<QueryResultSet<AuditLogEntity>> search(@RequestBody SearchForm searchForm) {
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


    /**
     * 查看日志
     *
     * @return
     */
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String view() throws Exception {
        prepareData();
        String id = request.getParameter("id");
        if (StringHelper.isEmpty(id)) {
            throw new Exception("日志id丢失!");
        }
        AuditLogEntity auditLogEntity = auditLogService.findById(id);
        if (auditLogEntity == null) {
            throw new Exception("日志编号" + id + "不存在!");
        }
        request.setAttribute("entity", auditLogEntity);
        return PREFIX + "/view";
    }

    @Data
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


