package com.adminpro.system.core.security.jwt;

import lombok.Getter;

/**
 * JWT 认证相关错误码
 * <p>
 * 统一定义 JWT 认证过程中的各种错误情况。
 * HTTP 状态码始终返回 200，通过 restCode 返回标准 HTTP 状态码 (401, 403, 500 等)。
 * 用户看到的是通用友好信息，真实原因仅记录在服务端日志中。
 * </p>
 *
 * @author adminpro
 * @since 2.0.0
 */
@Getter
public enum JwtErrorCode {

    // ==================== 认证失败 (401) ====================
    /**
     * 用户名或密码错误
     */
    LOGIN_FAILED(401, "LOGIN_FAILED", "用户名或密码错误", "用户名或密码错误"),

    /**
     * 验证码错误或过期
     */
    CAPTCHA_INVALID(401, "CAPTCHA_INVALID", "用户名或密码错误", "验证码错误或已过期"),

    /**
     * 账户已锁定
     */
    ACCOUNT_LOCKED(401, "ACCOUNT_LOCKED", "用户名或密码错误", "账户已锁定"),

    /**
     * 账户已停用
     */
    ACCOUNT_DISABLED(401, "ACCOUNT_DISABLED", "用户名或密码错误", "账户已停用"),

    /**
     * 设备数量超限
     */
    DEVICE_LIMIT_EXCEEDED(401, "DEVICE_LIMIT_EXCEEDED", "用户名或密码错误", "登录设备数量超限"),

    /**
     * Token 无效
     */
    TOKEN_INVALID(401, "TOKEN_INVALID", "请重新登录", "Token 无效"),

    /**
     * Token 已过期
     */
    TOKEN_EXPIRED(401, "TOKEN_EXPIRED", "请重新登录", "Token 已过期"),

    /**
     * Token 已被撤销
     */
    TOKEN_REVOKED(401, "TOKEN_REVOKED", "请重新登录", "Token 已被撤销"),

    /**
     * Refresh Token 无效
     */
    REFRESH_TOKEN_INVALID(401, "REFRESH_TOKEN_INVALID", "请重新登录", "Refresh Token 无效"),

    /**
     * Refresh Token 已过期
     */
    REFRESH_TOKEN_EXPIRED(401, "REFRESH_TOKEN_EXPIRED", "请重新登录", "Refresh Token 已过期"),

    /**
     * Refresh Token 已被撤销
     */
    REFRESH_TOKEN_REVOKED(401, "REFRESH_TOKEN_REVOKED", "请重新登录", "Refresh Token 已被撤销"),

    /**
     * 未提供认证信息
     */
    MISSING_TOKEN(401, "MISSING_TOKEN", "请先登录", "未提供认证信息"),

    // ==================== 参数错误 (400) ====================
    /**
     * 参数错误
     */
    BAD_REQUEST(400, "BAD_REQUEST", "请求参数错误", "请求参数错误"),

    // ==================== 权限不足 (403) ====================
    /**
     * 无权限访问
     */
    FORBIDDEN(403, "FORBIDDEN", "无权限访问", "无权限访问"),

    // ==================== 系统错误 (500) ====================
    /**
     * 缓存服务异常
     */
    CACHE_ERROR(500, "CACHE_ERROR", "系统繁忙，请稍后重试", "缓存服务异常"),

    /**
     * 数据库异常
     */
    DATABASE_ERROR(500, "DATABASE_ERROR", "系统繁忙，请稍后重试", "数据库异常"),

    /**
     * 系统内部错误
     */
    INTERNAL_ERROR(500, "INTERNAL_ERROR", "系统繁忙，请稍后重试", "系统内部错误"),

    // ==================== 请求限制 (429) ====================
    /**
     * 操作频繁
     */
    TOO_MANY_REQUESTS(429, "TOO_MANY_REQUESTS", "操作过于频繁，请稍后重试", "操作过于频繁");

    /**
     * HTTP 状态码 (作为 restCode 返回给前端)
     */
    private final int code;

    /**
     * 内部错误标识 (用于日志和调试)
     */
    private final String internalCode;

    /**
     * 用户友好的错误消息 (返回给前端)
     */
    private final String message;

    /**
     * 真实错误描述 (仅用于服务端日志)
     */
    private final String logMessage;

    JwtErrorCode(int code, String internalCode, String message, String logMessage) {
        this.code = code;
        this.internalCode = internalCode;
        this.message = message;
        this.logMessage = logMessage;
    }

    /**
     * 根据内部错误标识获取枚举
     *
     * @param internalCode 内部错误标识
     * @return 枚举实例，不存在返回 null
     */
    public static JwtErrorCode fromInternalCode(String internalCode) {
        for (JwtErrorCode errorCode : values()) {
            if (errorCode.internalCode.equals(internalCode)) {
                return errorCode;
            }
        }
        return null;
    }
}
