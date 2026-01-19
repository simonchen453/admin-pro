package com.adminpro.system.rbac.domains.vo.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户设备 VO
 *
 * @author adminpro
 * @since 1.0.0
 */
@Data
@Schema(description = "用户设备信息")
public class UserDeviceVo {

    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "用户ID")
    private String userId;
    private String loginName;
    private String realName;
    private String userDomain;

    @Schema(description = "设备ID (唯一标识)")
    private String deviceId;

    @Schema(description = "平台 (Web/App/MiniProgram)")
    private String platform;

    @Schema(description = "设备名称 (浏览器/型号)")
    private String deviceName;

    @Schema(description = "最近登录IP")
    private String lastIp;

    @Schema(description = "最近活跃时间")
    private LocalDateTime lastActiveAt;

    @Schema(description = "刷新令牌ID")
    private String refreshTokenJti;

    @Schema(description = "最近UserAgent")
    private String lastUserAgent;

    @Schema(description = "是否活跃")
    private Integer isActive;

    @Schema(description = "是否当前设备")
    private Boolean isCurrent;
}
