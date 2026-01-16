package com.adminpro.system.tools.domains.entity.dict;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 字典类型 服务层实现
 *
 * @author simon
 * @date 2020-05-21
 */
@Service("dict")
public class DictService extends BaseService<DictEntity, String> {

    private DictDao dao;

    private DictDataDao dataDao;

    @Autowired
    public DictService(DictDao dao, DictDataDao dataDao) {
        super(dao);
        this.dao = dao;
        this.dataDao = dataDao;
    }

    public static DictService getInstance() {
        return SpringUtil.getBean(DictService.class);
    }

    public QueryResultSet<DictEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<DictEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    /**
     * 创建 DictEntity
     *
     * @param entity
     */
    @Override
    @Transactional
    @CacheEvict(value = RbacCacheConstants.DICT_CACHE, allEntries = true)
    public void create(DictEntity entity) {
        entity.setId(IdGenerator.getInstance().nextStringId());
        super.create(entity);
        List<DictDataEntity> data = entity.getData();
        for (int i = 0; i < data.size(); i++) {
            DictDataEntity dictDataEntity = data.get(i);
            dictDataEntity.setId(IdGenerator.getInstance().nextStringId());
            dictDataEntity.setKey(entity.getKey());
            dataDao.create(dictDataEntity);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = RbacCacheConstants.DICT_CACHE, allEntries = true)
    public void update(DictEntity entity) {
        super.update(entity);
        dataDao.deleteByKey(entity.getKey());
        List<DictDataEntity> data = entity.getData();
        for (int i = 0; i < data.size(); i++) {
            DictDataEntity dictDataEntity = data.get(i);
            dictDataEntity.setId(IdGenerator.getInstance().nextStringId());
            dictDataEntity.setKey(entity.getKey());
            dataDao.create(dictDataEntity);
        }
    }

    @Override
    public DictEntity findById(String id) {
        DictEntity dictEntity = super.findById(id);
        if (dictEntity != null) {
            List<DictDataEntity> data = dataDao.findByKey(dictEntity.getKey());
            dictEntity.setData(data);
        }
        return dictEntity;
    }

    @Cacheable(value = RbacCacheConstants.DICT_CACHE, key = "'key_'+#key")
    public DictEntity findByKey(String key) {
        DictEntity dictEntity = dao.findByKey(key);
        if (dictEntity != null) {
            List<DictDataEntity> data = dataDao.findByKey(dictEntity.getKey());
            dictEntity.setData(data);
        }
        return dictEntity;
    }

    /**
     * 批量删除字典（优化：避免嵌套循环，使用 deleteByKey 批量删除字典数据）
     *
     * @param ids 字典ID字符串，格式：id1,id2,id3
     */
    @CacheEvict(value = RbacCacheConstants.DICT_CACHE, allEntries = true)
    @Transactional
    public void deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }

        // 批量查询所有字典
        String[] idArray = ids.split(",");
        List<DictEntity> entities = new ArrayList<>();
        for (String id : idArray) {
            DictEntity entity = dao.findById(id);
            if (entity != null) {
                entities.add(entity);
            }
        }

        if (entities.isEmpty()) {
            return;
        }

        // 批量删除字典数据（使用 deleteByKey，一次删除所有相同 key 的数据）
        for (DictEntity entity : entities) {
            dataDao.deleteByKey(entity.getKey());
        }

        // 批量删除字典
        for (DictEntity entity : entities) {
            dao.delete(entity.getId());
        }
    }

    @Cacheable(value = RbacCacheConstants.DICT_CACHE, key = "'array_'+#key")
    public String[][] getDictArray(String key) {
        DictEntity entity = findByKey(key);
        if (entity != null) {
            List<DictDataEntity> values = entity.getData();
            if (values != null && values.size() > 0) {
                String[][] vals = new String[values.size()][2];
                for (int i = 0; i < values.size(); i++) {
                    DictDataEntity dictDataEntity = values.get(i);
                    vals[i] = new String[]{dictDataEntity.getValue(), dictDataEntity.getLabel()};
                }
                return vals;
            }
        }
        return new String[0][0];
    }

    @Cacheable(value = RbacCacheConstants.DICT_CACHE, key = "'data_'+#key")
    public List<DictDataEntity> getDictData(String key) {
        DictEntity sysConfigEntity = findByKey(key);
        if (sysConfigEntity != null) {
            return sysConfigEntity.getData();
        } else {
            return new ArrayList<>();
        }
    }

    @Cacheable(value = RbacCacheConstants.DICT_CACHE, key = "'display_'+#key+'_'+#value")
    public String getDisplayLabel(String key, String value) {
        DictEntity sysConfigEntity = findByKey(key);
        if (sysConfigEntity != null) {
            List<DictDataEntity> data = sysConfigEntity.getData();
            for (int i = 0; i < data.size(); i++) {
                DictDataEntity entity = data.get(i);
                if (StringHelper.equals(entity.getValue(), value)) {
                    return entity.getLabel();
                }
            }
        }
        return "";
    }

    @Cacheable(value = RbacCacheConstants.DICT_CACHE, key = "'dict_map_'+#key")
    public Map<String, String> getDictMap(String key) {
        DictEntity sysConfigEntity = findByKey(key);
        Map<String, String> map = new LinkedHashMap<>();
        if (sysConfigEntity != null) {
            List<DictDataEntity> data = sysConfigEntity.getData();
            for (int i = 0; i < data.size(); i++) {
                DictDataEntity dataEntity = data.get(i);
                map.put(dataEntity.getValue(), dataEntity.getLabel());
            }
        }
        return map;
    }

    @Cacheable(value = RbacCacheConstants.DICT_CACHE, key = "'dict_list_'+#key")
    public List<Map<String, String>> getDictList(String key) {
        DictEntity sysConfigEntity = findByKey(key);
        List<Map<String, String>> list = new ArrayList<>();
        if (sysConfigEntity != null) {
            List<DictDataEntity> data = sysConfigEntity.getData();
            for (int i = 0; i < data.size(); i++) {
                Map<String, String> map = new HashMap<>();
                DictDataEntity dataEntity = data.get(i);
                map.put("key", dataEntity.getValue());
                map.put("value", dataEntity.getLabel());
                list.add(map);
            }
        }
        return list;
    }
}
