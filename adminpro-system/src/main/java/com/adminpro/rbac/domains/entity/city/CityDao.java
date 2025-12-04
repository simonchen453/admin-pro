package com.adminpro.rbac.domains.entity.city;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 城市区划表 数据库持久层
 *
 * @author simon
 * @date 2018-09-06
 */
@Component
public class CityDao extends BaseDao<CityEntity, String> {

    public QueryResultSet<CityEntity> search(SearchParam param) {
        SelectBuilder<CityEntity> select = new SelectBuilder<CityEntity>(CityEntity.class);
        select.setTable(CityEntity.TABLE_NAME);
        select.setSearchParam(param);
        return search(select);
    }

    public List<CityEntity> findByLevel(Integer level) {
        SelectBuilder<CityEntity> select = new SelectBuilder<CityEntity>(CityEntity.class);
        select.setTable(CityEntity.TABLE_NAME);
        select.addWhereAnd(CityEntity.COL_LEVEL + " = ? ", level);
        return execute(select);
    }

    public List<CityEntity> findByLevelAndParent(Integer level, String parent) {
        SelectBuilder<CityEntity> select = new SelectBuilder<CityEntity>(CityEntity.class);
        select.setTable(CityEntity.TABLE_NAME);
        select.addWhereAnd(CityEntity.COL_LEVEL + " = ? ", level);
        select.addWhereAnd(CityEntity.COL_PARENT + " = ? ", parent);
        return execute(select);
    }

    public List<CityEntity> findByTitle(String title) {
        SelectBuilder<CityEntity> select = new SelectBuilder<CityEntity>(CityEntity.class);
        select.setTable(CityEntity.TABLE_NAME);
        select.addWhereAnd(CityEntity.COL_TITLE + " = ? ", title);
        return execute(select);
    }

}
