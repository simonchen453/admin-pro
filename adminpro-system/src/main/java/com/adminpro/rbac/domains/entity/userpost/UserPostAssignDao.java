package com.adminpro.rbac.domains.entity.userpost;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.rbac.domains.entity.user.UserIden;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 用户角色分配表 数据库持久层
 *
 * @author simon
 * @date 2020-06-14
 */
@Repository
public class UserPostAssignDao extends BaseDao<UserPostAssignEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<UserPostAssignEntity> search(SearchParam param) {
        SelectBuilder<UserPostAssignEntity> select = new SelectBuilder<UserPostAssignEntity>(UserPostAssignEntity.class);
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
    public List<UserPostAssignEntity> findByParam(SearchParam param) {
        SelectBuilder<UserPostAssignEntity> select = new SelectBuilder<UserPostAssignEntity>(UserPostAssignEntity.class);
        prepareSelectBuilder(select, param);
        return execute(select);
    }

    public List<UserPostAssignEntity> findByUserIden(UserIden userIden) {
        SelectBuilder<UserPostAssignEntity> select = new SelectBuilder<UserPostAssignEntity>(UserPostAssignEntity.class);
        select.addWhereAnd(UserPostAssignEntity.COL_USER_DOMAIN + " = ?", userIden.getUserDomain());
        select.addWhereAnd(UserPostAssignEntity.COL_USER_ID + " = ?", userIden.getUserId());
        return execute(select);
    }

    public void deleteByUserIden(UserIden userIden) {
        DeleteBuilder delete = new DeleteBuilder(UserPostAssignEntity.TABLE_NAME);
        delete.addWhereAnd(UserPostAssignEntity.COL_USER_DOMAIN + " = ?", userIden.getUserDomain());
        delete.addWhereAnd(UserPostAssignEntity.COL_USER_ID + " = ?", userIden.getUserId());
        execute(delete);
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
            //select.addWhereAnd(UserPostAssignEntity.COL_TITLE + " like ?", "%" + condition+"%");
        }
    }
}
