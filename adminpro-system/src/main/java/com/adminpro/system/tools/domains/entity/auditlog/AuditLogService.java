package com.adminpro.system.tools.domains.entity.auditlog;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import org.springframework.stereotype.Service;

/**
 * 日志服务类
 *
 * @author simon
 */
@Service
public class AuditLogService extends BaseService<AuditLogEntity, String> {

    private final AuditLogDao dao;

    public AuditLogService(AuditLogDao dao) {
        super(dao);
        this.dao = dao;
    }

    public QueryResultSet<AuditLogDTO> search(SearchParam param) {
        return dao.search(param);
    }

    public static AuditLogService getInstance() {
        return SpringUtil.getBean(AuditLogService.class);
    }

}
