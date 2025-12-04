package com.adminpro.system.rbac.domains.entity.domain;

import com.adminpro.framework.base.entity.BaseDao;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.framework.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用户域环境配置表 数据库持久层
 *
 * @author simon
 * @date 2018-09-06
 */
@Component
public class UserDomainEnvDao extends BaseDao<UserDomainEnvEntity, String> {

    public QueryResultSet<UserDomainEnvEntity> search(SearchParam param) {
        SelectBuilder<UserDomainEnvEntity> select = new SelectBuilder<UserDomainEnvEntity>(UserDomainEnvEntity.class);
        select.setSearchParam(param);

        Map<String, Object> filters = param.getFilters();
        String userDomain = (String) filters.get("userDomain");
        if (StringUtils.isNotEmpty(userDomain)) {
            select.addWhereAnd(UserDomainEnvEntity.COL_USER_DOMAIN + " like ?", "%" + userDomain + "%");
        }
        return search(select);
    }

    public UserDomainEnvEntity findByUserDomain(String userDomain) {
        SelectBuilder<UserDomainEnvEntity> select = new SelectBuilder<UserDomainEnvEntity>(UserDomainEnvEntity.class);
        select.addWhereAnd(UserDomainEnvEntity.COL_USER_DOMAIN + " = ? ", userDomain);
        return executeSingle(select);
    }
}
