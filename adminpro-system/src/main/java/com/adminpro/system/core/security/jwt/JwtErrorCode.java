package com.adminpro.system.core.security.jwt;

import lombok.Getter;

/**
 * JWT 认证相关错误码
 * <p>
 * 统一定义 JWT 认证过程中的各种错误情况
 * </p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Getter
public enum JwtErrorCode {

    // ==================== 登录相关错误 (4xxx) ====================
    /**
     * 用户名或密码错误
     */
    LOGIN_FAILED(4001, "用户名或密码错误"),

    /**
     * 验证码错误或过期
     */
    CAPTCHA_INVALID(4002, "验证码错误或已过期"),

    /**
     * 账户已锁定
     */
    ACCOUNT_LOCKED(4003, "账户已锁定，请联系管理员"),

    /**
     * 账户已停用
     */
    ACCOUNT_DISABLED(4004, "账户已停用，请联系管理员"),

    /**
     * 设备数量超限
     */
    DEVICE_LIMIT_EXCEEDED(4005, "登录设备数量超限，请先登出其他设备"),

    /**
     * 参数错误
     */
    BAD_REQUEST(4006, "请求参数错误"),

    // ==================== Token 相关错误 (401x) ====================
    /**
     * Token 无效
     */
    TOKEN_INVALID(4011, "Token 无效"),

    /**
     * Token 已过期
     */
    TOKEN_EXPIRED(4012, "Token 已过期，请重新登录"),

    /**
     * Token 已被撤销
     */
    TOKEN_REVOKED(4013, "Token 已失效，请重新登录"),

    /**
     * Refresh Token 无效
     */
    REFRESH_TOKEN_INVALID(4014, "Refresh Token 无效，请重新登录"),

    /**
     * Refresh Token 已过期
     */
    REFRESH_TOKEN_EXPIRED(4015, "Refresh Token 已过期，请重新登录"),

    /**
     * Refresh Token 已被撤销
     */
    REFRESH_TOKEN_REVOKED(4016, "Refresh Token 已失效，请重新登录"),

    /**
     * 未提供认证信息
     */
    MISSING_TOKEN(4017, "请先登录"),

    // ==================== 系统相关错误 (5xxx) ====================
    /**
     * 缓存服务异常
     */
    CACHE_ERROR(5001, "缓存服务异常，请稍后重试"),

    /**
     * 数据库异常
     */
    DATABASE_ERROR(5002, "数据库异常，请稍后重试"),

    /**
     * 系统内部错误
     */
    INTERNAL_ERROR(5000, "系统内部错误，请稍后重试"),

    /**
     * 操作频繁
     */
    TOO_MANY_REQUESTS(5003, "操作过于频繁，请稍后重试");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误描述
     */
    private final String message;

    JwtErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取枚举
     *
     * @param code 错误码
     * @return 枚举实例，不存在返回 null
     */
    public static JwtErrorCode fromCode(int code) {
        for (JwtErrorCode errorCode : values()) {
            if (errorCode.code == code) {
                return errorCode;
            }
        }
        return null;
    }
}
