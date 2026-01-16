package com.adminpro.system.rbac.domains.entity.usertoken;

import com.adminpro.framework.base.entity.BaseDao;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.framework.jdbc.sqlbuilder.SelectBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户Token表 数据库持久层
 *
 * @author simon
 * @date 2018-09-03
 */
@Component
public class UserTokenDao extends BaseDao<UserTokenEntity, String> {

    public QueryResultSet<UserTokenEntity> search(SearchParam param) {
        SelectBuilder<UserTokenEntity> select = new SelectBuilder<UserTokenEntity>(UserTokenEntity.class);
        select.setSearchParam(param);
        return search(select);
    }

    public UserTokenEntity findByToken(String token) {
        SelectBuilder<UserTokenEntity> select = new SelectBuilder<UserTokenEntity>(UserTokenEntity.class);
        select.addWhereAnd(UserTokenEntity.COL_TOKEN + " = ? ", token);
        return executeSingle(select);
    }

    public List<UserTokenEntity> findByUserIdAndStatus(String userId, String status) {
        SelectBuilder<UserTokenEntity> select = new SelectBuilder<UserTokenEntity>(UserTokenEntity.class);
        select.addWhereAnd(UserTokenEntity.COL_USER_ID + " = ? ", userId);
        select.addWhereAnd(UserTokenEntity.COL_STATUS + " = ? ", status);
        return execute(select);
    }
}
