package com.adminpro.tools.domains.entity.session;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
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

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<SessionEntity> search(SearchParam param) {
        SelectBuilder<SessionEntity> select = new SelectBuilder<SessionEntity>(SessionEntity.class);
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
        SelectBuilder<SessionEntity> select = new SelectBuilder<SessionEntity>(SessionEntity.class);
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
        String userDomain = (String) filters.get("userDomain");
        String ipAddr = (String) filters.get("ipAddr");
        String deptNo = (String) filters.get("deptNo");
        if (StringUtils.isNotEmpty(sessionId)) {
            select.addWhereAnd(SessionEntity.COL_SESSION_ID + " like ?", "%" + sessionId + "%");
        }
        if (StringUtils.isNotEmpty(loginName)) {
            select.addWhereAnd(SessionEntity.COL_LOGIN_NAME + " like ?", "%" + loginName + "%");
        }
        if (StringUtils.isNotEmpty(ipAddr)) {
            select.addWhereAnd(SessionEntity.COL_IP_ADDR + " like ?", "%" + ipAddr + "%");
        }
        if (StringUtils.isNotEmpty(deptNo)) {
            select.addWhereAnd(SessionEntity.COL_DEPT_NO + " like ?", "%" + deptNo + "%");
        }
        if (StringUtils.isNotEmpty(status)) {
            select.addWhereAnd(SessionEntity.COL_STATUS + " = ?", status);
        }
        if (StringUtils.isNotEmpty(userDomain)) {
            select.addWhereAnd(SessionEntity.COL_USER_DOMAIN + " = ?", userDomain);
        }
        select.addOrderByDescending(SessionEntity.COL_CREATED_DATE);
    }

    public int deleteAll() {
        DeleteBuilder delete = new DeleteBuilder(SessionEntity.TABLE_NAME);
        return execute(delete);
    }

    public List<SessionEntity> findByUserDomainAndUserIdAndStatus(String userDomain, String userId, String status) {
        SelectBuilder<SessionEntity> select = new SelectBuilder<SessionEntity>(SessionEntity.class);
        select.addWhereAnd(SessionEntity.COL_USER_DOMAIN + " = ? ", userDomain);
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
