package com.adminpro.system.core.common.helper;

import com.adminpro.system.rbac.api.LoginHelper;
import com.adminpro.system.tools.domains.entity.auditlog.AuditLogEntity;
import com.adminpro.system.tools.domains.entity.auditlog.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 审计日志辅助类
 * <p>
 * 本类提供系统审计日志的记录功能，用于追踪和记录系统中的关键操作。
 * 记录的信息包括：操作类别、模块、事件名称、状态、操作前数据、操作后数据、
 * 执行时间、IP地址、JWT Token ID等
 * <p>
 * 主要功能：
 * <ul>
 * <li>记录系统操作的审计日志</li>
 * <li>支持记录操作前后的数据变化</li>
 * <li>支持记录方法执行时间</li>
 * <li>自动记录请求来源IP和JWT Token信息</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 * <li>用户登录/登出记录</li>
 * <li>重要数据的增删改操作记录</li>
 * <li>系统异常操作审计</li>
 * <li>第三方接口调用记录</li>
 * </ul>
 * <p>
 * 注意：所有方法都是静态方法，日志记录失败不会抛出异常，只会记录错误日志
 */
public class AuditLogHelper {

    protected static Logger logger = LoggerFactory.getLogger(AuditLogHelper.class);

    /**
     * 操作状态：成功
     */
    public static String STATUS_SUCCESS = "success";
    /**
     * 操作状态：失败
     */
    public static String STATUS_FAIL = "fail";
    /**
     * 操作类别：普通操作
     */
    public static String CATEGORY_NORMAL = "Normal";
    /**
     * 操作类别：管理员操作
     */
    public static String CATEGORY_ADMIN = "Admin";
    /**
     * 操作类别：第三方操作
     */
    public static String CATEGORY_THIRDPART = "third-part";

