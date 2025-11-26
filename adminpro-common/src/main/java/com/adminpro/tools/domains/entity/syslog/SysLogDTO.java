package com.adminpro.tools.domains.entity.syslog;

import com.adminpro.framework.common.entity.BaseAuditDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author simon
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysLogDTO extends BaseAuditDTO {
    /**
     * ID
     */
    private String id;
    /**
     * 用户域
     */
    private String userDomain;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户显示
     */
    private String loginName;
    /**
     * 用户IP
     */
    private String ip;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 访问Request Method
     */
    private String method;
    /**
     * 操作系统
     */
    private String os;
    /**
     * 操作描述
     */
    private String description;
    /**
     * 访问参数
     */
    private String params;
    /**
     * Response
     */
    private String response;
    /**
     * 消耗时间
     */
    private Long time;
    /**
     * 分类
     */
    private String category;
    /**
     * 模块
     */
    private String module;
    /**
     * 状态
     */
    private String status;
}
