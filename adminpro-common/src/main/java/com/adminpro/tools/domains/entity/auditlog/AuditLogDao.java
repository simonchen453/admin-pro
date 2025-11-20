package com.adminpro.tools.domains.entity.auditlog;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * 日志Dao类
 *
 * @author simon
 */
@Repository
public class AuditLogDao extends BaseDao<AuditLogEntity, String> {
    public static final String SQL = "select al.*, u.col_real_name from sys_audit_log_tbl al left join sys_user_tbl u " +
            "on al.col_created_by_user_id = u.col_user_id and al.col_created_by_user_domain = u.col_user_domain";


    public QueryResultSet<AuditLogDTO> search(SearchParam param) {

        SelectBuilder<AuditLogDTO> select = new SelectBuilder<AuditLogDTO>(new RowMapper<AuditLogDTO>() {
            @Override
            public AuditLogDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                AuditLogDTO dto = new AuditLogDTO();
                dto.setId(resultSet.getString(AuditLogEntity.COL_ID));
                dto.setCategory(resultSet.getString(AuditLogEntity.COL_CATEGORY));
                dto.setEvent(resultSet.getString(AuditLogEntity.COL_EVENT));
                dto.setAfterData(resultSet.getString(AuditLogEntity.COL_AFTER_DATA));
                dto.setBeforeData(resultSet.getString(AuditLogEntity.COL_BEFORE_DATA));
                dto.setIpAddress(resultSet.getString(AuditLogEntity.COL_IP_ADDRESS));
                dto.setLogDate(resultSet.getTimestamp(AuditLogEntity.COL_LOG_DATE));
                dto.setModule(resultSet.getString(AuditLogEntity.COL_MODULE));
                dto.setSessionId(resultSet.getString(AuditLogEntity.COL_SESSION_ID));
                dto.setStatus(resultSet.getString(AuditLogEntity.COL_STATUS));
                dto.setUserName(resultSet.getString(UserEntity.COL_REAL_NAME));
                //处理日志字段
                retrieveAuditField(dto, resultSet);
                return dto;
            }
        });
        select.setQuery(SQL);
        select.setSearchParam(param);
        Map<String, Object> filters = param.getFilters();
        String module = (String) filters.get("module");
        String category = (String) filters.get("category");
        String event = (String) filters.get("event");
        String status = (String) filters.get("status");
        String user = (String) filters.get("user");
        Date startDate = (Date) filters.get("startDate");
        Date endDate = (Date) filters.get("endDate");

        if (StringUtils.isNotEmpty(status)) {
            select.addWhereAnd("al." + AuditLogEntity.COL_STATUS + " like ?", "%" + status + "%");
        }

        if (StringUtils.isNotEmpty(module)) {
            select.addWhereAnd("al." + AuditLogEntity.COL_MODULE + " like ?", "%" + module + "%");
        }
        if (StringUtils.isNotEmpty(category)) {
            select.addWhereAnd("al." + AuditLogEntity.COL_CATEGORY + " like ?", "%" + category + "%");
        }
        if (StringUtils.isNotEmpty(event)) {
            select.addWhereAnd("al." + AuditLogEntity.COL_EVENT + " like ?", "%" + event + "%");
        }
        if (StringUtils.isNotEmpty(user)) {
            select.addWhereAnd("u." + UserEntity.COL_REAL_NAME + " like ?", "%" + user + "%");
        }
        if (startDate != null) {
            select.addWhereAnd("al." + AuditLogEntity.COL_CREATED_DATE + " >= ?", startDate);
        }
        if (endDate != null) {
            select.addWhereAnd("al." + AuditLogEntity.COL_CREATED_DATE + " <= ?", endDate);
        }
        select.addOrderByDescending(AuditLogEntity.COL_CREATED_DATE);
        return search(select);
    }
}
