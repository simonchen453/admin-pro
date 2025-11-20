package com.adminpro.web.tools;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.rbac.domains.entity.domain.DomainEntity;
import com.adminpro.rbac.domains.entity.domain.DomainService;
import com.adminpro.tools.domains.entity.session.SessionEntity;
import com.adminpro.tools.domains.entity.session.SessionService;
import com.adminpro.tools.domains.enums.SessionStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户Session 信息操作处理
 *
 * @author simon
 * @date 2020-06-17
 */
@Controller
@RequestMapping("/admin/session")
@PreAuthorize("@ss.hasPermission('system:session')")
public class SessionController extends BaseRoutingController {
    protected static final String PREFIX = "admin/session";
    protected static final String PREFIX_URL = "/admin/session";
    protected static final String SEARCH_FORM_KEY = "sessionSearchForm";

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DomainService domainService;

    @GetMapping()
    public String prepareList() {
        return "forward:" + PREFIX_URL + "/list";
    }

    @GetMapping("/list")
    public String session() {
        prepareData();
        getSearchForm();
        List<DomainEntity> all = domainService.findAll();
        request.setAttribute("domains", all);
        return PREFIX + "/list";
    }

    /**
     * 查询用户Session列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public R<QueryResultSet<SessionEntity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String sessionId = searchForm.getSessionId();
        String status = searchForm.getStatus();
        String userDomain = searchForm.getUserDomain();
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
        if (StringUtils.isNotEmpty(userDomain)) {
            param.addFilter("userDomain", userDomain);
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
    @ResponseBody
    public R<SessionEntity> detail(@PathVariable String id) {
        SessionEntity entity = sessionService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    @RequestMapping(value = "/suspend/{id}", method = RequestMethod.PATCH)
    @ResponseBody
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

    @RequestMapping(value = "/unsuspend/{id}", method = RequestMethod.PATCH)
    @ResponseBody
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

    @RequestMapping(value = "/kill/{id}", method = RequestMethod.PATCH)
    @ResponseBody
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
         * 用户域
         */
        private String userDomain;
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

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUserDomain() {
            return userDomain;
        }

        public void setUserDomain(String userDomain) {
            this.userDomain = userDomain;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getIpAddr() {
            return ipAddr;
        }

        public void setIpAddr(String ipAddr) {
            this.ipAddr = ipAddr;
        }

        public String getDeptNo() {
            return deptNo;
        }

        public void setDeptNo(String deptNo) {
            this.deptNo = deptNo;
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
