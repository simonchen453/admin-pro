package com.adminpro.system.rbac.domains.entity.menu;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.enums.CommonStatus;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.rbac.common.RbacConstants;
import com.adminpro.system.rbac.domains.entity.domain.UserDomainEnvEntity;
import com.adminpro.system.rbac.domains.entity.domain.UserDomainEnvService;
import com.adminpro.system.rbac.domains.entity.rolemenu.RoleMenuAssignEntity;
import com.adminpro.system.rbac.domains.entity.rolemenu.RoleMenuAssignService;
import com.adminpro.system.rbac.domains.vo.menu.MenuTreeVo;
import com.adminpro.system.rbac.domains.vo.tree.TreeSelect;
import com.adminpro.system.rbac.enums.MenuDisplay;
import com.adminpro.system.rbac.enums.MenuType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单权限服务类
 * <p>
 * 提供菜单权限管理的核心业务功能，包括：
 * <ul>
 * <li>菜单基本操作：创建、更新、删除、查询</li>
 * <li>菜单树构建：构建菜单树形结构供前端展示</li>
 * <li>用户菜单查询：根据用户角色查询可访问的菜单列表</li>
 * <li>权限查询：查询角色的权限标识列表</li>
 * <li>菜单路由构建：构建前端路由所需的菜单结构</li>
 * </ul>
 * </p>
 * <p>
 * 菜单类型：
 * <ul>
 * <li>目录：菜单分类，不对应具体页面</li>
 * <li>菜单：具体的功能菜单项</li>
 * <li>按钮：页面内的操作按钮权限</li>
 * </ul>
 * </p>
 *
 * @author simon
 * @date 2020-05-21
 * @version 1.0
 * @see MenuEntity
 * @see MenuDao
 */
@Service
public class MenuService extends BaseService<MenuEntity, String> {

    private final MenuDao dao;

    @Autowired
    private UserDomainEnvService userDomainEnvService;

    public MenuService(MenuDao dao) {
        super(dao);
        this.dao = dao;
    }

    /**
     * 获取MenuService实例
     * <p>
     * 通过Spring容器获取Service实例，用于在非Spring管理的类中调用服务
     * </p>
     *
     * @return MenuService实例
     */
    public static MenuService getInstance() {
        return SpringUtil.getBean(MenuService.class);
    }

    /**
     * 搜索菜单（分页）
     * <p>
     * 根据搜索参数进行分页查询，支持多种条件过滤
     * </p>
     *
     * @param param 搜索参数对象，包含分页信息和过滤条件
     * @return 分页查询结果集
     */
    public QueryResultSet<MenuEntity> search(SearchParam param) {
        return dao.search(param);
    }

    /**
     * 根据参数查询菜单列表
     * <p>
     * 根据搜索参数查询符合条件的菜单列表，不分页
     * </p>
     *
     * @param param 搜索参数对象，包含过滤条件
     * @return 菜单实体列表
     */
    public List<MenuEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    /**
     * 根据菜单名称查询菜单
     *
     * @param name 菜单名称
     * @return 菜单实体对象，不存在返回null
     */
    public MenuEntity findByName(String name) {
        return dao.findByName(name);
    }

    /**
     * 删除菜单
     * <p>
     * 删除指定菜单及其所有角色菜单关联关系。
     * 注意：不会递归删除子菜单。
     * </p>
     *
     * @param id 菜单ID
     */
    @Override
    @Transactional
    public void delete(String id) {
        MenuEntity menuEntity = findById(id);
        if (menuEntity != null) {
            RoleMenuAssignService roleMenuAssignService = RoleMenuAssignService.getInstance();
            List<RoleMenuAssignEntity> assignEntityList = roleMenuAssignService.findByMenuId(menuEntity.getId());
            roleMenuAssignService.delete(assignEntityList);
        }
        super.delete(id);
    }