    /**
     * 获取当前会话标识（JWT JTI）
     * <p>
     * JWT 认证模式下，返回 JWT Token 的唯一标识（JTI）
     * 如果无法获取（如未登录或Token解析失败），返回null
     * </p>
     *
     * @return JTI，无法获取时返回null
     */
    private static String getCurrentJti() {
        try {
            return LoginHelper.getInstance().getCurrentJti();
        } catch (Exception e) {
            logger.debug("获取 JTI 失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 记录审计日志（包含操作前后数据）
     * <p>
     * 记录完整的操作信息，包括操作前后的数据变化。
     * 自动获取当前请求的IP地址和JWT Token ID
     * <p>
     * 使用场景：
     * <ul>
     * <li>数据更新操作记录</li>
     * <li>需要追踪数据变化的场景</li>
     * <li>重要操作的完整审计</li>
     * </ul>
     *
     * @param category  操作类别，如：CATEGORY_ADMIN、CATEGORY_NORMAL
     * @param module    操作所属模块，如：user、role、menu
     * @param eventName 操作事件名称，如：create、update、delete
     * @param status    操作状态，如：STATUS_SUCCESS、STATUS_FAIL
     * @param before    操作前的数据（JSON格式），可为null
     * @param after     操作后的数据（JSON格式），可为null
     */
    public static void log(String category, String module, String eventName, String status, String before, String after) {
        logWithExecutionTime(category, module, eventName, status, before, after, null);
    }

    /**
     * 记录审计日志（包含操作前后数据和执行时间）
     * <p>
     * 记录完整的操作信息，包括操作前后的数据变化和方法执行时间。
     * 自动获取当前请求的IP地址和JWT Token ID
     * <p>
     * 使用场景：
     * <ul>
     * <li>需要性能监控的操作记录</li>
     * <li>慢查询分析</li>
     * <li>接口性能审计</li>
     * </ul>
     *
     * @param category      操作类别，如：CATEGORY_ADMIN、CATEGORY_NORMAL
     * @param module        操作所属模块，如：user、role、menu
     * @param eventName     操作事件名称，如：create、update、delete
     * @param status        操作状态，如：STATUS_SUCCESS、STATUS_FAIL
     * @param before        操作前的数据（JSON格式），可为null
     * @param after         操作后的数据（JSON格式），可为null
     * @param executionTime 执行时间（毫秒），如果为null则不设置
     */
    public static void logWithExecutionTime(String category, String module, String eventName, String status, String before, String after, Long executionTime) {

        try {
            AuditLogEntity auditLogEntity = new AuditLogEntity();
            auditLogEntity.setBeforeData(before);
            auditLogEntity.setAfterData(after);
            auditLogEntity.setEvent(eventName);
            auditLogEntity.setModule(module);
            auditLogEntity.setStatus(status);
            auditLogEntity.setCategory(category);
            if (executionTime != null) {
                auditLogEntity.setExecutionTime(executionTime);
            }
            //获取request
            HttpServletRequest request = WebHelper.getHttpRequest();
            if(request != null){
                auditLogEntity.setIpAddress(WebHelper.getIpAddr(request));
                String jti = getCurrentJti();
                if (jti != null) {
                    auditLogEntity.setJti(jti);
                }
            }

            AuditLogService.getInstance().create(auditLogEntity);
        } catch (Exception e) {
            logger.error("日志生成失败：", e);
        }
    }

    /**
     * 记录审计日志（仅包含操作后数据）
     * <p>
     * 记录操作信息，仅包含操作后的数据，不包含操作前数据。
     * 自动获取当前请求的IP地址和JWT Token ID
     * <p>
     * 使用场景：
     * <ul>
     * <li>新增操作记录</li>
     * <li>查询操作记录</li>
     * <li>不需要追踪数据变化的场景</li>
     * </ul>
     *
     * @param category  操作类别，如：CATEGORY_ADMIN、CATEGORY_NORMAL
     * @param module    操作所属模块，如：user、role、menu
     * @param eventName 操作事件名称，如：create、query
     * @param status    操作状态，如：STATUS_SUCCESS、STATUS_FAIL
     * @param after     操作后的数据（通常是请求参数），可为null
     */
    public static void log(String category, String module, String eventName, String status, String after) {
        logWithExecutionTime(category, module, eventName, status, null, after, null);
    }

    /**
     * 记录审计日志（包含操作后数据和执行时间）
     * <p>
     * 记录操作信息，包含操作后的数据和方法执行时间。
     * 自动获取当前请求的IP地址和JWT Token ID
     * <p>
     * 使用场景：
     * <ul>
     * <li>需要性能监控的新增操作</li>
     * <li>查询性能审计</li>
     * </ul>
     *
     * @param category      操作类别，如：CATEGORY_ADMIN、CATEGORY_NORMAL
     * @param module        操作所属模块，如：user、role、menu
     * @param eventName     操作事件名称，如：create、query
     * @param status        操作状态，如：STATUS_SUCCESS、STATUS_FAIL
     * @param after         操作后的数据（通常是请求参数），可为null
     * @param executionTime 执行时长（毫秒），如果为null则不设置
     */
    public static void logWithExecutionTime(String category, String module, String eventName, String status, String after, Long executionTime) {

        try {
            AuditLogEntity auditLogEntity = new AuditLogEntity();
            auditLogEntity.setAfterData(after);
            auditLogEntity.setEvent(eventName);
            auditLogEntity.setModule(module);
            auditLogEntity.setStatus(status);
            auditLogEntity.setCategory(category);
            if (executionTime != null) {
                auditLogEntity.setExecutionTime(executionTime);
            }
            //获取request
            HttpServletRequest request = WebHelper.getHttpRequest();
            if(request != null) {
                auditLogEntity.setIpAddress(WebHelper.getIpAddr(request));
                String jti = getCurrentJti();
                if (jti != null) {
                    auditLogEntity.setJti(jti);
                }
            }
            AuditLogService.getInstance().create(auditLogEntity);
        } catch (Exception e) {
            logger.error("日志生成失败：", e);
        }
    }

    /**
     * 从当前请求中生成参数Map
     * <p>
     * 获取当前HTTP请求的所有参数，并封装为Map返回
     * <p>
     * 使用场景：
     * <ul>
     * <li>记录请求参数到审计日志</li>
     * <li>获取所有请求参数进行处理</li>
     * </ul>
     *
     * @return 包含所有请求参数的Map，key为参数名，value为参数值
     */
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
