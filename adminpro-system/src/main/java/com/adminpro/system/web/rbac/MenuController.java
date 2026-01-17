package com.adminpro.system.web.rbac;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.rbac.domains.entity.menu.MenuCreateValidator;
import com.adminpro.system.rbac.domains.entity.menu.MenuEntity;
import com.adminpro.system.rbac.domains.entity.menu.MenuService;
import com.adminpro.system.rbac.domains.entity.menu.MenuUpdateValidator;
import com.adminpro.system.rbac.domains.vo.menu.MenuTreeVo;
import com.adminpro.system.rbac.enums.MenuType;
import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.rbac.api.LoginHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单权限管理控制器
 * <p>
 * 提供菜单权限的增删改查功能，包括菜单列表查询、菜单详情查询、菜单创建、菜单更新、菜单删除等操作
 * </p>
 *
 * @author simon
 * @date 2020-05-21
 */
@Tag(name = "菜单权限管理", description = "菜单权限的增删改查接口")
@RestController
@RequestMapping(MenuController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:menu')")
public class MenuController extends BaseController {
    protected static final String PREFIX_URL = "/api/v1/menus";
    protected static final String SEARCH_FORM_KEY = "menuSearchForm";

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuCreateValidator menuCreateValidator;

    @Autowired
    private MenuUpdateValidator menuUpdateValidator;

    /**
     * 查询菜单权限列表
     * <p>
     * 根据查询条件获取菜单权限列表，支持按名称、状态、可见性等条件进行过滤和分页查询
     * </p>
     *
     * @param searchForm 查询条件表单，包含菜单名称、状态、可见性等过滤条件
     * @return 菜单权限列表
     */
    @Operation(summary = "查询菜单权限列表", description = "根据查询条件获取菜单权限列表，支持按名称、状态、可见性等条件进行过滤和分页查询")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @PostMapping(value = "/search")
    public R<List<MenuEntity>> list(
            @Parameter(description = "查询条件表单", required = true) @RequestBody SearchForm searchForm) {
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
     * 获取当前用户的菜单列表
     * <p>
     * 根据当前登录用户获取其可访问的菜单树形结构
     * </p>
     *
     * @return 当前用户的菜单树列表
     */
    @Operation(summary = "获取当前用户菜单", description = "根据当前登录用户获取其可访问的菜单树形结构")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未授权")
    })
    @GetMapping(value = "/current-user")
    @PreAuthorize("isAuthenticated()")
    public R<List<MenuTreeVo>> getCurrentUserMenus() {
        try {
            LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
            if (loginUser == null) {
                return R.error("401", "用户未登录");
            }
            String userId = loginUser.getUserId();
            String userDomain = loginUser.getUserDomain();
            List<MenuEntity> menuTree = menuService.findMenuTreeByUserId(userId, userDomain);
            List<MenuTreeVo> menus = menuService.buildMenus(menuTree);
            return R.ok(menus);
        } catch (Exception e) {
            logger.error("获取当前用户菜单失败：", e);
            return R.error("获取菜单失败: " + e.getMessage());
        }
    }

    /**
     * 获取菜单树形结构
     * <p>
     * 获取所有菜单的树形结构数据，用于角色管理分配菜单权限
     * </p>
     *
     * @return 菜单树形结构列表
     */
    @Operation(summary = "获取菜单树", description = "获取所有菜单的树形结构数据，用于角色管理分配菜单权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未授权")
    })
    @GetMapping(value = "/tree")
    @PreAuthorize("isAuthenticated()")
    public R<List<com.adminpro.system.rbac.domains.vo.tree.TreeSelect>> getMenuTree() {
        List<MenuEntity> menus = menuService.findAll();
        List<MenuEntity> menuTree = menuService.buildMenuTree(menus);
        List<com.adminpro.system.rbac.domains.vo.tree.TreeSelect> treeSelect = menuTree.stream()
                .map(com.adminpro.system.rbac.domains.vo.tree.TreeSelect::new)
                .collect(java.util.stream.Collectors.toList());
        return R.ok(treeSelect);
    }

    /**
     * 根据角色ID获取菜单树形结构
     * <p>
     * 获取所有菜单的树形结构数据，以及指定角色已选中的菜单ID列表
     * </p>
     *
     * @param roleId 角色ID
     * @return 包含菜单树和已选中菜单ID的映射
     */
    @Operation(summary = "根据角色获取菜单树", description = "获取菜单树形结构及角色已选中的菜单ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未授权")
    })
    @GetMapping(value = "/tree/role/{roleId}")
    @PreAuthorize("isAuthenticated()")
    public R<java.util.Map<String, Object>> getMenuTreeByRoleId(@PathVariable String roleId) {
        List<MenuEntity> menus = menuService.findAll();
        List<MenuEntity> menuTree = menuService.buildMenuTree(menus);
        List<com.adminpro.system.rbac.domains.vo.tree.TreeSelect> treeSelect = menuTree.stream()
                .map(com.adminpro.system.rbac.domains.vo.tree.TreeSelect::new)
                .collect(java.util.stream.Collectors.toList());

        // 获取角色已选中的菜单ID - 从角色菜单关联表中查询
        List<com.adminpro.system.rbac.domains.entity.rolemenu.RoleMenuAssignEntity> roleMenus = com.adminpro.system.rbac.domains.entity.rolemenu.RoleMenuAssignService
                .getInstance().findByRoleId(roleId);
        List<String> checkedKeys = roleMenus.stream()
                .map(com.adminpro.system.rbac.domains.entity.rolemenu.RoleMenuAssignEntity::getMenuId)
                .collect(java.util.stream.Collectors.toList());

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("menus", treeSelect);
        result.put("checkedKeys", checkedKeys);
        return R.ok(result);
    }

    /**
     * 查询菜单详情
     * <p>
     * 根据菜单ID获取菜单的详细信息
     * </p>
     *
     * @param id 菜单ID
     * @return 菜单详细信息
     */
    @Operation(summary = "查询菜单详情", description = "根据菜单ID获取菜单的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "菜单不存在")
    })
    @GetMapping(value = "/{id}")
    public R<MenuEntity> detail(@Parameter(description = "菜单ID", required = true) @PathVariable String id) {
        MenuEntity entity = menuService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 删除单个菜单
     * <p>
     * 根据菜单ID删除指定的菜单权限
     * </p>
     *
     * @param id 菜单ID
     * @return 被删除的菜单信息
     */
    @Operation(summary = "删除单个菜单", description = "根据菜单ID删除指定的菜单权限")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @SysLog("删除菜单")
    @DeleteMapping(value = "/{id}")
    public R<MenuEntity> delete(@Parameter(description = "菜单ID", required = true) @PathVariable String id) {
        MenuEntity entity = menuService.findById(id);
        if (entity != null) {
            menuService.delete(entity.getId());
        }

        return R.ok(entity);
    }

    /**
     * 创建菜单权限
     * <p>
     * 新增一个菜单权限，支持目录、菜单、按钮等多种类型的菜单
     * </p>
     *
     * @param menu 菜单实体信息
     * @return 操作结果
     */
    @Operation(summary = "创建菜单权限", description = "新增一个菜单权限，支持目录、菜单、按钮等多种类型的菜单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @SysLog("创建菜单")
    @PostMapping
    public R create(@Parameter(description = "菜单实体信息", required = true) @RequestBody MenuEntity menu) {
        BeanUtil.beanAttributeValueTrim(menu);

        if (MenuType.isButton(menu.getType()) && StringUtils.isEmpty(menu.getVisible())) {
            menu.setVisible("0");
        }

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
     * 更新菜单权限
     * <p>
     * 更新已有菜单权限的信息
     * </p>
     *
     * @param menu 菜单实体信息
     * @return 操作结果
     */
    @Operation(summary = "更新菜单权限", description = "更新已有菜单权限的信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "菜单不存在")
    })
    @SysLog("更新菜单")
    @PutMapping(value = "/{id}")
    public R editSave(@Parameter(description = "菜单实体信息", required = true) @RequestBody MenuEntity menu) {
        BeanUtil.beanAttributeValueTrim(menu);

        if (MenuType.isButton(menu.getType()) && StringUtils.isEmpty(menu.getVisible())) {
            menu.setVisible("0");
        }

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
     * 批量删除菜单权限
     * <p>
     * 根据多个菜单ID批量删除菜单权限，ID之间用逗号分隔
     * </p>
     *
     * @param ids 菜单ID列表，多个ID用逗号分隔
     * @return 操作结果
     */
    @Operation(summary = "批量删除菜单权限", description = "根据多个菜单ID批量删除菜单权限，ID之间用逗号分隔")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @SysLog("批量删除菜单")
    @DeleteMapping
    public R remove(
            @Parameter(description = "菜单ID列表，多个ID用逗号分隔", required = true, example = "1,2,3") @RequestParam("ids") String ids) {
        // 使用验证工具类解析和验证参数
        List<String> menuIdList = BatchOperationValidator.validateAndParseIds(ids);
        menuService.deleteByIds(StringUtils.join(menuIdList, ","));
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

        @Override
        public String toString() {
            return "SearchForm{" +
                    "name='" + name + '\'' +
                    ", status='" + status + '\'' +
                    ", visible=" + visible +
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
