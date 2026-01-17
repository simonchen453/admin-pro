package com.adminpro.system.rbac.domains.entity.user;

import com.adminpro.framework.base.entity.BaseDao;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.framework.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.framework.jdbc.sqlbuilder.SelectBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户标签表 数据库持久层
 *
 * @author simon
 * @date 2020-05-21
 */
@Repository
public class UserTagDao extends BaseDao<UserTagEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<UserTagEntity> search(SearchParam param) {
        SelectBuilder<UserTagEntity> select = new SelectBuilder<UserTagEntity>(UserTagEntity.class);
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
    public List<UserTagEntity> findByParam(SearchParam param) {
        SelectBuilder<UserTagEntity> select = new SelectBuilder<UserTagEntity>(UserTagEntity.class);
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
        // select.addWhereAnd(UserTagEntity.COL_TITLE + " like ?", "%" + condition+"%");
    }

    public List<UserTagEntity> findByUserId(String userId) {
        SelectBuilder<UserTagEntity> select = new SelectBuilder<UserTagEntity>(UserTagEntity.class);
        select.addWhereAnd(UserTagEntity.COL_USER_ID + " = ? ", userId);
        return execute(select);
    }

    public void deleteByUserId(String userId) {
        DeleteBuilder delete = new DeleteBuilder(UserTagEntity.TABLE_NAME);
        delete.addWhereAnd(UserTagEntity.COL_USER_ID + " = ? ", userId);
        execute(delete);
    }
}
