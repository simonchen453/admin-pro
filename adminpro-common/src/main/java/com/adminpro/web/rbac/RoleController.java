package com.adminpro.web.rbac;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.annotation.SysLog;
import com.adminpro.rbac.domains.entity.role.RoleCreateValidator;
import com.adminpro.rbac.domains.entity.role.RoleEntity;
import com.adminpro.rbac.domains.entity.role.RoleService;
import com.adminpro.rbac.domains.entity.role.RoleUpdateValidator;
import com.adminpro.rbac.domains.vo.role.ListRoleVo;
import com.adminpro.rbac.domains.vo.role.ListRoleVoConverter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 角色 信息操作处理
 *
 * @author simon
 * @date 2020-06-08
 */
@Controller
@RequestMapping("/admin/role")
@PreAuthorize("@ss.hasPermission('system:role')")
public class RoleController extends BaseRoutingController {
    protected static final String PREFIX = "admin/role";
    protected static final String PREFIX_URL = "/admin/role";
    protected static final String SEARCH_FORM_KEY = "roleSearchForm";

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleCreateValidator roleCreateValidator;

    @Autowired
    private RoleUpdateValidator roleUpdateValidator;

    @GetMapping()
    public String prepareList() {
        return "forward:" + PREFIX_URL + "/list";
    }

    @GetMapping("/list")
    public String role() {
        prepareData();
        getSearchForm();
        return PREFIX + "/list";
    }

    /**
     * 查询角色列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public R<QueryResultSet<ListRoleVo>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String name = searchForm.getName();
        String display = searchForm.getDisplay();
        String status = searchForm.getStatus();
        Boolean system = searchForm.system;

        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(name)) {
            param.addFilter("name", name);
        }
        if (StringUtils.isNotEmpty(display)) {
            param.addFilter("display", display);
        }
        if (StringUtils.isNotEmpty(status)) {
            param.addFilter("status", status);
        }
        if (system != null) {
            param.addFilter("system", system);
        }
        QueryResultSet<ListRoleVo> resultSet = roleService.search(param).map(ListRoleVoConverter.class);
        return R.ok(resultSet);
    }

    /**
     * 新增保存角色
     */
    @SysLog("创建角色")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public R create(@RequestBody RoleEntity role) {
        BeanUtil.beanAttributeValueTrim(role);
        MessageBundle messageBundle = getMessageBundle();
        roleCreateValidator.validate(role, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String name = role.getName();
            String display = role.getDisplay();
            Boolean isSystem = role.isSystem();

            RoleEntity entity = new RoleEntity();
            entity.setName(name);
            entity.setDisplay(display);
            entity.setSystem(isSystem);
            entity.setStatus(role.getStatus());
            entity.setSystem(role.isSystem());
            entity.setMenuNames(role.getMenuNames());
            roleService.create(entity);
            return R.ok();
        }
    }

    /**
     * 修改保存角色
     */
    @SysLog("更新角色")
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    @ResponseBody
    public R editSave(@RequestBody RoleEntity role) {
        BeanUtil.beanAttributeValueTrim(role);
        MessageBundle messageBundle = getMessageBundle();

        roleUpdateValidator.validate(role, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            RoleEntity entity = roleService.findById(role.getId());
            String name = role.getName();
            String display = role.getDisplay();
            Boolean isSystem = role.isSystem();

            entity.setName(name);
            entity.setDisplay(display);
            entity.setSystem(isSystem);
            entity.setStatus(role.getStatus());
            entity.setMenuNames(role.getMenuNames());
            roleService.update(entity);
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
    public R<RoleEntity> detail(@PathVariable String id) {
        RoleEntity entity = roleService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 删除角色
     */
    @SysLog("删除角色")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public R remove(@RequestParam("ids") String ids) {
        roleService.deleteMany(ids);
        return R.ok();
    }

    @Data
    public static class SearchForm extends BaseSearchForm {
        private String name;
        private String display;
        private String status;
        private Boolean system;
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
