package com.adminpro.framework.common.helper;

import com.adminpro.tools.domains.entity.auditlog.AuditLogEntity;
import com.adminpro.tools.domains.entity.auditlog.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class AuditLogHelper {

    protected static Logger logger = LoggerFactory.getLogger(AuditLogHelper.class);

    public static String STATUS_SUCCESS = "success";
    public static String STATUS_FAIL = "fail";
    public static String CATEGORY_NORMAL = "Normal";
    public static String CATEGORY_ADMIN = "Admin";
    public static String CATEGORY_THIRDPART = "third-part";

    /**
     *
     * @param category
     * @param module
     * @param eventName
     * @param status
     * @param before
     * @param after
     */
    public static void log(String category, String module, String eventName, String status, String before, String after) {

        try {
            AuditLogEntity auditLogEntity = new AuditLogEntity();
            auditLogEntity.setBeforeData(before);
            auditLogEntity.setAfterData(after);
            auditLogEntity.setEvent(eventName);
            auditLogEntity.setModule(module);
            auditLogEntity.setLogDate(new Date());
            auditLogEntity.setStatus(status);
            auditLogEntity.setCategory(category);
            //获取request
            HttpServletRequest request = WebHelper.getHttpRequest();
            if(request != null){
                auditLogEntity.setIpAddress(WebHelper.getIpAddr(request));
                auditLogEntity.setSessionId(request.getSession().getId());
            }

            AuditLogService.getInstance().create(auditLogEntity);
        } catch (Exception e) {
            logger.error("日志生成失败：", e);
        }
    }

    /**
     * @param after    params from request
     * @param eventName
     * @param module
     * @param status    success--failed
     * @param category  admin -- normal
     */
    public static void log(String category, String module, String eventName, String status, String after) {

        try {
            AuditLogEntity auditLogEntity = new AuditLogEntity();
            auditLogEntity.setAfterData(after);
            auditLogEntity.setEvent(eventName);
            auditLogEntity.setModule(module);
            auditLogEntity.setLogDate(new Date());
            auditLogEntity.setStatus(status);
            auditLogEntity.setCategory(category);
            //获取request
            HttpServletRequest request = WebHelper.getHttpRequest();
            if(request != null) {
                auditLogEntity.setIpAddress(WebHelper.getIpAddr(request));
                auditLogEntity.setSessionId(request.getSession().getId());
            }
            AuditLogService.getInstance().create(auditLogEntity);
        } catch (Exception e) {
            logger.error("日志生成失败：", e);
        }
    }

    public static Map<String, Object> generateParamsMapFromRequest() {
        Map<String, Object> paramsMap = new HashMap<>();
        HttpServletRequest request = WebHelper.getHttpRequest();
        Enumeration<String> parametes = request.getParameterNames();
        if (parametes != null) {
            while (parametes.hasMoreElements()) {
                String key = parametes.nextElement();
                paramsMap.put(key, request.getParameter(key));
            }
        }
        return paramsMap;
    }
}
