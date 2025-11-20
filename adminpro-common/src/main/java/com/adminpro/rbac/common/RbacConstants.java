package com.adminpro.rbac.common;

import com.adminpro.framework.common.helper.ConfigHelper;

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
     */
    public static final String DEFAULT_NATION = ConfigHelper.getString("app.default.nation", "01");

    public static final String[] NEED_CHECK_CAPTURE_DOMAINS = ConfigHelper.getStringArray("app.check.capture.domains");

    /**
     * 默认的国家
     */
    public static final String DEFAULT_NATIONALITY = ConfigHelper.getString("app.default.nationality", "156");
    /**
     * 默认省
     */
    public static final String DEFAULT_PROVINCE = ConfigHelper.getString("app.default.province", "320000");
    /**
     * 默认市
     */
    public static final String DEFAULT_CITY = ConfigHelper.getString("app.default.city", "320500");
    /**
     * 默认区
     */
    public static final String DEFAULT_DISTRICT = ConfigHelper.getString("app.default.district", "320506");
    /**
     * 默认婚姻状态
     */
    public static final String DEFAULT_MARITALSTATUS = ConfigHelper.getString("app.default.maritalstatus", "10");
    /**
     * 默认证件类型
     */
    public static final String DEFAULT_CERTTYPE = ConfigHelper.getString("app.default.certtype", "1");
    /**
     * 默认身份类别
     */
    public static final String DEFAULT_IDTYPE = ConfigHelper.getString("app.default.idtype", "1");

    public static final String CLOUD_STORAGE_CONFIG = "ossconfig";

    public static final String UPLOAD_PATH = ConfigHelper.getString("app.upload.dir", "/upload/");

    public static final String DEPLOYMENT_MODE = ConfigHelper.getString("app.deployment.mode", "prod");
    public static final int AUTH_CODE_EXPIRE_PERIOD = ConfigHelper.getInt("app.auth.code.expire.period", 5);

    public static final String DEPT_SUPER_PARENT_ID = ConfigHelper.getString("app.dept.super.parent.id", "0");

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
