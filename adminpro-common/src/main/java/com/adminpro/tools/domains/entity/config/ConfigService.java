package com.adminpro.tools.domains.entity.config;

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

import java.util.List;

/**
 * 参数配置 服务层实现
 *
 * @author simon
 * @date 2020-06-15
 */
@Service
public class ConfigService extends BaseService<ConfigEntity, String> {

    private ConfigDao dao;

    @Autowired
    public ConfigService(ConfigDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static ConfigService getInstance() {
        return SpringUtil.getBean(ConfigService.class);
    }

    public QueryResultSet<ConfigEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<ConfigEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    @Cacheable(value = RbacCacheConstants.CONFIG_CACHE, key = "'key_'+#key")
    public ConfigEntity findByKey(String key) {
        try {
            return dao.findByKey(key);
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    @CacheEvict(value = RbacCacheConstants.CONFIG_CACHE, allEntries = true)
    public void create(ConfigEntity entity) {
        super.create(entity);
    }

    @Override
    @CacheEvict(value = RbacCacheConstants.CONFIG_CACHE, key = "'key_' + #entity.key")
    public void update(ConfigEntity entity) {
        super.update(entity);
    }

    @Override
    @CacheEvict(value = RbacCacheConstants.CONFIG_CACHE, allEntries = true)
    public void delete(String id) {
        super.delete(id);
    }

    @Transactional
    @CacheEvict(value = RbacCacheConstants.CONFIG_CACHE, allEntries = true)
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
