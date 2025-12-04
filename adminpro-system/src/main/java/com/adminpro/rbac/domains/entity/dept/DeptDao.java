package com.adminpro.rbac.domains.entity.dept;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 部门表 数据库持久层
 *
 * @author simon
 * @date 2020-05-24
 */
@Repository
public class DeptDao extends BaseDao<DeptEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<DeptEntity> search(SearchParam param) {
        SelectBuilder<DeptEntity> select = new SelectBuilder<DeptEntity>(DeptEntity.class);
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
    public List<DeptEntity> findByParam(SearchParam param) {
        SelectBuilder<DeptEntity> select = new SelectBuilder<DeptEntity>(DeptEntity.class);
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
        String status = (String) filters.get("status");
        String parentId = (String) filters.get("parentId");
        if (StringUtils.isNotEmpty(name)) {
            select.addWhereAnd(DeptEntity.COL_NAME + " like ?", "%" + name + "%");
        }
        if (StringUtils.isNotEmpty(status)) {
            select.addWhereAnd(DeptEntity.COL_STATUS + " = ?", status);
        }
        if (StringUtils.isNotEmpty(parentId)) {
            select.addWhereAnd(DeptEntity.COL_PARENT_ID + " = ?", parentId);
        }
    }

    public DeptEntity findByNo(String no) {
        SelectBuilder<DeptEntity> select = new SelectBuilder<DeptEntity>(DeptEntity.class);
        select.addWhereAnd(DeptEntity.COL_NO + " = ? ", no);
        return executeSingle(select);
    }

    public List<DeptEntity> findByParentId(String parentId) {
        SelectBuilder<DeptEntity> select = new SelectBuilder<DeptEntity>(DeptEntity.class);
        select.addWhereAnd(DeptEntity.COL_PARENT_ID + " = ? ", parentId);
        return execute(select);
    }
}
