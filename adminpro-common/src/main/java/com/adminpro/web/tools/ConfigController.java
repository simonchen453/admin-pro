package com.adminpro.web.tools;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.tools.domains.entity.config.ConfigCreateValidator;
import com.adminpro.tools.domains.entity.config.ConfigEntity;
import com.adminpro.tools.domains.entity.config.ConfigService;
import com.adminpro.tools.domains.entity.config.ConfigUpdateValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 参数配置 信息操作处理
 *
 * @author simon
 * @date 2020-06-15
 */
@Controller
@RequestMapping("/admin/config")
@PreAuthorize("@ss.hasPermission('system:config')")
public class ConfigController extends BaseRoutingController {
    protected static final String PREFIX = "admin/config";
    protected static final String PREFIX_URL = "/admin/config";
    protected static final String SEARCH_FORM_KEY = "configSearchForm";

    @Autowired
    private ConfigService configService;

    @Autowired
    private ConfigCreateValidator configCreateValidator;

    @Autowired
    private ConfigUpdateValidator configUpdateValidator;

    @GetMapping()
    public String prepareList() {
        return "forward:" + PREFIX_URL + "/list";
    }

    @GetMapping("/list")
    public String config() {
        prepareData();
        getSearchForm();
        return PREFIX + "/list";
    }

    /**
     * 查询参数配置列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public R<QueryResultSet<ConfigEntity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String name = searchForm.getName();
        String key = searchForm.getKey();
        String value = searchForm.getValue();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(name)) {
            param.addFilter("name", name);
        }
        if (StringUtils.isNotEmpty(key)) {
            param.addFilter("key", key);
        }
        if (StringUtils.isNotEmpty(value)) {
            param.addFilter("value", value);
        }

        QueryResultSet<ConfigEntity> resultSet = configService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 新增参数配置
     */
    @GetMapping("/create")
    public String add() {
        prepareData();
        return PREFIX + "/create";
    }

    /**
     * 新增保存参数配置
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public R create(@RequestBody ConfigEntity config) {
        BeanUtil.beanAttributeValueTrim(config);
        MessageBundle messageBundle = getMessageBundle();
        configCreateValidator.validate(config, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String key = config.getKey();
            String name = config.getName();
            String value = config.getValue();
            Integer system = config.getSystem();
            String remark = config.getRemark();

            ConfigEntity entity = new ConfigEntity();
            entity.setKey(key);
            entity.setName(name);
            entity.setValue(value);
            entity.setSystem(system);
            entity.setRemark(remark);

            configService.create(entity);
            return R.ok();
        }
    }

    /**
     * 修改参数配置
     */
    @GetMapping("/edit")
    public String edit(@RequestParam("id") String id) {
        prepareData();
        ConfigEntity config = configService.findById(id);
        request.setAttribute("config", config);
        return PREFIX + "/edit";
    }

    /**
     * 修改保存参数配置
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    @ResponseBody
    public R editSave(@RequestBody ConfigEntity config) {
        BeanUtil.beanAttributeValueTrim(config);
        MessageBundle messageBundle = getMessageBundle();

        configUpdateValidator.validate(config, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            ConfigEntity entity = configService.findById(config.getId());
            String key = config.getKey();
            String name = config.getName();
            String value = config.getValue();
            Integer system = config.getSystem();
            String remark = config.getRemark();

            entity.setKey(key);
            entity.setName(name);
            entity.setValue(value);
            entity.setSystem(system);
            entity.setRemark(remark);

            configService.update(entity);
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
    public R<ConfigEntity> detail(@PathVariable String id) {
        ConfigEntity entity = configService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 删除参数配置
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public R remove(@RequestParam("ids") String ids) {
        configService.deleteByIds(ids);
        return R.ok();
    }

    public static class SearchForm extends BaseSearchForm {
        private String name;
        private String key;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
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
