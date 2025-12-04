package com.adminpro.tools.domains.entity.syslog;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * 获取最近的系统日志（用于首页最近活动）
     * 
     * @param limit 返回的记录数，默认 10
     * @return 最近的系统日志列表
     */
    public List<SysLogDTO> findRecentLogs(int limit) {
        SearchParam param = new SearchParam();
        param.setPageNo(1);
        param.setPageSize(limit);
        QueryResultSet<SysLogDTO> resultSet = dao.search(param);
        return resultSet.getRecords();
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
