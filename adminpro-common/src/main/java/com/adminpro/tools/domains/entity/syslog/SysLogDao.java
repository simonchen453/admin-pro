package com.adminpro.tools.domains.entity.syslog;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * 系统日志表 数据库持久层
 *
 * @author simon
 * @date 2018-11-29
 */
@Repository
public class SysLogDao extends BaseDao<SysLogEntity, String> {

    private String sql = "select sl.*, u.col_login_name from sys_sys_log_tbl sl " +
            " left join sys_user_tbl u on sl.col_created_by_user_id = u.col_user_id and sl.col_created_by_user_domain = u.col_user_domain";

    public QueryResultSet<SysLogDTO> search(SearchParam param) {
        SelectBuilder<SysLogDTO> select = new SelectBuilder<SysLogDTO>(getSysLogDTORowMapper());
        select.setQuery(sql);
        select.setSearchParam(param);

        Map<String, Object> filters = param.getFilters();

        String condition = (String) filters.get("condition");
        Date startTime = (Date) filters.get("startTime");
        Date endTime = (Date) filters.get("endTime");

        if (StringUtils.isNotEmpty(condition)) {
            select.addWhereAnd(SysLogEntity.COL_PARAMS + " like ?", "%" + condition + "%");
            select.addWhereOr(SysLogEntity.COL_RESPONSE + " like ?", "%" + condition + "%");
            select.addWhereOr(SysLogEntity.COL_OPERATION + " like ?", "%" + condition + "%");
        }

        if (startTime != null) {
            select.addWhereAnd("sl." + SysLogEntity.COL_CREATED_DATE + " >= ?", startTime);
        }

        if (endTime != null) {
            select.addWhereAnd("sl." + SysLogEntity.COL_CREATED_DATE + " <= ?", endTime);
        }

        select.addOrderByDescending("sl." + SysLogEntity.COL_CREATED_DATE);

        return search(select);
    }

    /**
     * SysLogDTO表映射关系
     *
     * @return
     */
    protected RowMapper<SysLogDTO> getSysLogDTORowMapper() {
        return new RowMapper<SysLogDTO>() {
            @Override
            public SysLogDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                SysLogDTO dto = new SysLogDTO();

                dto.setId(resultSet.getString(SysLogEntity.COL_ID));
                dto.setUserDomain(resultSet.getString(SysLogEntity.COL_USER_DOMAIN));
                dto.setUserId(resultSet.getString(SysLogEntity.COL_USER_ID));
                dto.setIp(resultSet.getString(SysLogEntity.COL_IP));
                dto.setMethod(resultSet.getString(SysLogEntity.COL_METHOD));
                dto.setOperation(resultSet.getString(SysLogEntity.COL_OPERATION));
                dto.setParams(resultSet.getString(SysLogEntity.COL_PARAMS));
                dto.setResponse(resultSet.getString(SysLogEntity.COL_RESPONSE));
                dto.setTime(resultSet.getLong(SysLogEntity.COL_TIME));
                dto.setBrowser(resultSet.getString(SysLogEntity.COL_BROWSER));
                dto.setLoginName(resultSet.getString("col_login_name"));

                //处理日志字段
                retrieveAuditField(dto, resultSet);

                return dto;
            }
        };
    }

    protected RowMapper<SysLogEntity> getSysLogRowMapper() {
        return new RowMapper<SysLogEntity>() {
            @Override
            public SysLogEntity mapRow(ResultSet resultSet, int i) throws SQLException {
                SysLogEntity entity = new SysLogEntity();

                entity.setId(resultSet.getString(SysLogEntity.COL_ID));
                entity.setUserDomain(resultSet.getString(SysLogEntity.COL_USER_DOMAIN));
                entity.setUserId(resultSet.getString(SysLogEntity.COL_USER_ID));
                entity.setIp(resultSet.getString(SysLogEntity.COL_IP));
                entity.setMethod(resultSet.getString(SysLogEntity.COL_METHOD));
                entity.setOperation(resultSet.getString(SysLogEntity.COL_OPERATION));
                entity.setParams(resultSet.getString(SysLogEntity.COL_PARAMS));
                entity.setResponse(resultSet.getString(SysLogEntity.COL_RESPONSE));
                entity.setTime(resultSet.getLong(SysLogEntity.COL_TIME));
                entity.setBrowser(resultSet.getString(SysLogEntity.COL_BROWSER));

                //处理日志字段
                retrieveAuditField(entity, resultSet);

                return entity;
            }
        };
    }
}
