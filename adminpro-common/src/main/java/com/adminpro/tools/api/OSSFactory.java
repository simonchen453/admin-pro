package com.adminpro.tools.api;

import com.adminpro.rbac.common.RbacConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件上传Factory
 */
public final class OSSFactory {

    public static final String CLOUD_STORAGE_CONFIG_KEY = "app.cloud.storage.config";
    private static CloudStorageConfig config = null;
    private static final Logger logger = LoggerFactory.getLogger(OSSFactory.class);

    public static BaseCloudStorageService build() {
        //获取云存储配置信息


//        CloudStorageConfig config = SysConfigHelper.getInstance().getConfigObject(RbacConstants.CLOUD_STORAGE_CONFIG, CloudStorageConfig.class);

        try {
            config = init();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("初始化OSS配置：", e);
        }

        if (config.getType() == RbacConstants.CloudService.QINIU.getValue()) {
            return new QiniuCloudStorageService(config);
        } else if (config.getType() == RbacConstants.CloudService.ALIYUN.getValue()) {
            return new AliyunCloudStorageService(config);
        }/*else if(config.getType() == RbacConstants.CloudService.QCLOUD.getValue()){
            return new QcloudCloudStorageService(config);
        }*/

        return null;
    }

    private static CloudStorageConfig init() throws Exception {
        InputStream resourceAsStream = OSSFactory.class.getClassLoader().getResourceAsStream("oss.json");
        JsonParser parser = new JsonParser();  //创建JSON解析器
        BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"), 50 * 1024 * 1024); //设置缓冲区 编码
        JsonObject object = (JsonObject) parser.parse(in);  //创建JsonObject对象
        String aliyunAccessKeyId = object.get("aliyunAccessKeyId").getAsString();
        String aliyunAccessKeySecret = object.get("aliyunAccessKeySecret").getAsString();
        String aliyunBucketName = object.get("aliyunBucketName").getAsString();
        String aliyunDomain = object.get("aliyunDomain").getAsString();
        String aliyunEndPoint = object.get("aliyunEndPoint").getAsString();
        String aliyunPrefix = object.get("aliyunPrefix").getAsString();
        String qcloudBucketName = object.get("qcloudBucketName").getAsString();
        String qcloudDomain = object.get("qcloudDomain").getAsString();
        String qcloudPrefix = object.get("qcloudPrefix").getAsString();
        String qcloudSecretId = object.get("qcloudSecretId").getAsString();
        String qcloudSecretKey = object.get("qcloudSecretKey").getAsString();
        String qcloudRegion = object.get("qcloudRegion").getAsString();
        int qcloudAppId = object.get("qcloudAppId").getAsInt();
        String qiniuAccessKey = object.get("qiniuAccessKey").getAsString();
        String qiniuBucketName = object.get("qiniuBucketName").getAsString();
        String qiniuDomain = object.get("qiniuDomain").getAsString();
        String qiniuPrefix = object.get("qiniuPrefix").getAsString();
        String qiniuSecretKey = object.get("qiniuSecretKey").getAsString();
        int type = object.get("type").getAsInt();

        CloudStorageConfig cloudStorageConfig = new CloudStorageConfig();
        cloudStorageConfig.setType(type);
        cloudStorageConfig.setQiniuDomain(qiniuDomain);
        cloudStorageConfig.setQiniuPrefix(qiniuPrefix);
        cloudStorageConfig.setQiniuAccessKey(qiniuAccessKey);
        cloudStorageConfig.setQiniuSecretKey(qiniuSecretKey);
        cloudStorageConfig.setQiniuBucketName(qiniuBucketName);
        cloudStorageConfig.setAliyunDomain(aliyunDomain);
        cloudStorageConfig.setAliyunPrefix(aliyunPrefix);
        cloudStorageConfig.setAliyunEndPoint(aliyunEndPoint);
        cloudStorageConfig.setAliyunAccessKeyId(aliyunAccessKeyId);
        cloudStorageConfig.setAliyunAccessKeySecret(aliyunAccessKeySecret);
        cloudStorageConfig.setAliyunBucketName(aliyunBucketName);
        cloudStorageConfig.setQcloudDomain(qcloudDomain);
        cloudStorageConfig.setQcloudPrefix(qcloudPrefix);
        cloudStorageConfig.setQcloudAppId(qcloudAppId);
        cloudStorageConfig.setQcloudSecretId(qcloudSecretId);
        cloudStorageConfig.setQcloudSecretKey(qcloudSecretKey);
        cloudStorageConfig.setQcloudBucketName(qcloudBucketName);
        cloudStorageConfig.setQcloudRegion(qcloudRegion);
        return cloudStorageConfig;
    }
}
