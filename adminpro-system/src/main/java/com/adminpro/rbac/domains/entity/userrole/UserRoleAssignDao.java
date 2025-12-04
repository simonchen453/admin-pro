package com.adminpro.rbac.domains.entity.userrole;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.rbac.domains.entity.user.UserIden;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户角色分配表 数据库持久层
 *
 * @author simon
 * @date 2018-09-06
 */
@Component
public class UserRoleAssignDao extends BaseDao<UserRoleAssignEntity, String> {

    public QueryResultSet<UserRoleAssignEntity> search(SearchParam param) {
        SelectBuilder<UserRoleAssignEntity> select = new SelectBuilder<UserRoleAssignEntity>(UserRoleAssignEntity.class);
        select.setSearchParam(param);
        return search(select);
    }

    public void deleteByUserIden(UserIden userIden) {
        DeleteBuilder delete = new DeleteBuilder(UserRoleAssignEntity.TABLE_NAME);
        delete.addWhereAnd(UserRoleAssignEntity.COL_USER_DOMAIN + " = ? ", userIden.getUserDomain());
        delete.addWhereAnd(UserRoleAssignEntity.COL_USER_ID + " = ? ", userIden.getUserId());
        execute(delete);
    }

    public UserRoleAssignEntity findByUserDomainAndUserIdAndRoleId(String userDomain, String userId, String roleId) {
        SelectBuilder<UserRoleAssignEntity> select = new SelectBuilder<UserRoleAssignEntity>(UserRoleAssignEntity.class);
        select.setTable(UserRoleAssignEntity.TABLE_NAME);
        select.addWhereAnd(UserRoleAssignEntity.COL_USER_DOMAIN + " = ? ", userDomain);
        select.addWhereAnd(UserRoleAssignEntity.COL_USER_ID + " = ? ", userId);
        select.addWhereAnd(UserRoleAssignEntity.COL_ROLE_NAME + " = ? ", roleId);
        return executeSingle(select);
    }

    public List<UserRoleAssignEntity> findByUserDomainAndUserId(String userDomain, String userId) {
        SelectBuilder<UserRoleAssignEntity> select = new SelectBuilder<UserRoleAssignEntity>(UserRoleAssignEntity.class);
        select.addWhereAnd(UserRoleAssignEntity.COL_USER_DOMAIN + " = ? ", userDomain);
        select.addWhereAnd(UserRoleAssignEntity.COL_USER_ID + " = ? ", userId);
        return execute(select);
    }
}
