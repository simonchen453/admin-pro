package com.adminpro.rbac.common;

import com.adminpro.framework.common.helper.ConfigHelper;

import static com.adminpro.framework.common.constants.ConfigKeys.*;

/**
 * Created by simon on 2017/5/31.
 */
public class RbacConstants {
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";
    public static final String MENU_SESSION_KEY = "_m_id";

    public static final String RESOURCE_MENU = "menu";
    public static final String RESOURCE_PROCESS = "process";
    public static final String RESOURCE_MENU_DISPLAY = "Menu";
    public static final String RESOURCE_PROCESS_DISPLAY = "Process";

    public static final String SPRING_SECURITY_USERIDEN_SPLIT = "_";
    public static final String USER_PWD_FORMAT = "{0}_{1}_{2}";

    public static final String SYSTEM_DOMAIN = "system";
    public static final String INTERNET_DOMAIN = "internet";
    public static final String INTRANET_DOMAIN = "intranet";
    public static final String WX_DOMAIN = "wx";

    public static final String KILL_SESSION_WHEN_LOGIN = "ec.kill.usersession";

    public static final String USER_DEFAULT_PASSWORD = "password$1";

    /**
     * 默认的民族
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String getDefaultNation() {
        return ConfigHelper.getString(APP_DEFAULT_NATION, "01");
    }

    /**
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String[] getNeedCheckCaptureDomains() {
        return ConfigHelper.getStringArray(APP_CHECK_CAPTURE_DOMAINS);
    }

    /**
     * 默认的国家
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String getDefaultNationality() {
        return ConfigHelper.getString(APP_DEFAULT_NATIONALITY, "156");
    }

    /**
     * 默认省
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String getDefaultProvince() {
        return ConfigHelper.getString(APP_DEFAULT_PROVINCE, "320000");
    }

    /**
     * 默认市
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String getDefaultCity() {
        return ConfigHelper.getString(APP_DEFAULT_CITY, "320500");
    }

    /**
     * 默认区
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String getDefaultDistrict() {
        return ConfigHelper.getString(APP_DEFAULT_DISTRICT, "320506");
    }

    /**
     * 默认婚姻状态
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String getDefaultMaritalstatus() {
        return ConfigHelper.getString(APP_DEFAULT_MARITALSTATUS, "10");
    }

    /**
     * 默认证件类型
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String getDefaultCerttype() {
        return ConfigHelper.getString(APP_DEFAULT_CERTTYPE, "1");
    }

    /**
     * 默认身份类别
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String getDefaultIdtype() {
        return ConfigHelper.getString(APP_DEFAULT_IDTYPE, "1");
    }

    public static final String CLOUD_STORAGE_CONFIG = "ossconfig";

    /**
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String getUploadPath() {
        return ConfigHelper.getString(APP_UPLOAD_DIR, "/upload/");
    }

    /**
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String getDeploymentMode() {
        return ConfigHelper.getString(APP_DEPLOYMENT_MODE, "prod");
    }

    /**
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static int getAuthCodeExpirePeriod() {
        return ConfigHelper.getInt(APP_AUTH_CODE_EXPIRE_PERIOD, 5);
    }

    /**
     * 直接调用 ConfigHelper，利用其内部的缓存机制（@Cacheable）
     */
    public static String getDeptSuperParentId() {
        return ConfigHelper.getString(APP_DEPT_SUPER_PARENT_ID, "0");
    }


    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
