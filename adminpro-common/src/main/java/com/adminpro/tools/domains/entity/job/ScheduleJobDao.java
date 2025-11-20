package com.adminpro.tools.domains.entity.job;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 定时任务表 数据库持久层
 *
 * @author simon
 * @date 2018-09-03
 */
@Component
@DependsOn({"springUtil", "jdbcTemplate", "namedParameterJdbcTemplate"})
public class ScheduleJobDao extends BaseDao<ScheduleJobEntity, String> {

    public QueryResultSet<ScheduleJobEntity> search(SearchParam param) {
        SelectBuilder<ScheduleJobEntity> select = new SelectBuilder<ScheduleJobEntity>(ScheduleJobEntity.class);
        select.setSearchParam(param);
        Map<String, Object> filters = param.getFilters();
        String condition = (String) filters.get("condition");
        if (StringUtils.isNotEmpty(condition)) {
            select.addWhereOr(ScheduleJobEntity.COL_METHOD_NAME + " like ?", "%" + condition + "%");
            select.addWhereOr(ScheduleJobEntity.COL_BEAN_NAME + " like ?", "%" + condition + "%");
        }
        select.addOrderByDescending(ScheduleJobEntity.COL_CREATE_TIME);
        return search(select);
    }
}
