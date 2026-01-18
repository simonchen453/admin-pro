package com.adminpro.system.rbac.domains.entity.userrole;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRoleAssignService extends BaseService<UserRoleAssignEntity, String> {

    private final UserRoleAssignDao dao;

    protected UserRoleAssignService(UserRoleAssignDao dao) {
        super(dao);
        this.dao = dao;
    }

    @Transactional
    @CacheEvict(value = { RbacCacheConstants.PROCESS_RESOURCE_CACHE, RbacCacheConstants.MENU_CACHE }, allEntries = true)
    public void create(UserRoleAssignEntity entity) {
        entity.setId(IdGenerator.getInstance().nextStringId());
        dao.create(entity);
    }

    @Transactional
    @CacheEvict(value = { RbacCacheConstants.PROCESS_RESOURCE_CACHE, RbacCacheConstants.MENU_CACHE }, allEntries = true)
    public void update(UserRoleAssignEntity entity) {
        dao.update(entity);
    }

    public UserRoleAssignEntity findByUserIdAndRoleId(String userId, String roleId) {
        return dao.findByUserIdAndRoleId(userId, roleId);
    }

    public List<UserRoleAssignEntity> findByUserId(String userId) {
        return dao.findByUserId(userId);
    }

    @Transactional
    public void deleteByUserId(String userId) {
        dao.deleteByUserId(userId);
    }
}
