package com.adminpro.web.rbac;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.annotation.SysLog;
import com.adminpro.rbac.domains.entity.menu.MenuCreateValidator;
import com.adminpro.rbac.domains.entity.menu.MenuEntity;
import com.adminpro.rbac.domains.entity.menu.MenuService;
import com.adminpro.rbac.domains.entity.menu.MenuUpdateValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单权限 信息操作处理
 *
 * @author simon
 * @date 2020-05-21
 */
@RestController
@RequestMapping("/admin/menu")
@PreAuthorize("@ss.hasPermission('system:menu')")
public class MenuController extends BaseRoutingController {
    protected static final String PREFIX = "admin/menu";
    protected static final String PREFIX_URL = "/admin/menu";
    protected static final String SEARCH_FORM_KEY = "menuSearchForm";

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuCreateValidator menuCreateValidator;

    @Autowired
    private MenuUpdateValidator menuUpdateValidator;

    /**
     * 查询菜单权限列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<List<MenuEntity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String name = searchForm.getName();
        String status = searchForm.getStatus();
        Boolean visible = searchForm.isVisible();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(name)) {
            param.addFilter("name", name);
        }
        if (StringUtils.isNotEmpty(status)) {
            param.addFilter("status", status);
        }
        if (visible != null) {
            param.addFilter("visible", visible);
        }
        List<MenuEntity> list = menuService.findByParam(param);
        return R.ok(list);
    }

    /**
     * 查询菜单详情
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public R<List<MenuEntity>> detail(@PathVariable String id) {
        MenuEntity entity = menuService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 查询菜单权限列表
     */
    @SysLog("删除菜单")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public R<List<MenuEntity>> delete(@PathVariable String id) {
        MenuEntity entity = menuService.findById(id);
        if (entity != null) {
            menuService.delete(entity.getId());
        }

        return R.ok(entity);
    }

    /**
     * 新增保存菜单权限
     */
    @SysLog("创建菜单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public R create(@RequestBody MenuEntity menu) {
        BeanUtil.beanAttributeValueTrim(menu);
        MessageBundle messageBundle = getMessageBundle();
        menuCreateValidator.validate(menu, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String name = menu.getName();
            String display = menu.getDisplay();
            String parentId = menu.getParentId();
            Integer orderNum = menu.getOrderNum();
            String url = menu.getUrl();
            boolean isFrame = menu.isFrame();
            String menuType = menu.getType();
            String visible = menu.getVisible();
            String status = menu.getStatus();
            String permission = menu.getPermission();
            String icon = menu.getIcon();
            String remark = menu.getRemark();

            MenuEntity entity = new MenuEntity();
            entity.setName(name);
            entity.setDisplay(display);
            entity.setParentId(parentId);
            entity.setOrderNum(orderNum);
            entity.setUrl(url);
            entity.setFrame(isFrame);
            entity.setType(menuType);
            entity.setVisible(visible);
            entity.setStatus(status);
            entity.setPermission(permission);
            entity.setIcon(icon);
            entity.setRemark(remark);

            menuService.create(entity);
            return R.ok();
        }
    }

    /**
     * 修改保存菜单权限
     */
    @SysLog("更新菜单")
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    public R editSave(@RequestBody MenuEntity menu) {
        BeanUtil.beanAttributeValueTrim(menu);
        MessageBundle messageBundle = getMessageBundle();

        menuUpdateValidator.validate(menu, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            MenuEntity entity = menuService.findById(menu.getId());
            String name = menu.getName();
            String display = menu.getDisplay();
            String parentId = menu.getParentId();
            Integer orderNum = menu.getOrderNum();
            String url = menu.getUrl();
            boolean isFrame = menu.isFrame();
            String menuType = menu.getType();
            String visible = menu.getVisible();
            String status = menu.getStatus();
            String permission = menu.getPermission();
            String icon = menu.getIcon();
            String remark = menu.getRemark();

            entity.setName(name);
            entity.setDisplay(display);
            entity.setParentId(parentId);
            entity.setOrderNum(orderNum);
            entity.setUrl(url);
            entity.setFrame(isFrame);
            entity.setType(menuType);
            entity.setVisible(visible);
            entity.setStatus(status);
            entity.setPermission(permission);
            entity.setIcon(icon);
            entity.setRemark(remark);

            menuService.update(entity);
            return R.ok();
        }
    }

    /**
     * 删除菜单权限
     */
    @SysLog("批量删除菜单")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public R remove(@RequestParam("ids") String ids) {
        menuService.deleteByIds(ids);
        return R.ok();
    }

    public static class SearchForm extends BaseSearchForm {
        private String name;
        private String status;
        private Boolean visible;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Boolean isVisible() {
            return visible;
        }

        public void setVisible(Boolean visible) {
            this.visible = visible;
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
