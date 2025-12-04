package com.adminpro.system.rbac.domains.vo.job;

import com.adminpro.framework.base.util.DateUtil;
import com.adminpro.framework.jdbc.query.IModelConverter;
import com.adminpro.system.tools.domains.entity.job.ScheduleJobLogEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JobLogVoConverter implements IModelConverter<ScheduleJobLogEntity, JobLogVo> {


    @Override
    public JobLogVo convert(ScheduleJobLogEntity entity) {
        if (entity == null) {
            return null;
        }
        JobLogVo logVo = new JobLogVo();
        logVo.setId(entity.getId());
        logVo.setBeanName(entity.getBeanName());
        logVo.setMethodName(entity.getMethodName());
        logVo.setParams(entity.getParams());
        Integer status = entity.getStatus();
        logVo.setStatus(String.valueOf(status));
        Date createTime = entity.getCreatedTime();
        String formatDate = DateUtil.formatDateTime(createTime);
        logVo.setCreateTime(formatDate);
        logVo.setTimes(entity.getTimes());
        logVo.setJobId(entity.getJobId());
        return logVo;
    }

    @Override
    public ScheduleJobLogEntity inverse(JobLogVo s) {
        if (s == null) {
            return null;
        }
        return null;
    }
}
