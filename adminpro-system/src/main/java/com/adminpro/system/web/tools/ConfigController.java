package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.tools.domains.entity.config.ConfigCreateValidator;
import com.adminpro.system.tools.domains.entity.config.ConfigEntity;
import com.adminpro.system.tools.domains.entity.config.ConfigService;
import com.adminpro.system.tools.domains.entity.config.ConfigUpdateValidator;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参数配置 信息操作处理
 *
 * @author simon
 * @date 2020-06-15
 */
@RestController
@RequestMapping(ConfigController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:config')")
public class ConfigController extends BaseController {

    protected static final String PREFIX_URL = "/admin/config";
    protected static final String SEARCH_FORM_KEY = "configSearchForm";

    @Autowired
    private ConfigService configService;

    @Autowired
    private ConfigCreateValidator configCreateValidator;

    @Autowired
    private ConfigUpdateValidator configUpdateValidator;

    /**
     * 查询参数配置列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
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
     * 新增保存参数配置
     */
    @SysLog("创建配置")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
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
     * 修改保存参数配置
     */
    @SysLog("更新配置")
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
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
    @SysLog("删除配置")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public R remove(@RequestParam("ids") String ids) {
        // 使用验证工具类解析和验证参数
        List<String> configIdList = BatchOperationValidator.validateAndParseIds(ids);
        configService.deleteByIds(StringUtils.join(configIdList, ","));
        return R.ok();
    }

    @Data
    public static class SearchForm extends BaseSearchForm {
        private String name;
        private String key;
        private String value;

        @Override
        public String toString() {
            return "SearchForm{" +
                    "name='" + name + '\'' +
                    ", key='" + key + '\'' +
                    ", value='" + value + '\'' +
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
