package com.adminpro.web.tools;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.enums.CommonStatus;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.annotation.SysLog;
import com.adminpro.tools.domains.entity.dict.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 字典类型 信息操作处理
 *
 * @author simon
 * @date 2020-08-22
 */
@Controller
@RequestMapping("/admin/dict")
@PreAuthorize("@ss.hasPermission('system:dict')")
public class DictController extends BaseRoutingController {
    protected static final String PREFIX = "admin/dict";
    protected static final String PREFIX_URL = "/admin/dict";
    protected static final String SEARCH_FORM_KEY = "dictSearchForm";

    @Autowired
    private DictService dictService;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private DictCreateValidator dictCreateValidator;

    @Autowired
    private DictUpdateValidator dictUpdateValidator;

    @GetMapping()
    public String prepareList() {
        return "forward:" + PREFIX_URL + "/list";
    }

    @GetMapping("/list")
    public String dict() {
        prepareData();
        getSearchForm();
        return PREFIX + "/list";
    }

    /**
     * 查询字典类型列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public R<QueryResultSet<DictEntity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String name = searchForm.getName();
        String key = searchForm.getKey();
        String status = searchForm.getStatus();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(name)) {
            param.addFilter("name", name);
        }
        if (StringUtils.isNotEmpty(key)) {
            param.addFilter("key", key);
        }
        if (StringUtils.isNotEmpty(status)) {
            param.addFilter("status", status);
        }
        QueryResultSet<DictEntity> resultSet = dictService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 新增保存字典类型
     */
    @SysLog("创建字典")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public R create(@RequestBody DictEntity dict) {
        BeanUtil.beanAttributeValueTrim(dict);
        MessageBundle messageBundle = getMessageBundle();
        dictCreateValidator.validate(dict, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String name = dict.getName();
            String key = dict.getKey();
            String status = dict.getStatus();
            String remark = dict.getRemark();

            DictEntity entity = new DictEntity();
            entity.setName(name);
            entity.setKey(key);
            entity.setStatus(status);
            entity.setRemark(remark);
            entity.setData(dict.getData());

            dictService.create(entity);
            return R.ok();
        }
    }

    /**
     * 修改保存字典类型
     */
    @SysLog("更新字典")
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    @ResponseBody
    public R editSave(@RequestBody DictEntity dict) {
        BeanUtil.beanAttributeValueTrim(dict);
        MessageBundle messageBundle = getMessageBundle();

        dictUpdateValidator.validate(dict, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            DictEntity entity = dictService.findById(dict.getId());
            String name = dict.getName();
            String key = dict.getKey();
            String status = dict.getStatus();
            String remark = dict.getRemark();

            entity.setName(name);
            entity.setKey(key);
            entity.setStatus(status);
            entity.setRemark(remark);
            entity.setData(dict.getData());

            dictService.update(entity);
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
    public R<DictEntity> detail(@PathVariable String id) {
        DictEntity entity = dictService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 删除字典类型
     */
    @SysLog("删除字典")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public R remove(@RequestParam("ids") String ids) {
        dictService.deleteByIds(ids);
        return R.ok();
    }

    /**
     * 激活
     *
     * @param id
     * @return
     */
    @SysLog("激活字典")
    @RequestMapping(value = "/active/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public R<DictEntity> active(@PathVariable String id) {
        DictEntity entity = dictService.findById(id);
        if (entity != null) {
            entity.setStatus(CommonStatus.ACTIVE.getCode());
            dictService.update(entity);
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 停用
     *
     * @param id
     * @return
     */
    @SysLog("停用字典")
    @RequestMapping(value = "/inactive/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public R<DictEntity> inactive(@PathVariable String id) {
        DictEntity entity = dictService.findById(id);
        if (entity != null) {
            entity.setStatus(CommonStatus.INACTIVE.getCode());
            dictService.update(entity);
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    @Data
    public static class SearchForm extends BaseSearchForm {
        /**
         * 字典名称
         */
        private String name;
        /**
         * 字典类型
         */
        private String key;
        /**
         * 状态
         */
        private String status;
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
