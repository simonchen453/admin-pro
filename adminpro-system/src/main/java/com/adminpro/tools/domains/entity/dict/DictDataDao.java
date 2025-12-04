package com.adminpro.tools.domains.entity.dict;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 字典数据表 数据库持久层
 *
 * @author simon
 * @date 2020-05-21
 */
@Repository
public class DictDataDao extends BaseDao<DictDataEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<DictDataEntity> search(SearchParam param) {
        SelectBuilder<DictDataEntity> select = new SelectBuilder<DictDataEntity>(DictDataEntity.class);
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
    public List<DictDataEntity> findByParam(SearchParam param) {
        SelectBuilder<DictDataEntity> select = new SelectBuilder<DictDataEntity>(DictDataEntity.class);
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
        //TODO 页面过滤条件
        Map<String, Object> filters = param.getFilters();
        String condition = (String) filters.get("condition");
        if (StringUtils.isNotEmpty(condition)) {
            //select.addWhereAnd(DictDataEntity.COL_TITLE + " like ?", "%" + condition+"%");
        }
    }

    /**
     * 根据Key查询配置项
     *
     * @param key
     * @return
     */
    public List<DictDataEntity> findByKey(String key) {
        SelectBuilder<DictDataEntity> select = new SelectBuilder<DictDataEntity>(DictDataEntity.class);
        select.addWhereAnd(DictDataEntity.COL_KEY + " = ? ", key);
        select.addOrderByAscending(DictDataEntity.COL_ORDER);
        return execute(select);
    }

    public void deleteByKey(String key) {
        DeleteBuilder delete = new DeleteBuilder(DictDataEntity.TABLE_NAME);
        delete.addWhereAnd(DictDataEntity.COL_KEY + " = ? ", key);
        execute(delete);
    }
}
