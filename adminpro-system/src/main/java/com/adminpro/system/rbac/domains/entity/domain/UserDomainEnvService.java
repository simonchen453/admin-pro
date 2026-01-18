package com.adminpro.system.rbac.domains.entity.domain;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDomainEnvService extends BaseService<UserDomainEnvEntity, String> {

    private final UserDomainEnvDao dao;

    public UserDomainEnvService(UserDomainEnvDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static UserDomainEnvService getInstance() {
        return SpringUtil.getBean(UserDomainEnvService.class);
    }

    public QueryResultSet<UserDomainEnvEntity> search(SearchParam param) {
        return dao.search(param);
    }

    @Transactional
    @CacheEvict(value = RbacCacheConstants.USER_DOMAIN_ENV_CACHE, key = "'userdomainenv_'+#entity.getUserDomain()")
    public void save(UserDomainEnvEntity entity) {
        if (StringUtils.isNotEmpty(entity.getId())) {
            dao.update(entity);
        } else {
            entity.setId(IdGenerator.getInstance().nextStringId());
            dao.create(entity);
        }
    }

    @CacheEvict(value = RbacCacheConstants.USER_DOMAIN_ENV_CACHE, key = "'userdomainenv_'+#entity.getUserDomain()")
    public void delete(UserDomainEnvEntity entity) {
        dao.delete(entity.getId());
    }

    @Cacheable(value = RbacCacheConstants.USER_DOMAIN_ENV_CACHE, key = "'userdomainenv_'+#userDomain")
    public UserDomainEnvEntity findByUserDomain(String userDomain) {
        return dao.findByUserDomain(userDomain);
    }

    @CacheEvict(value = RbacCacheConstants.USER_DOMAIN_ENV_CACHE, key = "'userdomainenv_'+#entity.userDomain")
    @Override
    public void create(UserDomainEnvEntity entity) {
        entity.setId(IdGenerator.getInstance().nextStringId());
        super.create(entity);
    }

    @CacheEvict(value = RbacCacheConstants.USER_DOMAIN_ENV_CACHE, key = "'userdomainenv_'+#entity.userDomain")
    @Override
    public void update(UserDomainEnvEntity entity) {
        super.update(entity);
    }

    @CacheEvict(value = RbacCacheConstants.USER_DOMAIN_ENV_CACHE, allEntries = true)
    @Override
    public void delete(String id) {
        super.delete(id);
    }

    @Transactional
    public void deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }

        String[] split = ids.split(",");
        for (int i = 0; i < split.length; i++) {
            String id = split[i];
            UserDomainEnvEntity entity = findById(id);
            delete(entity);
        }
    }
}
