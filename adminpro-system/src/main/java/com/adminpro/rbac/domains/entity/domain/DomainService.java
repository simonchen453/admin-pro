package com.adminpro.rbac.domains.entity.domain;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.rbac.common.RbacCacheConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DomainService extends BaseService<DomainEntity, String> {

    private DomainDao dao;

    @Autowired
    protected DomainService(DomainDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static DomainService getInstance() {
        return SpringUtil.getBean(DomainService.class);
    }

    public QueryResultSet<DomainEntity> search(SearchParam param) {
        return dao.search(param);
    }

    /**
     * 根据查询参数获取所有的记录
     *
     * @param param
     * @return
     */
    public List<DomainEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    public DomainEntity findByName(String name) {
        return dao.findByName(name);
    }

    public DomainEntity findByDisplay(String display) {
        return dao.findByDisplay(display);
    }

    @CacheEvict(value = RbacCacheConstants.DOMAIN_CACHE, key = "'domains'")
    public void create(DomainEntity entity) {
        super.create(entity);
    }

    @CacheEvict(value = RbacCacheConstants.DOMAIN_CACHE, key = "'domains'")
    public void update(DomainEntity entity) {
        super.update(entity);
    }

    @CacheEvict(value = RbacCacheConstants.DOMAIN_CACHE, key = "'domains'")
    public void delete(DomainEntity entity) {
        super.delete(entity.getId());
    }

    @Cacheable(value = RbacCacheConstants.DOMAIN_CACHE, key = "'domains'")
    public List<DomainEntity> findAll() {
        return dao.findAll();
    }

    @Cacheable(value = RbacCacheConstants.DOMAIN_CACHE, key = "'domains_map'")
    public Map<String, String> findMap() {
        List<DomainEntity> list = dao.findAll();
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            DomainEntity domainEntity = list.get(i);
            map.put(domainEntity.getName(), domainEntity.getDisplay());
        }
        return map;
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
}
