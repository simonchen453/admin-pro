package com.adminpro.rbac.domains.entity.usertoken;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
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

    public List<UserTokenEntity> findByUserDomainAndUserIdAndStatus(String userDomain, String userId, String status) {
        SelectBuilder<UserTokenEntity> select = new SelectBuilder<UserTokenEntity>(UserTokenEntity.class);
        select.addWhereAnd(UserTokenEntity.COL_USER_DOMAIN + " = ? ", userDomain);
        select.addWhereAnd(UserTokenEntity.COL_USER_ID + " = ? ", userId);
        select.addWhereAnd(UserTokenEntity.COL_STATUS + " = ? ", status);
        return execute(select);
    }
}
