package com.adminpro.rbac.domains.vo.job;

import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.jdbc.query.IModelConverter;
import com.adminpro.tools.domains.entity.job.ScheduleJobEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JobVoConverter implements IModelConverter<ScheduleJobEntity, JobVo> {


    @Override
    public JobVo convert(ScheduleJobEntity scheduleJobEntity) {
        if (scheduleJobEntity == null) {
            return null;
        }
        JobVo listJobVo = new JobVo();
        listJobVo.setId(scheduleJobEntity.getId());
        listJobVo.setBeanName(scheduleJobEntity.getBeanName());
        listJobVo.setMethodName(scheduleJobEntity.getMethodName());
        listJobVo.setParams(scheduleJobEntity.getParams());
        listJobVo.setCronExpression(scheduleJobEntity.getCronExpression());
        Integer status = scheduleJobEntity.getStatus();
        listJobVo.setStatus(String.valueOf(status));
        listJobVo.setRemark(scheduleJobEntity.getRemark());
        Date createTime = scheduleJobEntity.getCreatedTime();
        String formatDate = DateUtil.formatDate(createTime);
        listJobVo.setCreateTime(formatDate);
        return listJobVo;
    }

    @Override
    public ScheduleJobEntity inverse(JobVo s) {
        if (s == null) {
            return null;
        }
        ScheduleJobEntity scheduleJobEntity = new ScheduleJobEntity();
        scheduleJobEntity.setId(s.getId());
        scheduleJobEntity.setBeanName(s.getBeanName());
        scheduleJobEntity.setMethodName(s.getMethodName());
        scheduleJobEntity.setParams(s.getParams());
        scheduleJobEntity.setCronExpression(s.getCronExpression());
        scheduleJobEntity.setRemark(s.getRemark());
        return scheduleJobEntity;
    }
}
