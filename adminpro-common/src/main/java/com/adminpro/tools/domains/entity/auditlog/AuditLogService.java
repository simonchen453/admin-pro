package com.adminpro.tools.domains.entity.auditlog;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 日志服务类
 *
 * @author simon
 */
@Service
public class AuditLogService extends BaseService<AuditLogEntity, String> {

    private AuditLogDao dao;

    @Autowired
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
