package com.adminpro.framework.common.constants;

/**
 * 配置 Key 常量类
 * <p>
 * 统一管理所有配置项的 key，避免硬编码字符串。
 * 使用内部类按功能模块分组，提高代码可读性和可维护性。
 * </p>
 * 
 * <p>使用示例：</p>
 * <pre>
 * import static com.adminpro.framework.common.constants.ConfigKeys.*;
 * 
 * ConfigHelper.getString(App.SERVER_ADDRESS);
 * ConfigHelper.getString(User.AVATAR_DIR, "/avatar/");
 * </pre>
 * 
 * @author optimization
 */
public final class ConfigKeys {
    
    /**
     * 应用基础配置
     */
    public static final class App {
        /** 服务器地址 */
        public static final String SERVER_ADDRESS = "app.server.address";
        /** 部署模式（dev/prod） */
        public static final String DEPLOYMENT_MODE = "app.deployment.mode";
        /** 文件上传目录 */
        public static final String UPLOAD_DIR = "app.upload.dir";
        /** 系统锁定提示消息 */
        public static final String LOCK_BLOCK_MESSAGE = "app.lock.block.message";
        
        private App() {}
    }
    
    /**
     * 用户相关配置
     */
    public static final class User {
        /** 用户头像存储目录 */
        public static final String AVATAR_DIR = "app.user.avatar.dir";
        /** 用户头像访问 URL */
        public static final String AVATAR_URL = "app.user.avatar.url";
        
        private User() {}
    }
    
    /**
     * 默认值配置
     */
    public static final class Default {
        /** 默认民族代码 */
        public static final String NATION = "app.default.nation";
        /** 默认国家代码 */
        public static final String NATIONALITY = "app.default.nationality";
        /** 默认省份代码 */
        public static final String PROVINCE = "app.default.province";
        /** 默认城市代码 */
        public static final String CITY = "app.default.city";
        /** 默认区县代码 */
        public static final String DISTRICT = "app.default.district";
        /** 默认婚姻状态 */
        public static final String MARITALSTATUS = "app.default.maritalstatus";
        /** 默认证件类型 */
        public static final String CERTTYPE = "app.default.certtype";
        /** 默认身份类别 */
        public static final String IDTYPE = "app.default.idtype";
        
        private Default() {}
    }
    
    /**
     * 部门相关配置
     */
    public static final class Dept {
        /** 部门超级父级 ID（根部门 ID） */
        public static final String SUPER_PARENT_ID = "app.dept.super.parent.id";
        
        private Dept() {}
    }
    
    /**
     * 验证码相关配置
     */
    public static final class Auth {
        /** 验证码过期时间（单位：分钟） */
        public static final String CODE_EXPIRE_PERIOD = "app.auth.code.expire.period";
        /** 需要检查验证码的域名列表（逗号分隔） */
        public static final String CHECK_CAPTURE_DOMAINS = "app.check.capture.domains";
        
        private Auth() {}
    }
    
    /**
     * OSS 对象存储相关配置
     */
    public static final class Oss {
        /** 是否启用 OSS 视频帧提取功能 */
        public static final String FETCH_FRAME = "app.oss.fetch.frame";
        
        private Oss() {}
    }
    
    /**
     * 缓存相关配置
     */
    public static final class Cache {
        /** 缓存 TTL 配置列表 */
        public static final String TTLS = "app.cache.ttls";
        
        private Cache() {}
    }
    
    /**
     * APK 相关配置
     */
    public static final class Apk {
        /** APK 默认操作系统版本 */
        public static final String OS_VERSION_DEFAULT = "apk.os.version.default";
        
        private Apk() {}
    }
    
    /**
     * Quartz 调度器配置
     */
    public static final class Quartz {
        // 调度器配置
        /** 调度器实例名称 */
        public static final String SCHEDULER_INSTANCE_NAME = "org.quartz.scheduler.instanceName";
        /** 调度器实例 ID */
        public static final String SCHEDULER_INSTANCE_ID = "org.quartz.scheduler.instanceId";
        
        // 线程池配置
        /** 线程池实现类 */
        public static final String THREADPOOL_CLASS = "org.quartz.threadPool.class";
        /** 线程池线程数量 */
        public static final String THREADPOOL_THREAD_COUNT = "org.quartz.threadPool.threadCount";
        /** 线程池线程优先级 */
        public static final String THREADPOOL_THREAD_PRIORITY = "org.quartz.threadPool.threadPriority";
        /** 线程是否继承初始化线程的上下文类加载器 */
        public static final String THREADPOOL_THREADS_INHERIT_CONTEXT_CLASSLOADER = 
                "org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread";
        
        // JobStore 配置
        /** JobStore 实现类 */
        public static final String JOBSTORE_CLASS = "org.quartz.jobStore.class";
        /** JobStore 驱动委托类 */
        public static final String JOBSTORE_DRIVER_DELEGATE_CLASS = "org.quartz.jobStore.driverDelegateClass";
        /** 是否启用集群模式 */
        public static final String JOBSTORE_IS_CLUSTERED = "org.quartz.jobStore.isClustered";
        /** 集群检查间隔（毫秒） */
        public static final String JOBSTORE_CLUSTER_CHECKIN_INTERVAL = "org.quartz.jobStore.clusterCheckinInterval";
        /** 最大失火任务处理数 */
        public static final String JOBSTORE_MAX_MISFIRES_TO_HANDLE_AT_A_TIME = 
                "org.quartz.jobStore.maxMisfiresToHandleAtATime";
        /** 失火阈值（毫秒） */
        public static final String JOBSTORE_MISFIRE_THRESHOLD = "org.quartz.jobStore.misfireThreshold";
        /** 数据库表前缀 */
        public static final String JOBSTORE_TABLE_PREFIX = "org.quartz.jobStore.tablePrefix";
        /** 数据源名称 */
        public static final String JOBSTORE_DATASOURCE = "org.quartz.jobStore.dataSource";
        /** 是否使用属性配置 */
        public static final String JOBSTORE_USE_PROPERTIES = "org.quartz.jobStore.useProperties";
        
        private Quartz() {}
    }
    
    // 私有构造函数，防止实例化
    private ConfigKeys() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}

