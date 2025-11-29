package com.adminpro.web.rbac;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.annotation.SysLog;
import com.adminpro.rbac.domains.entity.domain.DomainCreateValidator;
import com.adminpro.rbac.domains.entity.domain.DomainEntity;
import com.adminpro.rbac.domains.entity.domain.DomainService;
import com.adminpro.rbac.domains.entity.domain.DomainUpdateValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户域 信息操作处理
 *
 * @author simon
 * @date 2020-06-14
 */
@RestController
@RequestMapping("/admin/domain")
@PreAuthorize("@ss.hasPermission('system:domain')")
public class DomainController extends BaseRoutingController {
    protected static final String PREFIX = "admin/domain";
    protected static final String PREFIX_URL = "/admin/domain";
    protected static final String SEARCH_FORM_KEY = "domainSearchForm";

    @Autowired
    private DomainService domainService;

    @Autowired
    private DomainCreateValidator domainCreateValidator;

    @Autowired
    private DomainUpdateValidator domainUpdateValidator;

    /**
     * 查询用户域列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<QueryResultSet<DomainEntity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String name = searchForm.getName();
        String display = searchForm.getDisplay();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(name)) {
            param.addFilter("name", name);
        }
        if (StringUtils.isNotEmpty(display)) {
            param.addFilter("display", display);
        }
        QueryResultSet<DomainEntity> resultSet = domainService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 新增保存用户域
     */
    @SysLog("创建用户域")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public R create(@RequestBody DomainEntity userDomain) {
        BeanUtil.beanAttributeValueTrim(userDomain);
        MessageBundle messageBundle = getMessageBundle();
        domainCreateValidator.validate(userDomain, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String name = userDomain.getName();
            String display = userDomain.getDisplay();
            Boolean isSystem = userDomain.getIsSystem();

            DomainEntity entity = new DomainEntity();
            entity.setName(name);
            entity.setDisplay(display);
            entity.setIsSystem(isSystem);

            domainService.create(entity);
            return R.ok();
        }
    }

    /**
     * 修改保存用户域
     */
    @SysLog("更新用户域")
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    public R editSave(@RequestBody DomainEntity userDomain) {
        BeanUtil.beanAttributeValueTrim(userDomain);
        MessageBundle messageBundle = getMessageBundle();

        domainUpdateValidator.validate(userDomain, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            DomainEntity entity = domainService.findById(userDomain.getId());
            String name = userDomain.getName();
            String display = userDomain.getDisplay();
            Boolean isSystem = userDomain.getIsSystem();

            entity.setName(name);
            entity.setDisplay(display);
            entity.setIsSystem(isSystem);

            domainService.update(entity);
            return R.ok();
        }
    }

    /**
     * 获取详细信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public R<DomainEntity> detail(@PathVariable String id) {
        DomainEntity entity = domainService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    public static class SearchForm extends BaseSearchForm {
        private String name;
        private String display;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
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
