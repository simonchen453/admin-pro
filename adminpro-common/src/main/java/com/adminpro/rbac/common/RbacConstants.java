package com.adminpro.rbac.common;

import com.adminpro.framework.common.helper.ConfigHelper;

import static com.adminpro.framework.common.constants.ConfigKeys.App;
import static com.adminpro.framework.common.constants.ConfigKeys.Auth;
import static com.adminpro.framework.common.constants.ConfigKeys.Default;
import static com.adminpro.framework.common.constants.ConfigKeys.Dept;

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

    // ========== 默认值配置 ==========
    
    /**
     * 获取默认民族代码
     * 
     * @return 默认民族代码，默认值 "01"
     */
    public static String getDefaultNation() {
        return ConfigHelper.getString(Default.NATION, "01");
    }

    /**
     * 获取默认国家代码
     * 
     * @return 默认国家代码，默认值 "156"
     */
    public static String getDefaultNationality() {
        return ConfigHelper.getString(Default.NATIONALITY, "156");
    }

    /**
     * 获取默认省份代码
     * 
     * @return 默认省份代码，默认值 "320000"
     */
    public static String getDefaultProvince() {
        return ConfigHelper.getString(Default.PROVINCE, "320000");
    }

    /**
     * 获取默认城市代码
     * 
     * @return 默认城市代码，默认值 "320500"
     */
    public static String getDefaultCity() {
        return ConfigHelper.getString(Default.CITY, "320500");
    }

    /**
     * 获取默认区县代码
     * 
     * @return 默认区县代码，默认值 "320506"
     */
    public static String getDefaultDistrict() {
        return ConfigHelper.getString(Default.DISTRICT, "320506");
    }

    /**
     * 获取默认婚姻状态
     * 
     * @return 默认婚姻状态，默认值 "10"
     */
    public static String getDefaultMaritalstatus() {
        return ConfigHelper.getString(Default.MARITALSTATUS, "10");
    }

    /**
     * 获取默认证件类型
     * 
     * @return 默认证件类型，默认值 "1"
     */
    public static String getDefaultCerttype() {
        return ConfigHelper.getString(Default.CERTTYPE, "1");
    }

    /**
     * 获取默认身份类别
     * 
     * @return 默认身份类别，默认值 "1"
     */
    public static String getDefaultIdtype() {
        return ConfigHelper.getString(Default.IDTYPE, "1");
    }

    // ========== 应用配置 ==========
    
    /**
     * 获取文件上传目录
     * 
     * @return 上传目录路径，默认值 "/upload/"
     */
    public static String getUploadPath() {
        return ConfigHelper.getString(App.UPLOAD_DIR, "/upload/");
    }

    /**
     * 获取部署模式
     * 
     * @return 部署模式（dev/prod），默认值 "prod"
     */
    public static String getDeploymentMode() {
        return ConfigHelper.getString(App.DEPLOYMENT_MODE, "prod");
    }

    // ========== 部门配置 ==========
    
    /**
     * 获取部门超级父级 ID（根部门 ID）
     * 
     * @return 根部门 ID，默认值 "0"
     */
    public static String getDeptSuperParentId() {
        return ConfigHelper.getString(Dept.SUPER_PARENT_ID, "0");
    }

    // ========== 验证码配置 ==========
    
    /**
     * 获取验证码过期时间（单位：分钟）
     * 
     * @return 过期时间，默认值 5 分钟
     */
    public static int getAuthCodeExpirePeriod() {
        return ConfigHelper.getInt(Auth.CODE_EXPIRE_PERIOD, 5);
    }

    /**
     * 获取需要检查验证码的域名列表
     * 
     * @return 域名数组，从配置中读取（逗号分隔的字符串会被转换为数组）
     */
    public static String[] getNeedCheckCaptureDomains() {
        return ConfigHelper.getStringArray(Auth.CHECK_CAPTURE_DOMAINS);
    }

    public static final String CLOUD_STORAGE_CONFIG = "ossconfig";


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
