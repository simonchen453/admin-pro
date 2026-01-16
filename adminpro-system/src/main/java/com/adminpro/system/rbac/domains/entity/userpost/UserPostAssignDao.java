package com.adminpro.system.rbac.domains.entity.userpost;

import com.adminpro.framework.base.entity.BaseDao;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.framework.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.framework.jdbc.sqlbuilder.SelectBuilder;
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
        SelectBuilder<UserPostAssignEntity> select = new SelectBuilder<UserPostAssignEntity>(
                UserPostAssignEntity.class);
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
        SelectBuilder<UserPostAssignEntity> select = new SelectBuilder<UserPostAssignEntity>(
                UserPostAssignEntity.class);
        prepareSelectBuilder(select, param);
        return execute(select);
    }

    public List<UserPostAssignEntity> findByUserId(String userId) {
        SelectBuilder<UserPostAssignEntity> select = new SelectBuilder<UserPostAssignEntity>(
                UserPostAssignEntity.class);
        // select.addWhereAnd(UserPostAssignEntity.COL_USER_DOMAIN + " = ?",
        // RbacConstants.INTERNET_DOMAIN); // Removed redundant filter
        select.addWhereAnd(UserPostAssignEntity.COL_USER_ID + " = ?", userId);
        return execute(select);
    }

    public void deleteByUserId(String userId) {
        DeleteBuilder delete = new DeleteBuilder(UserPostAssignEntity.TABLE_NAME);
        delete.addWhereAnd(UserPostAssignEntity.COL_USER_ID + " = ?", userId);
        execute(delete);
    }

    /**
     * 准备查询条件
     *
     * @param param
     * @return
     */
    private void prepareSelectBuilder(SelectBuilder select, SearchParam param) {
        // TODO 页面过滤条件
        Map<String, Object> filters = param.getFilters();
        String condition = (String) filters.get("condition");
        if (StringUtils.isNotEmpty(condition)) {
            // select.addWhereAnd(UserPostAssignEntity.COL_TITLE + " like ?", "%" +
            // condition+"%");
        }
    }
}
