package com.adminpro.system.tools.domains.entity.session;

import com.adminpro.framework.base.entity.BaseDao;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.framework.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.framework.jdbc.sqlbuilder.SelectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 用户Session表 数据库持久层
 *
 * @author simon
 * @date 2020-06-17
 */
@Repository
public class SessionDao extends BaseDao<SessionEntity, String> {
    private static final String SESSION_QUERY = "select sl.*, u.col_login_name from sys_session_tbl sl " +
            "left join sys_user_tbl u on sl.col_user_id = u.col_id";

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<SessionEntity> search(SearchParam param) {
        SelectBuilder<SessionEntity> select = new SelectBuilder<SessionEntity>(getSessionRowMapper());
        select.setQuery(SESSION_QUERY);
        select.setSearchParam(param);
        prepareSelectBuilder(select, param);
        return search(select);
    }

    /**
     * 根据查询参数获取所有的记录
     *
     * @param param
     * @return
     */
    public List<SessionEntity> findByParam(SearchParam param) {
        SelectBuilder<SessionEntity> select = new SelectBuilder<SessionEntity>(getSessionRowMapper());
        select.setQuery(SESSION_QUERY);
        prepareSelectBuilder(select, param);
        return execute(select);
    }

    /**
     * 准备查询条件
     *
     * @param param
     * @return
     */
    private void prepareSelectBuilder(SelectBuilder select, SearchParam param) {
        Map<String, Object> filters = param.getFilters();
        String sessionId = (String) filters.get("sessionId");
        String loginName = (String) filters.get("loginName");
        String status = (String) filters.get("status");
        String ipAddr = (String) filters.get("ipAddr");
        String deptNo = (String) filters.get("deptNo");
        if (StringUtils.isNotEmpty(sessionId)) {
            select.addWhereAnd("sl." + SessionEntity.COL_SESSION_ID + " like ?", "%" + sessionId + "%");
        }
        if (StringUtils.isNotEmpty(loginName)) {
            select.addWhereAnd("u.col_login_name like ?", "%" + loginName + "%");
        }
        if (StringUtils.isNotEmpty(ipAddr)) {
            select.addWhereAnd("sl." + SessionEntity.COL_IP_ADDR + " like ?", "%" + ipAddr + "%");
        }
        if (StringUtils.isNotEmpty(deptNo)) {
            select.addWhereAnd("sl." + SessionEntity.COL_DEPT_NO + " like ?", "%" + deptNo + "%");
        }
        if (StringUtils.isNotEmpty(status)) {
            select.addWhereAnd("sl." + SessionEntity.COL_STATUS + " = ?", status);
        }
        select.addOrderByDescending(SessionEntity.COL_CREATED_AT);
    }

    private org.springframework.jdbc.core.RowMapper<SessionEntity> getSessionRowMapper() {
        return (resultSet, i) -> {
            com.adminpro.framework.jdbc.DBRowMapper<SessionEntity> mapper =
                    new com.adminpro.framework.jdbc.DBRowMapper<>(SessionEntity.class);
            SessionEntity entity = mapper.mapRow(resultSet, i);
            entity.setLoginName(resultSet.getString("col_login_name"));
            return entity;
        };
    }

    public int deleteAll() {
        DeleteBuilder delete = new DeleteBuilder(SessionEntity.TABLE_NAME);
        return execute(delete);
    }

    public List<SessionEntity> findByUserIdAndStatus(String userId, String status) {
        SelectBuilder<SessionEntity> select = new SelectBuilder<SessionEntity>(SessionEntity.class);
        select.addWhereAnd(SessionEntity.COL_USER_ID + " = ? ", userId);
        select.addWhereAnd(SessionEntity.COL_STATUS + " = ? ", status);
        return execute(select);
    }

    public SessionEntity findBySessionId(String sessionId) {
        SelectBuilder<SessionEntity> select = new SelectBuilder<SessionEntity>(SessionEntity.class);
        select.addWhereAnd(SessionEntity.COL_SESSION_ID + " = ? ", sessionId);
        return executeSingle(select);
    }

    public List<SessionEntity> findByStatus(String status) {
        SelectBuilder<SessionEntity> select = new SelectBuilder<SessionEntity>(SessionEntity.class);
        select.addWhereAnd(SessionEntity.COL_STATUS + " = ? ", status);
        return execute(select);
    }
}
