package com.adminpro.system.rbac.domains.entity.usermenu;

import com.adminpro.framework.base.entity.BaseDao;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.framework.jdbc.sqlbuilder.SelectBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户菜单分配表 数据库持久层
 *
 * @author simon
 * @date 2020-05-21
 */
@Repository
public class UserMenuAssignDao extends BaseDao<UserMenuAssignEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<UserMenuAssignEntity> search(SearchParam param) {
        SelectBuilder<UserMenuAssignEntity> select = new SelectBuilder<UserMenuAssignEntity>(
                UserMenuAssignEntity.class);
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
    public List<UserMenuAssignEntity> findByParam(SearchParam param) {
        SelectBuilder<UserMenuAssignEntity> select = new SelectBuilder<UserMenuAssignEntity>(
                UserMenuAssignEntity.class);
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
        // select.addWhereAnd(UserMenuAssignEntity.COL_TITLE + " like ?", "%" +
        // condition+"%");
    }
}
