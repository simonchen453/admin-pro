package com.adminpro.system.rbac.domains.entity.jwt;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Refresh Token 缓存数据结构
 * 
 * @author adminpro
 * @since 1.0.0
 */
@Data
public class RefreshTokenData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户域
     */
    private String userDomain;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 平台: web/mobile/miniprogram
     */
    private String platform;

    /**
     * 设备标识
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 登录IP
     */
    private String ip;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsedAt;

    /**
     * 是否记住登录
     */
    private boolean rememberMe;
}
