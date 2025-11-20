package com.adminpro.tools.domains.entity.job;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.framework.common.helper.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 定时任务日志表 数据库持久层
 *
 * @author simon
 * @date 2018-09-03
 */
@Component
public class ScheduleJobLogDao extends BaseDao<ScheduleJobLogEntity, String> {

    public QueryResultSet<ScheduleJobLogEntity> search(SearchParam param) {
        SelectBuilder<ScheduleJobLogEntity> select = new SelectBuilder<ScheduleJobLogEntity>(ScheduleJobLogEntity.class);
        select.setSearchParam(param);
        Map<String, Object> filters = param.getFilters();
        String condition = (String) filters.get("condition");
        if (StringUtils.isNotEmpty(condition)) {
            select.addWhereOr(ScheduleJobEntity.COL_METHOD_NAME + " like ?", "%" + condition + "%");
            select.addWhereOr(ScheduleJobEntity.COL_BEAN_NAME + " like ?", "%" + condition + "%");
        }
        select.addOrderByDescending(ScheduleJobLogEntity.COL_CREATED_TIME);
        return search(select);
    }

    public void deleteByBeanName(String beanName) {
        DeleteBuilder delete = new DeleteBuilder(ScheduleJobLogEntity.TABLE_NAME);
        if (StringHelper.isNotEmpty(beanName)) {
            delete.addWhereAnd(ScheduleJobLogEntity.COL_BEAN_NAME + " = ? ", beanName);
        }
        execute(delete);
    }
}
