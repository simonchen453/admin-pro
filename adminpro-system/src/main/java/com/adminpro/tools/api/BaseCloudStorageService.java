package com.adminpro.tools.api;

import com.adminpro.core.base.util.DateUtil;
import com.adminpro.tools.domains.entity.oss.OSSEntity;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.UUID;

/**
 * 云存储(支持七牛、阿里云、腾讯云、又拍云)
 */
public abstract class BaseCloudStorageService {
    /**
     * 云存储配置信息
     */
    CloudStorageConfig config;

    /**
     * 文件路径
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    public String getPath(String prefix, String suffix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //文件路径
        String path = DateUtil.formatDate(new Date(), "yyyyMMdd") + "/" + uuid;

        if (StringUtils.isNotBlank(prefix)) {
            path = prefix + "/" + path;
        }

        return path + suffix;
    }

    /**
     * 文件上传
     *
     * @param data   文件字节数组
     * @param suffix 后缀
     * @return 返回http地址
     */
    public abstract OSSEntity uploadSuffix(byte[] data, String originalFilename, String suffix);

    public abstract boolean delete(String key);
}
