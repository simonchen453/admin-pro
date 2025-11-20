package com.adminpro.web.rbac;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.rbac.domains.entity.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户域环境配置 信息操作处理
 *
 * @author simon
 * @date 2020-06-14
 */
@Controller
@RequestMapping("/admin/userDomainEnv")
@PreAuthorize("@ss.hasPermission('system:user_domain_env')")
public class UserDomainEnvController extends BaseRoutingController {
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

    @GetMapping("/list")
    public String userDomainEnv() {
        prepareData();
        getSearchForm();
        List<DomainEntity> all = domainService.findAll();
        request.setAttribute("domains", all);
        return PREFIX + "/list";
    }

    /**
     * 查询用户域环境配置列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
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
    @ResponseBody
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
     * 修改用户域环境配置
     */
    @GetMapping("/edit")
    public String edit(@RequestParam("id") String id) {
        prepareData();
        UserDomainEnvEntity userDomainEnv = userDomainEnvService.findById(id);
        request.setAttribute("userDomainEnv", userDomainEnv);
        return PREFIX + "/edit";
    }

    /**
     * 修改保存用户域环境配置
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
    public R remove(@RequestParam("ids") String ids) {
        userDomainEnvService.deleteByIds(ids);
        return R.ok();
    }

    public static class SearchForm extends BaseSearchForm {
        private String userDomain;

        public String getUserDomain() {
            return userDomain;
        }

        public void setUserDomain(String userDomain) {
            this.userDomain = userDomain;
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
