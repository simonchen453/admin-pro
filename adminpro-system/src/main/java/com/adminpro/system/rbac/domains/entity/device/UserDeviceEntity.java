package com.adminpro.system.rbac.domains.entity.device;

import com.adminpro.framework.base.entity.BaseAuditEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户设备实体
 * 
 * @author adminpro
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDeviceEntity extends BaseAuditEntity {

    public static final String TABLE_NAME = "sys_user_device";

    public static final String COL_ID = "id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_DEVICE_ID = "device_id";
    public static final String COL_PLATFORM = "platform";
    public static final String COL_DEVICE_NAME = "device_name";
    public static final String COL_REFRESH_TOKEN_JTI = "refresh_token_jti";
    public static final String COL_LAST_IP = "last_ip";
    public static final String COL_LAST_USER_AGENT = "last_user_agent";
    public static final String COL_LAST_ACTIVE_AT = "last_active_at";
    public static final String COL_IS_ACTIVE = "is_active";
    // BaseAuditEntity定义的常量值是"COL_CREATED_AT"，不是数据库列名，必须覆盖
    public static final String COL_CREATED_AT = "created_at";
    public static final String COL_UPDATED_AT = "updated_at";

    /**
     * ID (BaseAuditEntity 没有 id 属性吗? BaseEntity 有吗? 通常 BaseEntity 是泛型的或者有 ID)
     * 检查 BaseEntity 只有 public static final String COL_ID = "COL_ID"; 还是有字段？
     * 如果 BaseEntity 是泛型 ID，那么 Entity 应该有 ID 字段。
     * UserEntity 有 private String id.
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 客户端设备标识
     */
    private String deviceId;

    /**
     * 平台: web/mobile/miniprogram
     */
    private String platform;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 当前有效的 RT JTI
     */
    private String refreshTokenJti;

    /**
     * 最后活跃IP
     */
    private String lastIp;

    /**
     * 最后 User-Agent
     */
    private String lastUserAgent;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveAt;

    /**
     * 是否活跃
     */
    private Integer isActive;
}
