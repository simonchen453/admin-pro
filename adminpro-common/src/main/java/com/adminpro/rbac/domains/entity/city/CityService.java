package com.adminpro.rbac.domains.entity.city;


import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.cache.AppCache;
import com.adminpro.rbac.common.RbacCacheConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService extends BaseService<CityEntity, String> {

    private CityDao dao;

    @Autowired
    protected CityService(CityDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static CityService getInstance() {
        return SpringUtil.getBean(CityService.class);
    }

    public List<CityEntity> findByLevel(Integer level) {
        List list = AppCache.getInstance().get(RbacCacheConstants.CITY_CACHE_LEVEL, String.valueOf(level), List.class);
        if (list != null) {
            return (List<CityEntity>) list;
        } else {
            List<CityEntity> byLevel = dao.findByLevel(level);
            if (byLevel != null) {
                AppCache.getInstance().set(RbacCacheConstants.CITY_CACHE_LEVEL, String.valueOf(level), byLevel);
            }
            return byLevel;
        }
    }

    public List<CityEntity> findByLevelAndParent(Integer level, String parent) {
        List list = AppCache.getInstance().get(RbacCacheConstants.CITY_CACHE_LEVEL_PARENT, String.valueOf(level) + "_" + String.valueOf(parent), List.class);
        if (list != null) {
            return (List<CityEntity>) list;
        } else {
            List<CityEntity> byLevel = dao.findByLevelAndParent(level, parent);
            if (byLevel != null) {
                AppCache.getInstance().set(RbacCacheConstants.CITY_CACHE_LEVEL_PARENT, String.valueOf(level) + "_" + String.valueOf(parent), byLevel);
            }
            return byLevel;
        }
    }

    public CityEntity findCityById(String id) {
        CityEntity entity = AppCache.getInstance().get(RbacCacheConstants.CITY_CACHE_ID, id, CityEntity.class);
        if (entity != null) {
            return entity;
        } else {
            CityEntity cityEntity = dao.findById(id);
            if (cityEntity != null) {
                AppCache.getInstance().set(RbacCacheConstants.CITY_CACHE_ID, id, cityEntity);
            }
            return cityEntity;
        }
    }

    public CityEntity findCityByTitle(String title) {
        List<CityEntity> entities = AppCache.getInstance().get(RbacCacheConstants.CITY_CACHE_TITLE, title, List.class);
        if (entities == null) {
            entities = dao.findByTitle(title);
            if (entities != null) {
                AppCache.getInstance().set(RbacCacheConstants.CITY_CACHE_TITLE, title, entities);
            }
        }

        if (entities != null) {
            for (int i = 0; i < entities.size(); i++) {
                CityEntity cityEntity = entities.get(i);
                return cityEntity;
            }
        }

        return null;
    }

    public String findCityNameById(String id) {
        CityEntity cityByid = findCityById(id);
        if (cityByid != null) {
            return cityByid.getTitle();
        } else {
            return null;
        }
    }

    public CityEntity findDistrictByTitle(String title, CityEntity parent) {
        List<CityEntity> entities = AppCache.getInstance().get(RbacCacheConstants.CITY_CACHE_TITLE, title, List.class);
        if (entities == null) {
            entities = dao.findByTitle(title);
            if (entities != null) {
                AppCache.getInstance().set(RbacCacheConstants.CITY_CACHE_TITLE, title, entities);
            }
        }

        if (entities != null) {
            for (int i = 0; i < entities.size(); i++) {
                CityEntity cityEntity = entities.get(i);
                if (cityEntity.getLevel() == CityEntity.DISTRICT_LEVEL && StringUtils.equals(cityEntity.getParent(), parent.getId())) {
                    return cityEntity;
                }
            }
        }

        return null;
    }

    public QueryResultSet<CityEntity> search(SearchParam param) {
        return dao.search(param);
    }

}
