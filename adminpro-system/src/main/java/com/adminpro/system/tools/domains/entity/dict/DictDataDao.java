package com.adminpro.system.tools.domains.entity.dict;

import com.adminpro.framework.base.entity.BaseDao;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.framework.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.framework.jdbc.sqlbuilder.SelectBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;

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
