package com.adminpro.system.web.rbac;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.rbac.domains.entity.domain.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

/**
 * 用户域环境配置 信息操作处理
 *
 * @author simon
 * @date 2020-06-14
 */
@RestController
@RequestMapping("/admin/userDomainEnv")
@PreAuthorize("@ss.hasPermission('system:user_domain_env')")
public class UserDomainEnvController extends BaseController {
    protected static final String PREFIX = "admin/userdomainenv";
    protected static final String PREFIX_URL = "/admin/userDomainEnv";
    protected static final String SEARCH_FORM_KEY = "userDomainEnvSearchForm";

    @Autowired
    private UserDomainEnvService userDomainEnvService;

    @Autowired
    private UserDomainEnvCreateValidator userDomainEnvCreateValidator;

    @Autowired
    private UserDomainEnvUpdateValidator userDomainEnvUpdateValidator;

    @Autowired
    private DomainService domainService;

    @GetMapping()
    public String prepareList() {
        return "forward:" + PREFIX_URL + "/list";
    }

    /**
     * 查询用户域环境配置列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<QueryResultSet<UserDomainEnvEntity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String userDomain = searchForm.getUserDomain();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(userDomain)) {
            param.addFilter("userDomain", userDomain);
        }

        QueryResultSet<UserDomainEnvEntity> resultSet = userDomainEnvService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 新增保存用户域环境配置
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public R create(@RequestBody UserDomainEnvEntity userDomainEnv) {
        BeanUtil.beanAttributeValueTrim(userDomainEnv);
        MessageBundle messageBundle = getMessageBundle();
        userDomainEnvCreateValidator.validate(userDomainEnv, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String commonRole = userDomainEnv.getCommonRole();
            String description = userDomainEnv.getDescription();
            String errorPageUrl = userDomainEnv.getErrorPageUrl();
            String fatalErrorPageUrl = userDomainEnv.getFatalErrorPageUrl();
            String homePageUrl = userDomainEnv.getHomePageUrl();
            String loginUrl = userDomainEnv.getLoginUrl();
            String layout = userDomainEnv.getLayout();
            String sessionExpiredUrl = userDomainEnv.getSessionExpiredUrl();
            String userDomain = userDomainEnv.getUserDomain();

            UserDomainEnvEntity entity = new UserDomainEnvEntity();
            entity.setCommonRole(commonRole);
            entity.setDescription(description);
            entity.setErrorPageUrl(errorPageUrl);
            entity.setFatalErrorPageUrl(fatalErrorPageUrl);
            entity.setHomePageUrl(homePageUrl);
            entity.setLoginUrl(loginUrl);
            entity.setLayout(layout);
            entity.setSessionExpiredUrl(sessionExpiredUrl);
            entity.setUserDomain(userDomain);

            userDomainEnvService.create(entity);
            return R.ok();
        }
    }

    /**
     * 修改保存用户域环境配置
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    public R editSave(@RequestBody UserDomainEnvEntity userDomainEnv) {
        BeanUtil.beanAttributeValueTrim(userDomainEnv);
        MessageBundle messageBundle = getMessageBundle();

        userDomainEnvUpdateValidator.validate(userDomainEnv, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            UserDomainEnvEntity entity = userDomainEnvService.findById(userDomainEnv.getId());
            String commonRole = userDomainEnv.getCommonRole();
            String description = userDomainEnv.getDescription();
            String errorPageUrl = userDomainEnv.getErrorPageUrl();
            String fatalErrorPageUrl = userDomainEnv.getFatalErrorPageUrl();
            String homePageUrl = userDomainEnv.getHomePageUrl();
            String layout = userDomainEnv.getLayout();
            String sessionExpiredUrl = userDomainEnv.getSessionExpiredUrl();
            String userDomain = userDomainEnv.getUserDomain();
            String loginUrl = userDomainEnv.getLoginUrl();

            entity.setCommonRole(commonRole);
            entity.setDescription(description);
            entity.setErrorPageUrl(errorPageUrl);
            entity.setFatalErrorPageUrl(fatalErrorPageUrl);
            entity.setHomePageUrl(homePageUrl);
            entity.setLoginUrl(loginUrl);
            entity.setLayout(layout);
            entity.setSessionExpiredUrl(sessionExpiredUrl);
            entity.setUserDomain(userDomain);

            userDomainEnvService.update(entity);
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
    public R<UserDomainEnvEntity> detail(@PathVariable String id) {
        UserDomainEnvEntity entity = userDomainEnvService.findById(id);
        if (entity != null) {
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 删除用户域环境配置
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public R remove(@RequestParam("ids") String ids) {
        userDomainEnvService.deleteByIds(ids);
        return R.ok();
    }

    @Data
    public static class SearchForm extends BaseSearchForm {
        private String userDomain;

        @Override
        public String toString() {
            return "SearchForm{" +
                    "userDomain='" + userDomain + '\'' +
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
