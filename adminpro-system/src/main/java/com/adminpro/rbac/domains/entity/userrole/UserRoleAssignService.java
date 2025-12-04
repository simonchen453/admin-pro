package com.adminpro.rbac.domains.entity.userrole;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.rbac.domains.entity.user.UserIden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRoleAssignService extends BaseService<UserRoleAssignEntity, String> {

    private UserRoleAssignDao dao;

    @Autowired
    protected UserRoleAssignService(UserRoleAssignDao dao) {
        super(dao);
        this.dao = dao;
    }

    @Transactional
    @CacheEvict(value = {RbacCacheConstants.PROCESS_RESOURCE_CACHE, RbacCacheConstants.MENU_CACHE}, allEntries = true)
    public void create(UserRoleAssignEntity entity) {
        entity.setId(IdGenerator.getInstance().nextStringId());
        dao.create(entity);
    }

    @Transactional
    @CacheEvict(value = {RbacCacheConstants.PROCESS_RESOURCE_CACHE, RbacCacheConstants.MENU_CACHE}, allEntries = true)
    public void update(UserRoleAssignEntity entity) {
        dao.update(entity);
    }

    public UserRoleAssignEntity findByUserDomainAndUserIdAndRoleId(String userDomain, String userId, String roleId) {
        return dao.findByUserDomainAndUserIdAndRoleId(userDomain, userId, roleId);
    }

    public List<UserRoleAssignEntity> findByUserIden(UserIden userIden) {
        return dao.findByUserDomainAndUserId(userIden.getUserDomain(), userIden.getUserId());
    }

    @Transactional
    public void deleteByUserIden(UserIden userIden) {
        dao.deleteByUserIden(userIden);
    }
}
