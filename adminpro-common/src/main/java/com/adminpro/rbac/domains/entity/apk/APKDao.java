package com.adminpro.rbac.domains.entity.apk;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * APK版本管理表 数据库持久层
 *
 * @author simon
 * @date 2021-02-03
 */
@Repository
public class APKDao extends BaseDao<APKEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<APKEntity> search(SearchParam param) {
        SelectBuilder<APKEntity> select = new SelectBuilder<>(APKEntity.class);
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
    public List<APKEntity> findByParam(SearchParam param) {
        SelectBuilder<APKEntity> select = new SelectBuilder<>(APKEntity.class);
        prepareSelectBuilder(select, param);
        return execute(select);
    }

    public List<APKEntity> findByTypeAndVerCode(String type, Integer verCode) {
        SelectBuilder<APKEntity> select = new SelectBuilder<>(APKEntity.class);
        select.addWhereAnd(APKEntity.COL_TYPE + " = ? ", type);
        select.addWhereAnd(APKEntity.COL_VER_CODE + " = ? ", verCode);
        return execute(select);
    }

    public APKEntity findLatestVersion(String type) {
        SelectBuilder<APKEntity> select = new SelectBuilder<>(APKEntity.class);
        select.addWhereAnd(APKEntity.COL_TYPE + " = ? ", type);
        select.addOrderByDescending(APKEntity.COL_VER_CODE);
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
        String condition = (String) filters.get("condition");
        if (StringUtils.isNotEmpty(condition)) {
            //select.addWhereAnd(ApkEntity.COL_TITLE + " like ?", "%" + condition+"%");
        }
    }
}
