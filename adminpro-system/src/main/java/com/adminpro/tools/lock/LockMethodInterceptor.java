package com.adminpro.tools.lock;

import com.adminpro.framework.cache.AppCache;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.tools.lock.annotation.CacheLock;
import com.adminpro.web.BaseConstants;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * redis 方案
 */
@Aspect
@Configuration
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
public class LockMethodInterceptor {

    protected static Logger logger = LoggerFactory.getLogger(LockMethodInterceptor.class);

    @Autowired
    private CacheKeyGenerator cacheKeyGenerator;

    @Autowired
    private AppCache appCache;

    @Around("execution(public * *(..)) && @annotation(com.adminpro.tools.lock.annotation.CacheLock)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock lock = method.getAnnotation(CacheLock.class);
        if (StringUtils.isEmpty(lock.key())) {
            throw new RuntimeException("lock key can't be null...");
        }
        final String lockKey = cacheKeyGenerator.getLockKey(pjp);

        String className = pjp.getTarget().getClass().getName();
        String methodName = signature.getName();

        logger.debug("分布式锁: " + className + "." + methodName + ", lock key: " + lockKey);
        try {
            //key不存在才能设置成功
            final Boolean success = appCache.get(RbacCacheConstants.LOCK_CACHE, lockKey, Boolean.class);
            if (!success) {
                appCache.set(RbacCacheConstants.LOCK_CACHE, lockKey, true);
            } else {
                String message = lock.message();
                if (StringUtils.isEmpty(message)) {
                    message = BaseConstants.LOCK_MSG;
                }
                logger.error(message);
                throw new CacheLockException(message);
            }

            return pjp.proceed();
        } finally {
            //如果演示的话需要注释该代码;实际应该放开
            appCache.delete(RbacCacheConstants.LOCK_CACHE, lockKey);
        }
    }
}
