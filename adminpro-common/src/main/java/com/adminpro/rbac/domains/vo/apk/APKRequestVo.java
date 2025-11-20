package com.adminpro.rbac.domains.vo.apk;

import com.adminpro.core.base.entity.BaseVO;

public class APKRequestVo extends BaseVO {
    private String id;
    /**
     * APP类型
     */
    private String type;

    /**
     * 是否强制更新
     */
    private boolean forceUpdate;

    /**
     * version name
     */
    private String verName;

    /**
     * version code
     */
    private int verCode;

    /**
     * 系统版本
     */
    private String osVersion;

    /**
     * 下载地址
     */
    private String downloadUrl;
    private String ossId;
    /**
     * 升级说明
     */
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public int getVerCode() {
        return verCode;
    }

    public void setVerCode(int verCode) {
        this.verCode = verCode;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
