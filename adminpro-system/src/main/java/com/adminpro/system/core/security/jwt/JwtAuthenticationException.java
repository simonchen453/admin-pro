package com.adminpro.system.core.security.jwt;

import lombok.Getter;

/**
 * JWT 认证异常
 * <p>
 * 用于 JWT 认证过程中的各种异常情况
 * </p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Getter
public class JwtAuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final JwtErrorCode errorCode;

    /**
     * HTTP 状态码
     */
    private final int httpStatus;

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     */
    public JwtAuthenticationException(JwtErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = determineHttpStatus(errorCode);
    }

    /**
     * 构造函数（带自定义消息）
     *
     * @param errorCode 错误码
     * @param message   自定义错误消息
     */
    public JwtAuthenticationException(JwtErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = determineHttpStatus(errorCode);
    }

    /**
     * 构造函数（带原因）
     *
     * @param errorCode 错误码
     * @param cause     原始异常
     */
    public JwtAuthenticationException(JwtErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.httpStatus = determineHttpStatus(errorCode);
    }

    /**
     * 根据错误码确定 HTTP 状态码
     */
    private int determineHttpStatus(JwtErrorCode errorCode) {
        int code = errorCode.getCode();
        if (code >= 4001 && code < 5000) {
            return 401; // 认证失败
        } else if (code >= 5000) {
            return 500; // 服务器错误
        }
        return 400; // 默认客户端错误
    }
}
