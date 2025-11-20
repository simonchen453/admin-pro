package com.adminpro.rbac.domains.vo.oss;

import lombok.Data;

/**
 * @author simon
 */
@Data
public class FileUploadVo {
    /**
     * 相对路径
     */
    private String relativePath;
    /**
     * 绝对路径
     */
    private String absolutePath;
}
