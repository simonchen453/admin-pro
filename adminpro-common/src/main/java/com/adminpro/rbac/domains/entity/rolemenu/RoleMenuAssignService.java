package com.adminpro.rbac.domains.entity.rolemenu;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色和菜单关联 服务层实现
 *
 * @author simon
 * @date 2020-05-24
 */
@Service
public class RoleMenuAssignService extends BaseService<RoleMenuAssignEntity, String> {

    private RoleMenuAssignDao dao;

    @Autowired
    public RoleMenuAssignService(RoleMenuAssignDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static RoleMenuAssignService getInstance() {
        return SpringUtil.getBean(RoleMenuAssignService.class);
    }

    public QueryResultSet<RoleMenuAssignEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<RoleMenuAssignEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    @Transactional
    public void deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }
        String[] split = ids.split(",");
        for (int i = 0; i < split.length; i++) {
            dao.delete(split[i]);
        }
    }

    @Transactional
    public void delete(List<RoleMenuAssignEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            RoleMenuAssignEntity assignEntity = list.get(i);
            dao.delete(assignEntity.getId());
        }
    }

    public List<RoleMenuAssignEntity> findByRoleName(String roleName) {
        return dao.findByRoleName(roleName);
    }

    public List<RoleMenuAssignEntity> findByMenuName(String menuName) {
        return dao.findByMenuName(menuName);
    }

    public void deleteByRoleName(String roleName) {
        dao.deleteByRoleName(roleName);
    }
}
