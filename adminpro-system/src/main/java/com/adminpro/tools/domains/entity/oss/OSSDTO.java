package com.adminpro.tools.domains.entity.oss;

import com.adminpro.core.base.entity.BaseDTO;

/**
 * @author simon
 */
public class OSSDTO extends BaseDTO {
    /**
     * ID
     */
    private String id;

    private String batchId;
    /**
     * 类型：图片、视频、文件
     */
    private String type;
    /**
     * 缩略图（当类型是视频的时候保存）
     */
    private String cover;
    /**
     * URL地址
     */
    private String url;
    /**
     * 后缀
     */
    private String suffix;
    /**
     * 状态
     */
    private String status;
    /**
     * 文件大小
     */
    private long size;

    public static OSSDTO from(OSSEntity entity) {
        OSSDTO dto = new OSSDTO();
        dto.setId(entity.getId());
        dto.setUrl(entity.getUrl());
        dto.setType(entity.getType());
        dto.setSize(entity.getSize());
        dto.setBatchId(entity.getBatchId());
        dto.setSuffix(entity.getSuffix());
        dto.setCover(entity.getCover());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
