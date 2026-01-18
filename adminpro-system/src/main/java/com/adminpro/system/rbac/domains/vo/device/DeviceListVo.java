package com.adminpro.system.rbac.domains.vo.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备列表 VO
 *
 * @author adminpro
 * @since 1.0.0
 */
@Data
@Schema(description = "设备列表信息")
public class DeviceListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID（设备指纹）")
    private String id;

    @Schema(description = "平台类型: web/mobile/miniprogram")
    private String platform;

    @Schema(description = "设备名称（如: Chrome on Windows）")
    private String deviceName;

    @Schema(description = "最后活跃IP")
    private String ip;

    @Schema(description = "最后活跃时间")
    private LocalDateTime lastActiveAt;

    @Schema(description = "是否为当前设备")
    private Boolean isCurrent;
}
