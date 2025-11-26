package com.adminpro.framework.common.constants;

/**
 * 配置 Key 常量类
 * 统一管理所有配置项的 key，避免硬编码字符串
 * 
 * @author optimization
 */
public class ConfigKeys {
    
    // ========== 应用基础配置 ==========
    /** 服务器地址 */
    public static final String APP_SERVER_ADDRESS = "app.server.address";
    
    /** 部署模式 */
    public static final String APP_DEPLOYMENT_MODE = "app.deployment.mode";
    
    /** 上传目录 */
    public static final String APP_UPLOAD_DIR = "app.upload.dir";
    
    /** 锁定消息 */
    public static final String APP_LOCK_BLOCK_MESSAGE = "app.lock.block.message";
    
    // ========== 用户相关配置 ==========
    /** 用户头像目录 */
    public static final String APP_USER_AVATAR_DIR = "app.user.avatar.dir";
    
    /** 用户头像 URL */
    public static final String APP_USER_AVATAR_URL = "app.user.avatar.url";
    
    // ========== 默认值配置 ==========
    /** 默认民族 */
    public static final String APP_DEFAULT_NATION = "app.default.nation";
    
    /** 默认国家 */
    public static final String APP_DEFAULT_NATIONALITY = "app.default.nationality";
    
    /** 默认省 */
    public static final String APP_DEFAULT_PROVINCE = "app.default.province";
    
    /** 默认市 */
    public static final String APP_DEFAULT_CITY = "app.default.city";
    
    /** 默认区 */
    public static final String APP_DEFAULT_DISTRICT = "app.default.district";
    
    /** 默认婚姻状态 */
    public static final String APP_DEFAULT_MARITALSTATUS = "app.default.maritalstatus";
    
    /** 默认证件类型 */
    public static final String APP_DEFAULT_CERTTYPE = "app.default.certtype";
    
    /** 默认身份类别 */
    public static final String APP_DEFAULT_IDTYPE = "app.default.idtype";
    
    // ========== 部门相关配置 ==========
    /** 部门超级父级 ID */
    public static final String APP_DEPT_SUPER_PARENT_ID = "app.dept.super.parent.id";
    
    // ========== 验证码相关配置 ==========
    /** 验证码过期时间（分钟） */
    public static final String APP_AUTH_CODE_EXPIRE_PERIOD = "app.auth.code.expire.period";
    
    /** 需要检查验证码的域名 */
    public static final String APP_CHECK_CAPTURE_DOMAINS = "app.check.capture.domains";
    
    // ========== OSS 相关配置 ==========
    /** OSS 获取帧 */
    public static final String APP_OSS_FETCH_FRAME = "app.oss.fetch.frame";
    
    // ========== 缓存相关配置 ==========
    /** 缓存 TTL 列表 */
    public static final String APP_CACHE_TTLS = "app.cache.ttls";
    
    // ========== APK 相关配置 ==========
    /** APK 默认操作系统版本 */
    public static final String APK_OS_VERSION_DEFAULT = "apk.os.version.default";
    
    // ========== Quartz 调度器配置 ==========
    /** Quartz JobStore 驱动委托类 */
    public static final String QUARTZ_JOBSTORE_DRIVER_DELEGATE_CLASS = "org.quartz.jobStore.driverDelegateClass";
    
    /** Quartz 调度器实例名称 */
    public static final String QUARTZ_SCHEDULER_INSTANCE_NAME = "org.quartz.scheduler.instanceName";
    
    /** Quartz 调度器实例 ID */
    public static final String QUARTZ_SCHEDULER_INSTANCE_ID = "org.quartz.scheduler.instanceId";
    
    /** Quartz 线程池类 */
    public static final String QUARTZ_THREADPOOL_CLASS = "org.quartz.threadPool.class";
    
    /** Quartz 线程池线程数 */
    public static final String QUARTZ_THREADPOOL_THREAD_COUNT = "org.quartz.threadPool.threadCount";
    
    /** Quartz 线程池线程优先级 */
    public static final String QUARTZ_THREADPOOL_THREAD_PRIORITY = "org.quartz.threadPool.threadPriority";
    
    /** Quartz 线程池继承上下文类加载器 */
    public static final String QUARTZ_THREADPOOL_THREADS_INHERIT_CONTEXT_CLASSLOADER = "org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread";
    
    /** Quartz JobStore 类 */
    public static final String QUARTZ_JOBSTORE_CLASS = "org.quartz.jobStore.class";
    
    /** Quartz JobStore 是否集群 */
    public static final String QUARTZ_JOBSTORE_IS_CLUSTERED = "org.quartz.jobStore.isClustered";
    
    /** Quartz JobStore 集群检查间隔 */
    public static final String QUARTZ_JOBSTORE_CLUSTER_CHECKIN_INTERVAL = "org.quartz.jobStore.clusterCheckinInterval";
    
    /** Quartz JobStore 最大失火处理数 */
    public static final String QUARTZ_JOBSTORE_MAX_MISFIRES_TO_HANDLE_AT_A_TIME = "org.quartz.jobStore.maxMisfiresToHandleAtATime";
    
    /** Quartz JobStore 失火阈值 */
    public static final String QUARTZ_JOBSTORE_MISFIRE_THRESHOLD = "org.quartz.jobStore.misfireThreshold";
    
    /** Quartz JobStore 表前缀 */
    public static final String QUARTZ_JOBSTORE_TABLE_PREFIX = "org.quartz.jobStore.tablePrefix";
    
    /** Quartz JobStore 数据源 */
    public static final String QUARTZ_JOBSTORE_DATASOURCE = "org.quartz.jobStore.dataSource";
    
    /** Quartz JobStore 使用属性 */
    public static final String QUARTZ_JOBSTORE_USE_PROPERTIES = "org.quartz.jobStore.useProperties";
    
    // 私有构造函数，防止实例化
    private ConfigKeys() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}

