package com.adminpro.rbac.domains.vo.job;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.framework.batchjob.utils.CronUtils;
import com.adminpro.tools.domains.entity.job.ScheduleJobEntity;
import com.adminpro.tools.domains.entity.job.ScheduleJobService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobUpdateValidator extends BaseValidator<JobVo> {
    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public void validate(JobVo model, MessageBundle msgBundle) {
        if (!msgBundle.hasErrorMessage("id")) {
            if (model.getId() != null) {
                ScheduleJobEntity entity = scheduleJobService.findById(model.getId());
                if (entity == null) {
                    msgBundle.addErrorMessage("id", "定时任务不存在");
                }
            }
        }

        String beanName = model.getBeanName();
        if (StringUtils.isEmpty(beanName)) {
            msgBundle.addErrorMessage("beanName", "类名不能为空");
        }

        String methodName = model.getMethodName();
        if (StringUtils.isEmpty(methodName)) {
            msgBundle.addErrorMessage("methodName", "方法名不能为空");
        }

        String cronExpression = model.getCronExpression();
        if (StringUtils.isEmpty(cronExpression)) {
            msgBundle.addErrorMessage("cronExpression", "正则表达式不能为空");
        } else if (!CronUtils.isValid(cronExpression)) {
            msgBundle.addErrorMessage("cronExpression", "正则表达式不合法");
        }

        String params = model.getParams();
        if (StringUtils.isEmpty(params)) {
//            msgBundle.addErrorMessage("params", "参数不能为空");
        }
    }
}
