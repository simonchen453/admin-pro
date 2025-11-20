package com.adminpro.framework.common.aspect;

import com.adminpro.core.base.entity.BaseVO;
import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.util.ConfigUtil;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.base.util.JsonUtil;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.rbac.api.LoginHelper;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * 系统日志，切面处理类
 *
 * @author simon
 */
@Aspect
@Component
public class RestControllerLogAspect {
    private static Logger loggerDebug = LoggerFactory.getLogger("com.adminpro.debug");
    private static Logger loggerPerformance = LoggerFactory.getLogger("com.adminpro.performance");

    @Around("within((@org.springframework.web.bind.annotation.RestController *) && (" +
            "@org.springframework.web.bind.annotation.RequestMapping * " +
            "|| @org.springframework.web.bind.annotation.GetMapping *" +
            "|| @org.springframework.web.bind.annotation.PostMapping *" +
            "|| @org.springframework.web.bind.annotation.PutMapping *" +
            "|| @org.springframework.web.bind.annotation.PatchMapping *" +
            "))")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        //输出日志
        try {
            log(point, result, time);
        } catch (Exception e) {
        }

        return result;
    }

    private void log(ProceedingJoinPoint joinPoint, Object result, long time) {

        String id = IdGenerator.getInstance().nextStringId();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        loggerDebug.debug("[" + id + "] " + "=======start=============================================");
        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        loggerDebug.debug("[" + id + "] " + className + "." + methodName + "()");

        //获取request
        HttpServletRequest request = WebHelper.getHttpRequest();

        String requestURI = request.getRequestURI();
        loggerDebug.debug("[" + id + "] " + "uri: " + requestURI);
        loggerDebug.debug("[" + id + "] " + "query: " + request.getQueryString());
        loggerDebug.debug("[" + id + "] " + "method: " + request.getMethod());
        Object[] args = joinPoint.getArgs();
        if (ArrayUtils.isNotEmpty(args)) {
            String[] stringArray = ConfigUtil.getStringArray("app.log.exclude.fields");
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof BaseVO) {
                    try {
                        String params = JsonUtil.toJson(args[i]);
                        //登录密码不能打印在日志里面
                        Map map = JsonUtil.fromJson(params, Map.class);
                        for (int j = 0; j < stringArray.length; j++) {
                            map.remove(stringArray[j]);
                        }

                        params = JsonUtil.toJson(map);
                        loggerDebug.debug("[" + id + "] " + "params: " + params);
                        break;
                    } catch (Exception e) {

                    }
                }
            }
        }

        //设置IP地址
        loggerDebug.debug("[" + id + "] " + "ip: " + WebHelper.getIpAddr(request));

        //用户名
        UserEntity userEntity = LoginHelper.getInstance().getUserEntity();
        if (userEntity != null) {
            loggerDebug.debug("[" + id + "] " + "user domain: " + userEntity.getUserDomain());
            loggerDebug.debug("[" + id + "] " + "user id: " + userEntity.getUserId());
            if (StringUtils.isNotEmpty(userEntity.getDisplay())) {
                loggerDebug.debug("[" + id + "] " + "user name: " + userEntity.getDisplay());
            }
            loggerDebug.debug("[" + id + "] " + "token: " + LoginHelper.getInstance().getAuthToken());
        }
        if (result != null) {
            try {
                if (result instanceof R) {
                    String res = new Gson().toJson(result);
                    loggerDebug.debug("[" + id + "] " + "返回值: " + res);
                }
            } catch (Exception e) {

            }
        } else {
            loggerDebug.debug("[" + id + "] " + "返回值: void");
        }
        loggerPerformance.debug("[" + id + "] " + "uri: " + requestURI + ", time: " + time);
        loggerDebug.debug("[" + id + "] " + "=======end===============================================");
    }


}
