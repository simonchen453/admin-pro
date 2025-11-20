package com.adminpro.tools.domains.entity.syslog;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统日志 服务层实现
 *
 * @author simon
 * @date 2018-11-29
 */
@Service
public class SysLogService extends BaseService<SysLogEntity, String> {

    private SysLogDao dao;

    @Autowired
    public SysLogService(SysLogDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static SysLogService getInstance() {
        return SpringUtil.getBean(SysLogService.class);
    }

    public QueryResultSet<SysLogDTO> search(SearchParam param) {
        return dao.search(param);
    }

    @Transactional
    public void deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }
        String[] split = ids.split(",");
        for (int i = 0; i < split.length; i++) {
            dao.delete(split[i]);
        }
    }
}
