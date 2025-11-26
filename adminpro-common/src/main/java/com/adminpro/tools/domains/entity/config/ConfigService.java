package com.adminpro.tools.domains.entity.config;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.rbac.common.RbacCacheConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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

    @Autowired(required = false)
    private CacheManager cacheManager;

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

    /**
     * 查询所有配置
     * 注意：此方法不使用缓存，因为配置项可能很多，不适合整体缓存
     * 如果需要批量预加载，应该使用 preloadAll() 方法
     *
     * @return 所有配置列表
     */
    public List<ConfigEntity> findAll() {
        return dao.findAll();
    }

    /**
     * 批量预加载所有配置到缓存
     * 一次性查询所有配置，然后直接放入缓存，避免重复查询数据库
     *
     * @return 成功加载到缓存的配置数量
     */
    public int preloadAll() {
        try {
            List<ConfigEntity> allConfigs = dao.findAll();
            if (allConfigs != null && !allConfigs.isEmpty()) {
                // 如果 CacheManager 可用，直接手动放入缓存，避免重复查询
                if (cacheManager != null) {
                    Cache cache = cacheManager.getCache(RbacCacheConstants.CONFIG_CACHE);
                    if (cache != null) {
                        int loadedCount = 0;
                        for (ConfigEntity config : allConfigs) {
                            if (config != null && config.getKey() != null) {
                                // 使用与 @Cacheable 注解相同的 key 格式
                                String cacheKey = "key_" + config.getKey();
                                cache.put(cacheKey, config);
                                loadedCount++;
                            }
                        }
                        logger.debug("通过 CacheManager 预加载了 {} 个配置到缓存", loadedCount);
                        return loadedCount;
                    } else {
                        // 如果缓存不存在，回退到通过 findByKey 触发缓存
                        logger.debug("缓存 {} 不存在，使用 findByKey 方式预加载", RbacCacheConstants.CONFIG_CACHE);
                        return preloadByFindByKey(allConfigs);
                    }
                } else {
                    // 如果 CacheManager 不可用，回退到通过 findByKey 触发缓存
                    logger.debug("CacheManager 不可用，使用 findByKey 方式预加载");
                    return preloadByFindByKey(allConfigs);
                }
            }
            return 0;
        } catch (Exception e) {
            logger.warn("批量预加载配置失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 通过 findByKey 方法触发缓存（备用方案）
     *
     * @param allConfigs 所有配置列表
     * @return 成功加载的配置数量
     */
    private int preloadByFindByKey(List<ConfigEntity> allConfigs) {
        int loadedCount = 0;
        for (ConfigEntity config : allConfigs) {
            if (config != null && config.getKey() != null) {
                try {
                    // 通过 findByKey 触发缓存，使用缓存注解的 key 格式
                    findByKey(config.getKey());
                    loadedCount++;
                } catch (Exception e) {
                    logger.debug("预加载配置 {} 失败: {}", config.getKey(), e.getMessage());
                }
            }
        }
        return loadedCount;
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
