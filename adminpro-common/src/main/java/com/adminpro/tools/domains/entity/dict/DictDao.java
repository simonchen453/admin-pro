package com.adminpro.tools.domains.entity.dict;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 字典类型表 数据库持久层
 *
 * @author simon
 * @date 2020-05-21
 */
@Repository
public class DictDao extends BaseDao<DictEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<DictEntity> search(SearchParam param) {
        SelectBuilder<DictEntity> select = new SelectBuilder<DictEntity>(DictEntity.class);
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
    public List<DictEntity> findByParam(SearchParam param) {
        SelectBuilder<DictEntity> select = new SelectBuilder<DictEntity>(DictEntity.class);
        prepareSelectBuilder(select, param);
        return execute(select);
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
        String status = (String) filters.get("status");
        if (StringUtils.isNotEmpty(name)) {
            select.addWhereAnd(DictEntity.COL_NAME + " like ?", "%" + name+"%");
        }
        if (StringUtils.isNotEmpty(key)) {
            select.addWhereAnd(DictEntity.COL_KEY + " like ?", "%" + key+"%");
        }
        if (StringUtils.isNotEmpty(status)) {
            select.addWhereAnd(DictEntity.COL_STATUS + " = ?", status);
        }
    }

    public DictEntity findByKey(String key) {
        SelectBuilder<DictEntity> select = new SelectBuilder<DictEntity>(DictEntity.class);
        select.addWhereAnd(DictEntity.COL_KEY + " = ? ", key);
        return executeSingle(select);
    }

}
