package com.adminpro.system.rbac.domains.entity.role;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import com.adminpro.system.rbac.domains.entity.rolemenu.RoleMenuAssignEntity;
import com.adminpro.system.rbac.domains.entity.rolemenu.RoleMenuAssignService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 角色服务类
 * <p>
 * 提供角色管理的核心业务功能，包括：
 * <ul>
 * <li>角色基本操作：创建、更新、删除、查询</li>
 * <li>角色菜单关系管理：维护角色与菜单的关联关系</li>
 * <li>角色查询：支持多种条件查询（名称、显示名等）</li>
 * <li>批量操作：批量删除角色</li>
 * <li>缓存管理：使用Spring Cache缓存角色数据</li>
 * </ul>
 * </p>
 * <p>
 * 角色菜单关联：
 * <ul>
 * <li>创建角色时自动关联菜单</li>
 * <li>更新角色时重建菜单关联关系</li>
 * </ul>
 * </p>
 *
 * @author system
 * @version 1.0
 * @see RoleEntity
 * @see RoleDao
 * @see RoleMenuAssignService
 */
@Service
public class RoleService extends BaseService<RoleEntity, String> {

    private RoleDao dao;

    @Autowired
    private RoleMenuAssignService roleMenuAssignService;

    @Autowired
    protected RoleService(RoleDao dao) {
        super(dao);
        this.dao = dao;
    }

    /**
     * 根据角色名称查询角色
     * <p>
     * 支持缓存，使用角色名称作为缓存键
     * </p>
     *
     * @param name 角色名称
     * @return 角色实体对象，不存在返回null
     */
    @Cacheable(value = RbacCacheConstants.ROLE_CACHE, key = "'name_'+#name")
    public RoleEntity findByName(String name) {
        return dao.findByName(name);
    }

    /**
     * 根据角色显示名查询角色
     *
     * @param display 角色显示名
     * @return 角色实体对象，不存在返回null
     */
    public RoleEntity findByDisplay(String display) {
        return dao.findByDisplay(display);
    }

    /**
     * 根据角色名称模糊查询角色列表
     *
     * @param name 角色名称（支持模糊匹配）
     * @return 角色实体列表
     */
    public List<RoleEntity> findLikeName(String name) {
        return dao.findByNameIsLike(name);
    }

    /**
     * 根据角色名称列表批量查询
     *
     * @param names 角色名称列表
     * @return 角色列表
     */
    public List<RoleEntity> findByNames(List<String> names) {
        return dao.findByNames(names);
    }

    /**
     * 根据ID列表批量查询
     *
     * @param ids 角色ID列表
     * @return 角色列表
     */
    public List<RoleEntity> findByIds(List<String> ids) {
        return dao.findByIds(ids);
    }

    /**
     * 批量删除角色
     * <p>
     * 根据角色ID字符串批量删除角色，使用批量SQL提升性能。
     * 注意：此操作仅删除角色本身，不会删除角色菜单关联关系。
     * </p>
     *
     * @param roleIds 角色ID字符串，格式：roleId,roleId,roleId
     */
    @Transactional
    public void deleteByIds(String roleIds) {
        if (StringUtils.isEmpty(roleIds)) {
            return;
        }
        String[] idArray = StringUtils.split(roleIds, ",");
        if (idArray.length > 0) {
            dao.deleteByIds(Arrays.asList(idArray));
            logger.info("批量删除角色成功: count={}", idArray.length);
        }
    }

    /**
     * 获取RoleService实例
     * <p>
     * 通过Spring容器获取Service实例，用于在非Spring管理的类中调用服务
     * </p>
     *
     * @return RoleService实例
     */
    public static RoleService getInstance() {
        return SpringUtil.getBean(RoleService.class);
    }

    /**
     * 搜索角色（分页）
     * <p>
     * 根据搜索参数进行分页查询，支持多种条件过滤
     * </p>
     *
     * @param param 搜索参数对象，包含分页信息和过滤条件
     * @return 分页查询结果集
     */
    public QueryResultSet<RoleEntity> search(SearchParam param) {
        return dao.search(param);
    }

    /**
     * 根据参数查询角色列表
     * <p>
     * 根据搜索参数查询符合条件的角色列表，不分页
     * </p>
     *
     * @param param 搜索参数对象，包含过滤条件
     * @return 角色实体列表
     */
    public List<RoleEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    /**
     * 查询未分配的角色（分页）
     * <p>
     * 查询尚未分配给特定用户或组织的角色列表
     * </p>
     *
     * @param param 搜索参数对象，包含分页信息和过滤条件
     * @return 分页查询结果集
     */
    public QueryResultSet<RoleEntity> unAssignedRole(SearchParam param) {
        return dao.unAssignedRole(param);
    }

    /**
     * 创建角色
     * <p>
     * 创建新角色，并自动关联菜单。
     * 如果角色对象中包含菜单ID列表，会自动创建角色菜单关联关系。
     * </p>
     *
     * @param entity 角色实体对象，包含角色信息和菜单ID列表
     */
    @Transactional
    public void create(RoleEntity entity) {
        dao.create(entity);
        List<String> menuIds = entity.getMenuIds();
        if (menuIds != null) {
            for (int i = 0; i < menuIds.size(); i++) {
                String menuId = menuIds.get(i);
                RoleMenuAssignEntity assignEntity = new RoleMenuAssignEntity();
                assignEntity.setMenuId(menuId);
                assignEntity.setRoleId(entity.getId());
                RoleMenuAssignService.getInstance().create(assignEntity);
            }
        }
    }

    /**
     * 更新角色
     * <p>
     * 更新角色信息和菜单关联关系。
     * 处理流程：
     * <ul>
     * <li>删除角色原有的所有菜单关联</li>
     * <li>根据新的菜单ID列表重建菜单关联关系</li>
     * <li>更新角色基本信息</li>
     * </ul>
     * </p>
     *
     * @param entity 角色实体对象，包含更新后的角色信息和菜单ID列表
     */
    @Transactional
    public void update(RoleEntity entity) {
        List<String> menuIds = entity.getMenuIds();
        roleMenuAssignService.deleteByRoleId(entity.getId());
        if (menuIds != null) {
            for (int i = 0; i < menuIds.size(); i++) {
                String menuId = menuIds.get(i);
                RoleMenuAssignEntity assignEntity = new RoleMenuAssignEntity();
                assignEntity.setMenuId(menuId);
                assignEntity.setRoleId(entity.getId());
                roleMenuAssignService.create(assignEntity);
            }
        }
        dao.update(entity);
    }
}
