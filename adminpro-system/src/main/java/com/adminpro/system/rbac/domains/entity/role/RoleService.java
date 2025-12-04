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

import java.util.List;

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

    @Cacheable(value = RbacCacheConstants.ROLE_CACHE, key = "'name_'+#name")
    public RoleEntity findByName(String name) {
        return dao.findByName(name);
    }

    public RoleEntity findByDisplay(String display) {
        return dao.findByDisplay(display);
    }

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

    @Transactional
    public void deleteMany(String roleIds) {
        String[] idArray = StringUtils.split(roleIds, ",");
        for (String id : idArray) {
            delete(id);
        }
    }

    public static RoleService getInstance() {
        return SpringUtil.getBean(RoleService.class);
    }

    public QueryResultSet<RoleEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<RoleEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }


    public QueryResultSet<RoleEntity> unAssignedRole(SearchParam param) {
        return dao.unAssignedRole(param);
    }

    /**
     * 创建 RoleEntity
     *
     * @param entity
     */
    @Transactional
    public void create(RoleEntity entity) {
        List<String> menuNames = entity.getMenuNames();
        for (int i = 0; i < menuNames.size(); i++) {
            String menuName = menuNames.get(i);
            RoleMenuAssignEntity assignEntity = new RoleMenuAssignEntity();
            assignEntity.setMenuName(menuName);
            assignEntity.setRoleName(entity.getName());
            RoleMenuAssignService.getInstance().create(assignEntity);
        }
        dao.create(entity);
    }

    /**
     * 更新 RoleEntity
     *
     * @param entity
     */
    @Transactional
    public void update(RoleEntity entity) {
        List<String> menuNames = entity.getMenuNames();
        roleMenuAssignService.deleteByRoleName(entity.getName());
        for (int i = 0; i < menuNames.size(); i++) {
            String menuName = menuNames.get(i);
            RoleMenuAssignEntity assignEntity = new RoleMenuAssignEntity();
            assignEntity.setMenuName(menuName);
            assignEntity.setRoleName(entity.getName());
            roleMenuAssignService.create(assignEntity);
        }
        dao.update(entity);
    }
}
