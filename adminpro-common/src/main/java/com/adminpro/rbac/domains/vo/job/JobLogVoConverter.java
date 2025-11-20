package com.adminpro.rbac.domains.vo.job;

import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.jdbc.query.IModelConverter;
import com.adminpro.tools.domains.entity.job.ScheduleJobLogEntity;
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
