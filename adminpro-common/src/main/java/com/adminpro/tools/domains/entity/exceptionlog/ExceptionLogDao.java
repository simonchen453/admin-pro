package com.adminpro.tools.domains.entity.exceptionlog;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;

/**
 * 异常表 数据库持久层
 *
 * @author simon
 * @date 2018-11-29
 */
@Repository
public class ExceptionLogDao extends BaseDao<ExceptionLogEntity, String> {

    public QueryResultSet<ExceptionLogEntity> search(SearchParam param) {
        SelectBuilder<ExceptionLogEntity> select = new SelectBuilder<ExceptionLogEntity>(ExceptionLogEntity.class);
        select.setSearchParam(param);
        Map<String, Object> filters = param.getFilters();
        String condition = (String) filters.get("condition");
        Date startTime = (Date) filters.get("startTime");
        Date endTime = (Date) filters.get("endTime");

        if (StringUtils.isNotEmpty(condition)) {
            select.addWhereAnd(ExceptionLogEntity.COL_DETAILS + " like ?", "%" + condition + "%");
            select.addWhereOr(ExceptionLogEntity.COL_ID + " like ?", "%" + condition + "%");
        }

        if (startTime != null) {
            select.addWhereAnd(ExceptionLogEntity.COL_CREATED_DATE + " >= ?", startTime);
        }

        if (endTime != null) {
            select.addWhereAnd(ExceptionLogEntity.COL_CREATED_DATE + " <= ?", endTime);
        }

        select.addOrderByDescending(ExceptionLogEntity.COL_CREATED_DATE);
        return search(select);
    }
}
