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
 * 菜单权限 服务层实现
 *
 * @author simon
 * @date 2020-05-21
 */
@Service
public class MenuService extends BaseService<MenuEntity, String> {

    private MenuDao dao;

    @Autowired
    private UserDomainEnvService userDomainEnvService;

    @Autowired
    public MenuService(MenuDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static MenuService getInstance() {
        return SpringUtil.getBean(MenuService.class);
    }

    public QueryResultSet<MenuEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<MenuEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    public MenuEntity findByName(String name) {
        return dao.findByName(name);
    }

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
     * 批量删除菜单（优化：使用批量删除SQL提升性能）
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
        return returnList;
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     * @return String
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
     * 递归列表
     *
     * @param list
     * @param t
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
     * 得到子节点列表
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
     */
    private boolean hasChild(List<MenuEntity> list, MenuEntity t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    public List<String> findPermissionByRoleId(String roleId) {
        return dao.findPermissionByRoleId(roleId);
    }

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

    public String getTopParentMenuId(String menuId) {

        return "";
    }
}
