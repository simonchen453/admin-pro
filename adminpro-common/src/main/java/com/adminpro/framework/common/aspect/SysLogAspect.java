package com.adminpro.framework.common.aspect;

import com.adminpro.core.base.entity.BaseEntity;
import com.adminpro.core.base.entity.BaseVO;
import com.adminpro.core.base.util.JsonUtil;
import com.adminpro.framework.common.annotation.SysLog;
import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.framework.security.auth.LoginUser;
import com.adminpro.rbac.api.LoginHelper;
import com.adminpro.tools.domains.entity.syslog.SysLogEntity;
import com.adminpro.tools.domains.entity.syslog.SysLogService;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;


/**
 * 系统日志，切面处理类
 */
@Aspect
@Component
public class SysLogAspect {
    @Lazy
    @Autowired(required = false)
    private SysLogService sysLogService;

    @Pointcut("@annotation(com.adminpro.framework.common.annotation.SysLog)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        //保存日志
        saveSysLog(point, time, result);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLogEntity sysLog = new SysLogEntity();
        SysLog syslog = method.getAnnotation(SysLog.class);
        if (syslog != null) {
            //注解上的描述
            sysLog.setDescription(syslog.value());
        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        if (ArrayUtils.isNotEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof BaseVO || args[i] instanceof BaseEntity) {
                    try {
                        String params = JsonUtil.toJson(args[i]);
                        params = filterSensitiveFields(params);
                        sysLog.setParams(params);
                        break;
                    } catch (Exception e) {

                    }
                }
            }
        }

        //获取request
        HttpServletRequest request = WebHelper.getHttpRequest();
        //设置IP地址
        sysLog.setIp(WebHelper.getIpAddr(request));
        sysLog.setBrowser(WebHelper.getBrowserInfo(request));
        sysLog.setOs(WebHelper.getOsInfo(request));
        //用户名
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser != null) {
            sysLog.setUserDomain(loginUser.getUserDomain());
            sysLog.setUserId(loginUser.getUsername());
        }
        sysLog.setTime(time);

        String responseJson = JsonUtil.toJson(result);
        responseJson = filterSensitiveFields(responseJson);
        sysLog.setResponse(responseJson);

        //保存系统日志
        if (sysLogService != null) {
            try {
                sysLogService.create(sysLog);
            } catch (Exception e) {
                // 忽略日志保存失败，避免影响主流程
            }
        }
    }

    /**
     * 过滤敏感字段
     *
     * @param jsonStr JSON字符串
     * @return 过滤后的JSON字符串
     */
    @SuppressWarnings("unchecked")
    private String filterSensitiveFields(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return jsonStr;
        }
        try {
            String[] excludeFields = ConfigHelper.getStringArray("app.log.exclude.fields");
            if (ArrayUtils.isEmpty(excludeFields)) {
                excludeFields = new String[]{"password", "pwd", "oldPwd", "newPwd", "confirmPwd", "confirmPassword", "confirmNewPwd", "payPwd"};
            }
            Map<String, Object> map = JsonUtil.fromJson(jsonStr, Map.class);
            if (map != null) {
                for (String field : excludeFields) {
                    map.remove(field);
                }
                return JsonUtil.toJson(map);
            }
        } catch (Exception e) {
            // 如果解析失败，返回原字符串
        }
        return jsonStr;
    }
}
