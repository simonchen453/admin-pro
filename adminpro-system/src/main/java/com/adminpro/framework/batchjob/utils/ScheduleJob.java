package com.adminpro.framework.batchjob.utils;

import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.tools.domains.entity.job.ScheduleJobEntity;
import com.adminpro.tools.domains.entity.job.ScheduleJobLogEntity;
import com.adminpro.tools.domains.entity.job.ScheduleJobLogService;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * 定时任务
 *
 * @author simon
 */
@Component
public class ScheduleJob extends QuartzJobBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ExecutorService service = Executors.newSingleThreadExecutor();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String jsonJob = context.getMergedJobDataMap().getString(ScheduleJobEntity.JOB_PARAM_KEY);
        ScheduleJobEntity scheduleJob = new Gson().fromJson(jsonJob, ScheduleJobEntity.class);

        logger.info("定时任务开始执行，类名：" + scheduleJob.getBeanName() + ", 方法名：" + scheduleJob.getMethodName());

        //获取spring bean
        ScheduleJobLogService scheduleJobLogService = SpringUtil.getBean(ScheduleJobLogService.class);

        //数据库保存执行记录
        ScheduleJobLogEntity log = new ScheduleJobLogEntity();
        log.setJobId(scheduleJob.getId());
        log.setBeanName(scheduleJob.getBeanName());
        log.setMethodName(scheduleJob.getMethodName());
        log.setParams(scheduleJob.getParams());
        log.setCreatedTime(new Date());

        //任务开始时间
        long startTime = System.currentTimeMillis();

        try {
            //执行任务
            ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getBeanName(),
                    scheduleJob.getMethodName(), scheduleJob.getParams());
            Future<?> future = service.submit(task);

            future.get();

            //任务执行总时长
            long times = System.currentTimeMillis() - startTime;
            log.setTimes((int) times);
            //任务状态    0：成功    1：失败
            log.setStatus(0);
            logger.info("定时任务执行成功，类名：" + scheduleJob.getBeanName() + ", 方法名：" + scheduleJob.getMethodName());
        } catch (Exception e) {
            logger.info("任务执行失败，类名：" + scheduleJob.getBeanName() + ", 方法名：" + scheduleJob.getMethodName(), e);

            //任务执行总时长
            long times = System.currentTimeMillis() - startTime;
            log.setTimes((int) times);

            //任务状态    0：成功    1：失败
            log.setStatus(1);
            log.setError(StringUtils.substring(e.toString(), 0, 2000));
        } finally {
            scheduleJobLogService.create(log);
        }
    }
}
