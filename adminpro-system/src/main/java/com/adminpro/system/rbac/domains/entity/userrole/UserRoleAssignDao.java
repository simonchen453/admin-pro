package com.adminpro.system.rbac.domains.entity.userrole;

import com.adminpro.framework.base.entity.BaseDao;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.framework.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.framework.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.system.rbac.common.RbacConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 用户角色分配表 数据库持久层
 *
 * @author simon
 * @date 2020-05-27
 */
@Repository
public class UserRoleAssignDao extends BaseDao<UserRoleAssignEntity, String> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<UserRoleAssignEntity> search(SearchParam param) {
        SelectBuilder<UserRoleAssignEntity> select = new SelectBuilder<UserRoleAssignEntity>(
                UserRoleAssignEntity.class);
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
    public List<UserRoleAssignEntity> findByParam(SearchParam param) {
        SelectBuilder<UserRoleAssignEntity> select = new SelectBuilder<UserRoleAssignEntity>(
                UserRoleAssignEntity.class);
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
        // TODO 页面过滤条件
        Map<String, Object> filters = param.getFilters();
        String condition = (String) filters.get("condition");
        if (StringUtils.isNotEmpty(condition)) {
            // select.addWhereAnd(UserRoleAssignEntity.COL_TITLE + " like ?", "%" +
            // condition+"%");
        }
    }

    public List<UserRoleAssignEntity> findByUserId(String userId) {
        SelectBuilder<UserRoleAssignEntity> select = new SelectBuilder<UserRoleAssignEntity>(
                UserRoleAssignEntity.class);
        // select.addWhereAnd(UserRoleAssignEntity.COL_USER_DOMAIN + " = ?",
        // RbacConstants.INTERNET_DOMAIN); // Removed redundant filter
        select.addWhereAnd(UserRoleAssignEntity.COL_USER_ID + " = ?", userId);
        return execute(select);
    }

    public void deleteByUserId(String userId) {
        DeleteBuilder delete = new DeleteBuilder(UserRoleAssignEntity.TABLE_NAME);
        delete.addWhereAnd(UserRoleAssignEntity.COL_USER_ID + " = ?", userId);
        execute(delete);
    }

    public UserRoleAssignEntity findByUserIdAndRoleId(String userId, String roleId) {
        SelectBuilder<UserRoleAssignEntity> select = new SelectBuilder<UserRoleAssignEntity>(
                UserRoleAssignEntity.class);
        select.addWhereAnd(UserRoleAssignEntity.COL_USER_ID + " = ?", userId);
        select.addWhereAnd(UserRoleAssignEntity.COL_ROLE_ID + " = ?", roleId);
        return executeSingle(select);
    }
}
