package com.adminpro.tools.api;

import com.adminpro.core.base.util.FileUtil;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.tools.domains.entity.oss.OSSEntity;
import com.adminpro.tools.domains.entity.oss.OSSService;
import com.adminpro.tools.domains.enums.OSSStatus;
import com.aliyun.oss.OSSClient;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;

/**
 * 阿里云存储
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-26 16:22
 */
public class AliyunCloudStorageService extends BaseCloudStorageService {
    private OSSClient client;

    public AliyunCloudStorageService(CloudStorageConfig config) {
        this.config = config;
        //初始化
        init();
    }

    private void init() {
        client = new OSSClient(config.getAliyunEndPoint(), config.getAliyunAccessKeyId(),
                config.getAliyunAccessKeySecret());
    }

    @Override
    public OSSEntity uploadSuffix(byte[] data, String originalFilename, String suffix) {
        String path = getPath(config.getAliyunPrefix(), suffix);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        try {
            client.putObject(config.getAliyunBucketName(), path, inputStream);
        } catch (Exception e) {
            throw new BaseRuntimeException("上传文件失败，请检查配置信息", e);
        }
        String url = config.getAliyunDomain() + "/" + path;

        OSSEntity entity = new OSSEntity();

        entity.setSize(data.length);
        entity.setUrl(url);
        String hash = FileUtil.md5HashCode(inputStream);
        String key = suffix;
        if (StringUtils.isNotEmpty(suffix) && suffix.length() > 1) {
            entity.setSuffix(suffix.substring(1));
        }
        entity.setOriginal(originalFilename);
        entity.setKey(key);
        entity.setHash(hash);
        entity.setStatus(OSSStatus.TEMP.getCode());
        OSSService.getInstance().create(entity);
        return entity;
    }

    @Override
    public boolean delete(String key) {
        client.deleteObject(config.getAliyunBucketName(), key);
        return false;
    }
}
