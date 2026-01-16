package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.tools.domains.entity.session.SessionEntity;
import com.adminpro.system.tools.domains.entity.session.SessionService;
import com.adminpro.system.tools.domains.enums.SessionStatus;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Session 信息操作处理
 *
 * @author simon
 * @date 2020-06-17
 */
@RestController
@RequestMapping(SessionController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:session')")
public class SessionController extends BaseController {

    protected static final String PREFIX_URL = "/admin/session";
    protected static final String SEARCH_FORM_KEY = "sessionSearchForm";

    @Autowired
    private SessionService sessionService;

    /**
     * 查询用户Session列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<QueryResultSet<SessionEntity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String sessionId = searchForm.getSessionId();
        String status = searchForm.getStatus();
        String loginName = searchForm.getLoginName();
        String ipAddr = searchForm.getIpAddr();
        String deptNo = searchForm.getDeptNo();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(sessionId)) {
            param.addFilter("sessionId", sessionId);
        }
        if (StringUtils.isNotEmpty(status)) {
            param.addFilter("status", status);
        }
        if (StringUtils.isNotEmpty(loginName)) {
            param.addFilter("loginName", loginName);
        }
        if (StringUtils.isNotEmpty(ipAddr)) {
            param.addFilter("ipAddr", ipAddr);
        }
        if (StringUtils.isNotEmpty(deptNo)) {
            param.addFilter("deptNo", deptNo);
        }
        QueryResultSet<SessionEntity> resultSet = sessionService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 获取详细信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public R<SessionEntity> detail(@PathVariable String id) {
        SessionEntity entity = sessionService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    @SysLog("暂停会话")
    @RequestMapping(value = "/suspend/{id}", method = RequestMethod.PATCH)
    public R<SessionEntity> suspend(@PathVariable String id) {
        SessionEntity entity = sessionService.findById(id);
        if (StringHelper.equals(WebHelper.getSessionId(), entity.getSessionId())) {
            return R.error("不能操作当前用户");
        }
        if (entity != null) {
            entity.setStatus(SessionStatus.SUSPEND.getCode());
            sessionService.update(entity);
            return R.ok();
        } else {
            return R.error("对象不存在");
        }
    }

    @SysLog("恢复会话")
    @RequestMapping(value = "/unsuspend/{id}", method = RequestMethod.PATCH)
    public R<SessionEntity> unsuspend(@PathVariable String id) {
        SessionEntity entity = sessionService.findById(id);
        if (StringHelper.equals(WebHelper.getSessionId(), entity.getSessionId())) {
            return R.error("不能操作当前用户");
        }
        if (entity != null) {
            entity.setStatus(SessionStatus.ACTIVE.getCode());
            sessionService.update(entity);
            return R.ok();
        } else {
            return R.error("对象不存在");
        }
    }

    @SysLog("终止会话")
    @RequestMapping(value = "/kill/{id}", method = RequestMethod.PATCH)
    public R<SessionEntity> kill(@PathVariable String id) {
        SessionEntity entity = sessionService.findById(id);
        if (StringHelper.equals(WebHelper.getSessionId(), entity.getSessionId())) {
            return R.error("不能操作当前用户");
        }
        if (entity != null) {
            entity.setStatus(SessionStatus.KILLED.getCode());
            sessionService.update(entity);
            return R.ok();
        } else {
            return R.error("对象不存在");
        }
    }

    @Data
    public static class SearchForm extends BaseSearchForm {
        /**
         * Session ID
         */
        private String sessionId;
        /**
         * 状态
         */
        private String status;
        /**
         * 用户登录名
         */
        private String loginName;
        /**
         * 登陆IP
         */
        private String ipAddr;
        /**
         * 部门编号
         */
        private String deptNo;

        @Override
        public String toString() {
            return "SearchForm{" +
                    "sessionId='" + sessionId + '\'' +
                    ", status='" + status + '\'' +
                    ", loginName='" + loginName + '\'' +
                    ", ipAddr='" + ipAddr + '\'' +
                    ", deptNo='" + deptNo + '\'' +
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
