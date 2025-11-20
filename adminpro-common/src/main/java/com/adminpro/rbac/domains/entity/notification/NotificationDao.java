package com.adminpro.rbac.domains.entity.notification;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 通知表 数据库持久层
 *
 * @author simon
 * @date 2018-09-25
 */
@Repository
public class NotificationDao extends BaseDao<NotificationEntity, String> {

    public QueryResultSet<NotificationEntity> search(SearchParam param) {
        SelectBuilder<NotificationEntity> select = new SelectBuilder<NotificationEntity>(NotificationEntity.class);
        select.setSearchParam(param);
        Map<String, Object> filters = param.getFilters();
        String condition = (String) filters.get("condition");
        if (StringUtils.isNotEmpty(condition)) {
            select.addWhereAnd(NotificationEntity.COL_TITLE + " like ?", "%" + condition + "%");
        }
        select.addOrderByDescending(NotificationEntity.COL_CREATED_DATE);
        return search(select);
    }

    public List<NotificationEntity> latest() {
        SearchParam param = new SearchParam();
        param.setPageNo(1);
        param.setPageSize(3);
        SelectBuilder<NotificationEntity> select = new SelectBuilder<NotificationEntity>(NotificationEntity.class);
        select.setSearchParam(param);
        select.addOrderByDescending(NotificationEntity.COL_CREATED_DATE);
        List<NotificationEntity> execute = execute(select);
        return execute;
    }

    public List<NotificationEntity> findByDomain(String domain, int length) {
        SelectBuilder<NotificationEntity> select = new SelectBuilder<NotificationEntity>(NotificationEntity.class);
        select.addWhereAnd(NotificationEntity.COL_USER_DOMAIN + " = ? ", domain);
        select.addWhereAnd(NotificationEntity.COL_START_TIME + " <= ? ", DateUtil.now());
        select.addWhereAnd(NotificationEntity.COL_END_TIME + " >= ? ", DateUtil.now());
        if (length <= 0) {
            throw new BaseRuntimeException("至少查询一条记录");
        }
        SearchParam param = new SearchParam();
        param.setPageNo(1);
        param.setPageSize(length);

        select.setSearchParam(param);
        return execute(select);
    }
}
