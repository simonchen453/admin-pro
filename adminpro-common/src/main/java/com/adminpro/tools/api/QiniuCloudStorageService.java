package com.adminpro.tools.api;

import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.tools.domains.entity.oss.OSSEntity;
import com.adminpro.tools.domains.enums.OSSStatus;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Region;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 七牛云存储
 */
public class QiniuCloudStorageService extends BaseCloudStorageService {
    private UploadManager uploadManager;
    private BucketManager bucketManager;
    private String token;

    protected static final Logger logger = LoggerFactory.getLogger(QiniuCloudStorageService.class);

    public QiniuCloudStorageService(CloudStorageConfig config) {
        this.config = config;

        //初始化
        init();
    }

    private void init() {
        Configuration config = new Configuration(Region.autoRegion());
        uploadManager = new UploadManager(config);
        Auth auth = Auth.create(this.config.getQiniuAccessKey(), this.config.getQiniuSecretKey());
        token = auth.uploadToken(this.config.getQiniuBucketName());
        bucketManager = new BucketManager(auth, config);
    }

    @Override
    public boolean delete(String key) {
        try {
            bucketManager.delete(this.config.getQiniuBucketName(), key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            logger.error("删除文件失败，请核对七牛配置信息", ex);
            return false;
        }

        return true;
    }

    @Override
    public OSSEntity uploadSuffix(byte[] data, String originalFilename, String suffix) {
        String path = getPath(config.getQiniuPrefix(), suffix);
        try {
            Response res = uploadManager.put(data, path, token);
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res.toString());
            }
            OSSEntity entity = new OSSEntity();

            DefaultPutRet putRet = new Gson().fromJson(res.bodyString(), DefaultPutRet.class);
            entity.setSize(data.length);
            entity.setUrl(config.getQiniuDomain() + "/" + path);
            String hash = putRet.hash;
            String key = putRet.key;
            if (StringUtils.isNotEmpty(suffix) && suffix.length() > 1) {
                entity.setSuffix(suffix.substring(1));
            }
            entity.setOriginal(originalFilename);
            entity.setKey(key);
            entity.setHash(hash);
            entity.setStatus(OSSStatus.TEMP.getCode());
            return entity;
        } catch (Exception e) {
            throw new BaseRuntimeException("上传文件失败，请核对七牛配置信息", e);
        }
    }
}
