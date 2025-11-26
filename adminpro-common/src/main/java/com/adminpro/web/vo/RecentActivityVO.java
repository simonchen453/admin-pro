package com.adminpro.web.vo;

import com.adminpro.core.base.entity.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 最近活动 VO
 *
 * @author optimization
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RecentActivityVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID
     */
    private String id;

    /**
     * 活动类型：login/operation/system
     */
    private String type;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动时间
     */
    private Date time;

    /**
     * 用户名称
     */
    private String user;
}

