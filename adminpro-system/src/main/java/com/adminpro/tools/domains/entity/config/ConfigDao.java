package com.adminpro.tools.domains.entity.config;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.rbac.common.RbacCacheConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 参数配置表 数据库持久层
 *
 * @author simon
 * @date 2020-06-15
 */
@Repository
public class ConfigDao extends BaseDao<ConfigEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<ConfigEntity> search(SearchParam param) {
        SelectBuilder<ConfigEntity> select = new SelectBuilder<ConfigEntity>(ConfigEntity.class);
        select.setSearchParam(param);
        prepareSelectBuilder(select, param);
        return search(select);
    }

    /**
     * 根据查询参数获取所有的记录
     *
     * @param param
     * @return
     */
    public List<ConfigEntity> findByParam(SearchParam param) {
        SelectBuilder<ConfigEntity> select = new SelectBuilder<ConfigEntity>(ConfigEntity.class);
        prepareSelectBuilder(select, param);
        return execute(select);
    }

    @Cacheable(value = RbacCacheConstants.CONFIG_CACHE, key = "'key_'+#key")
    public ConfigEntity findByKey(String key) {
        SelectBuilder<ConfigEntity> select = new SelectBuilder<ConfigEntity>(ConfigEntity.class);
        select.addWhereAnd(ConfigEntity.COL_KEY + " = ?", key);
        return executeSingle(select);
    }

    /**
     * 准备查询条件
     *
     * @param param
     * @return
     */
    private void prepareSelectBuilder(SelectBuilder select, SearchParam param) {
        Map<String, Object> filters = param.getFilters();
        String name = (String) filters.get("name");
        String key = (String) filters.get("key");
        String value = (String) filters.get("value");
        if (StringUtils.isNotEmpty(name)) {
            select.addWhereAnd(ConfigEntity.COL_NAME + " like ?", "%" + name + "%");
        }
        if (StringUtils.isNotEmpty(key)) {
            select.addWhereAnd(ConfigEntity.COL_KEY + " like ?", "%" + key + "%");
        }
        if (StringUtils.isNotEmpty(value)) {
            select.addWhereAnd(ConfigEntity.COL_VALUE + " like ?", "%" + value + "%");
        }
    }
}
