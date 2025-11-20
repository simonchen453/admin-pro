package com.adminpro.tools.domains.entity.job;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.batchjob.ScheduleStatus;
import com.adminpro.framework.batchjob.utils.ScheduleUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Service
public class ScheduleJobService extends BaseService<ScheduleJobEntity, String> {
    @Autowired
    @Qualifier("schedulerFactoryBean")
    private Scheduler scheduler;

    private ScheduleJobDao dao;

    @Autowired
    protected ScheduleJobService(ScheduleJobDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static ScheduleJobService getInstance() {
        return SpringUtil.getBean(ScheduleJobService.class);
    }

    public QueryResultSet<ScheduleJobEntity> search(SearchParam param) {
        return dao.search(param);
    }

    @Transactional
    public void deleteMany(String ids) {
        String[] idArray = StringUtils.split(ids, ",");
        for (String id : idArray) {
            dao.delete(id);
            ScheduleUtils.deleteScheduleJob(scheduler, id);
        }
    }

    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init() {
        List<ScheduleJobEntity> scheduleJobList = dao.findAll();
        for (ScheduleJobEntity scheduleJob : scheduleJobList) {
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getId());
            //如果不存在，则创建
            if (cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }

    @Transactional
    public void save(ScheduleJobEntity scheduleJob) {
        scheduleJob.setCreatedTime(new Date());
        scheduleJob.setStatus(ScheduleStatus.NORMAL.getValue());
        scheduleJob.setId(IdGenerator.getInstance().nextStringId());
        dao.create(scheduleJob);

        ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
    }

    @Transactional
    public void update(ScheduleJobEntity scheduleJob) {
        ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
        dao.update(scheduleJob);
    }

    @Transactional
    protected void updateBatch(String[] jobIds, int status) {
        for (int i = 0; i < jobIds.length; i++) {
            ScheduleJobEntity entity = dao.findById(jobIds[i]);
            entity.setStatus(status);
            dao.update(entity);
        }
    }

    @Transactional
    public void run(String[] jobIds) {
        for (String jobId : jobIds) {
            ScheduleUtils.run(scheduler, findById(jobId));
        }
    }

    @Transactional
    public void pause(String[] jobIds) {
        String[] ids = new String[jobIds.length];
        for (int i = 0; i < jobIds.length; i++) {
            ids[i] = jobIds[i];
            ScheduleUtils.pauseJob(scheduler, jobIds[i]);
        }

        updateBatch(ids, ScheduleStatus.PAUSE.getValue());
    }

    @Transactional
    public void resume(String[] jobIds) {
        String[] ids = new String[jobIds.length];
        for (int i = 0; i < jobIds.length; i++) {
            ids[i] = jobIds[i];
            ScheduleUtils.resumeJob(scheduler, jobIds[i]);
        }

        updateBatch(ids, ScheduleStatus.NORMAL.getValue());
    }

}
