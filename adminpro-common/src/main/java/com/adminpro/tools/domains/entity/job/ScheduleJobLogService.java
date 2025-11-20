package com.adminpro.tools.domains.entity.job;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 定时任务日志 服务层实现
 *
 * @author simon
 * @date 2018-09-03
 */
@Service
public class ScheduleJobLogService extends BaseService<ScheduleJobLogEntity, String> {

    private ScheduleJobLogDao dao;

    @Autowired
    protected ScheduleJobLogService(ScheduleJobLogDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static ScheduleJobLogService getInstance() {
        return SpringUtil.getBean(ScheduleJobLogService.class);
    }

    public QueryResultSet<ScheduleJobLogEntity> search(SearchParam param) {
        return dao.search(param);
    }

    /**
     * 创建 ScheduleJobLogEntity
     *
     * @param entity
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void create(ScheduleJobLogEntity entity) {
        entity.setId(IdGenerator.getInstance().nextStringId());
        dao.create(entity);
    }

    /**
     * 更新 ScheduleJobLogEntity
     *
     * @param entity
     */
    @Transactional
    public void update(ScheduleJobLogEntity entity) {
        dao.update(entity);
    }

    @Transactional
    public void deleteMany(String ids) {
        String[] idArray = StringUtils.split(ids, ",");
        for (String id : idArray) {
            delete(id);
        }
    }

    public void deleteByBeanName(String beanName) {
        dao.deleteByBeanName(beanName);
    }
}
