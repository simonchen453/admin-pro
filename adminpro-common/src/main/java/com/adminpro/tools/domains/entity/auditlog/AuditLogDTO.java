package com.adminpro.tools.domains.entity.auditlog;

import com.adminpro.framework.common.entity.BaseAuditDTO;
import lombok.Data;

/**
 * 日志类
 *
 * @author simon
 */
@Data
public class AuditLogDTO extends BaseAuditDTO {

    private static final long serialVersionUID = 1L;

    private String id;

    private String category;

    private String module;

    private String afterData;
    private String beforeData;

    private String ipAddress;

    private String status;

    private String event;

    private String sessionId;

    private String userName;

    private Long executionTime;
}