    /**
     * 批量删除菜单
     * <p>
     * 根据菜单ID字符串批量删除菜单，使用批量SQL提升性能。
     * 注意：此操作仅删除菜单本身，不会删除角色菜单关联关系。
     * </p>
     *
     * @param ids 菜单ID字符串，格式：id1,id2,id3
     */
    @Transactional
    public void deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }
        String[] idArray = ids.split(",");
        if (idArray.length > 0) {
            dao.deleteByIds(Arrays.asList(idArray));
        }
    }

    /**
     * 构建菜单树选择列表
     * <p>
     * 将菜单列表构建为树形结构，并转换为TreeSelect对象供前端下拉选择使用
     * </p>
     *
     * @param menus 菜单实体列表
     * @return 树形选择对象列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<MenuEntity> menus) {
        List<MenuEntity> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<MenuEntity> buildMenuTree(List<MenuEntity> menus) {
        List<MenuEntity> returnList = new ArrayList<MenuEntity>();
        for (Iterator<MenuEntity> iterator = menus.iterator(); iterator.hasNext();) {
            MenuEntity t = (MenuEntity) iterator.next();
            // 根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (StringHelper.equals(t.getParentId(), "0")) {
                recursionFn(menus, t);
                returnList.add(t);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        // 按orderNum排序，确保菜单按正确顺序显示
        returnList.sort((a, b) -> {
            Integer orderA = a.getOrderNum() != null ? a.getOrderNum() : 0;
            Integer orderB = b.getOrderNum() != null ? b.getOrderNum() : 0;
            return orderA.compareTo(orderB);
        });
        return returnList;
    }

    /**
     * 根据父节点的ID获取所有子节点
     * <p>
     * 从菜单列表中获取指定父节点的所有子节点，并递归构建子节点树
     * </p>
     *
     * @param list     菜单列表
     * @param parentId 父节点ID
     * @return 子菜单树列表
     */
    public List<MenuEntity> getChildPerms(List<MenuEntity> list, String parentId) {
        List<MenuEntity> returnList = new ArrayList<MenuEntity>();
        for (Iterator<MenuEntity> iterator = list.iterator(); iterator.hasNext();) {
            MenuEntity t = (MenuEntity) iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (StringHelper.equals(t.getParentId(), parentId)) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归构建菜单树
     * <p>
     * 递归查找并设置菜单的所有子节点
     * </p>
     *
     * @param list 菜单列表
     * @param t    当前菜单节点
     */
    private void recursionFn(List<MenuEntity> list, MenuEntity t) {
        // 得到子节点列表
        List<MenuEntity> childList = getChildList(list, t);
        t.setChildren(childList);
        for (MenuEntity tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<MenuEntity> it = childList.iterator();
                while (it.hasNext()) {
                    MenuEntity n = (MenuEntity) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 获取子节点列表
     * <p>
     * 从菜单列表中查找指定父节点的所有直接子节点
     * </p>
     *
     * @param list 菜单列表
     * @param t    父菜单节点
     * @return 直接子菜单列表
     */
    private List<MenuEntity> getChildList(List<MenuEntity> list, MenuEntity t) {
        List<MenuEntity> tList = new ArrayList<MenuEntity>();
        Iterator<MenuEntity> it = list.iterator();
        while (it.hasNext()) {
            MenuEntity n = (MenuEntity) it.next();
            if (StringHelper.equals(n.getParentId(), t.getId())) {
                tList.add(n);
            }
        }
        return tList;
    }

    /**
     * 判断是否有子节点
     * <p>
     * 判断指定菜单节点是否存在子节点
     * </p>
     *
     * @param list 菜单列表
     * @param t    菜单节点
     * @return 存在子节点返回true，否则返回false
     */
    private boolean hasChild(List<MenuEntity> list, MenuEntity t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    /**
     * 根据角色ID查询权限标识列表
     * <p>
     * 查询指定角色拥有的所有权限标识（用于权限验证）
     * </p>
     *
     * @param roleId 角色ID
     * @return 权限标识字符串列表
     */
    public List<String> findPermissionByRoleId(String roleId) {
        return dao.findPermissionByRoleId(roleId);
    }

    /**
     * 根据用户ID和用户域查询菜单树
     * <p>
     * 查询指定用户可访问的所有菜单，并构建为树形结构。
     * 包含用户个人角色的菜单和用户域公共角色的菜单。
     * </p>
     *
     * @param userId     用户ID
     * @param userDomain 用户域
     * @return 菜单树列表
     */
    public List<MenuEntity> findMenuTreeByUserId(String userId, String userDomain) {
        UserDomainEnvEntity domainEnvEntity = userDomainEnvService.findByUserDomain(userDomain);
        List<MenuEntity> menus = new ArrayList<>();
        SearchParam param = new SearchParam();
        param.addFilter("userId", userId);
        param.addFilter("status", CommonStatus.ACTIVE.getCode());
        List<MenuEntity> menus1 = dao.findByParam(param);
        if (menus1 != null) {
            menus.addAll(menus1);
        }
        if (domainEnvEntity != null) {
            param = new SearchParam();
            String commonRoleName = domainEnvEntity.getCommonRole();
            if (StringHelper.isNotEmpty(commonRoleName)) {
                com.adminpro.system.rbac.domains.entity.role.RoleEntity roleEntity = com.adminpro.system.rbac.domains.entity.role.RoleService
                        .getInstance().findByName(commonRoleName);
                if (roleEntity != null) {
                    param.addFilter("commonRoleId", roleEntity.getId());
                    List<MenuEntity> menus2 = dao.findByParam(param);
                    if (menus2 != null) {
                        mergeMenuList(menus, menus2);
                    }
                }
            }
        }
        Collections.sort(menus);
        return getChildPerms(menus, "0");
    }

    /**
     * 合并菜单列表
     * <p>
     * 将源菜单列表合并到目标菜单列表，去重处理
     * </p>
     *
     * @param menus 目标菜单列表（会被修改）
     * @param list  源菜单列表
     */
    private void mergeMenuList(List<MenuEntity> menus, List<MenuEntity> list) {
        for (int i = 0; i < list.size(); i++) {
            MenuEntity menuEntity = list.get(i);
            boolean exist = false;
            for (int j = 0; j < menus.size(); j++) {
                MenuEntity entity = menus.get(j);
                if (StringHelper.equals(entity.getId(), menuEntity.getId())) {
                    exist = true;
                }
            }
            if (!exist) {
                menus.add(menuEntity);
            }
        }
    }

    /**
     * 构建前端路由菜单
     * <p>
     * 将菜单实体列表转换为前端路由所需的菜单树结构。
     * 仅包含可见的菜单项（非按钮类型）。
     * </p>
     *
     * @param menus 菜单实体列表
     * @return 菜单树VO列表
     */
    public List<MenuTreeVo> buildMenus(List<MenuEntity> menus) {
        List<MenuTreeVo> routers = new LinkedList<>();
        for (MenuEntity menu : menus) {
            MenuTreeVo treeVo = new MenuTreeVo();
            if (MenuDisplay.isShow(menu.getVisible()) && !MenuType.isButton(menu.getType())) {
                treeVo.setUrl(StringUtils.capitalize(menu.getUrl()) + "?" + RbacConstants.MENU_SESSION_KEY + "="
                        + menu.getId());
                String icon1 = menu.getIcon();
                if (StringUtils.isNotEmpty(icon1) && icon1.startsWith("/")) {
                    icon1 = WebHelper.getContextPath() + icon1;
                }
                treeVo.setIcon(icon1);
                treeVo.setIndex(menu.getId());
                treeVo.setTitle(menu.getDisplay());
                treeVo.setType(menu.getType());
                treeVo.setId(menu.getId());
                List<MenuEntity> cMenus = menu.getChildren();
                if (MenuType.isCategory(menu.getType())) {
                    // 没有子元素的目录就忽略
                    if (cMenus != null && !cMenus.isEmpty()) {
                        List<MenuTreeVo> menuTreeVos = buildMenus(cMenus);
                        if (menuTreeVos != null && !menuTreeVos.isEmpty()) {
                            treeVo.setSubs(menuTreeVos);
                            routers.add(treeVo);
                        }
                    }
                } else {
                    treeVo.setSubs(new ArrayList<>());
                    routers.add(treeVo);
                }
            }
        }
        return routers;
    }

    /**
     * 获取父级菜单ID列表
     * <p>
     * 从菜单树中提取所有目录类型且有子菜单的菜单ID
     * </p>
     *
     * @param menus 菜单实体列表
     * @return 父级菜单ID列表
     */
    public List<String> getParentMenuIds(List<MenuEntity> menus) {
        List<String> routers = new LinkedList<>();
        for (MenuEntity menu : menus) {
            MenuTreeVo treeVo = new MenuTreeVo();
            if (MenuDisplay.isShow(menu.getVisible())) {
                treeVo.setUrl(StringUtils.capitalize(menu.getUrl()));
                String icon1 = menu.getIcon();
                if (StringUtils.isNotEmpty(icon1) && icon1.startsWith("/")) {
                    icon1 = WebHelper.getContextPath() + icon1;
                }
                treeVo.setIcon(icon1);
                treeVo.setIndex(menu.getId());
                treeVo.setTitle(menu.getDisplay());
                treeVo.setType(menu.getType());
                List<MenuEntity> cMenus = menu.getChildren();
                if (cMenus != null && !cMenus.isEmpty() && cMenus.size() > 0 && MenuType.isCategory(menu.getType())) {
                    routers.add(menu.getId());
                }
            }
        }
        return routers;
    }

    /**
     * 获取顶级父菜单ID
     * <p>
     * 根据菜单ID递归查找顶级父菜单ID
     * </p>
     *
     * @param menuId 菜单ID
     * @return 顶级父菜单ID，未找到返回空字符串
     */
    public String getTopParentMenuId(String menuId) {

        return "";
    }
}
