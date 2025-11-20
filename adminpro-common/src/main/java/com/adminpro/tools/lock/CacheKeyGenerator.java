package com.adminpro.tools.lock;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * key生成器
 *
 * @author simon
 * @date 2019/4/3
 */
public interface CacheKeyGenerator {

    /**
     * 获取AOP参数,生成指定缓存Key
     *
     * @param pjp PJP
     * @return 缓存KEY
     */
    String getLockKey(ProceedingJoinPoint pjp);
}
