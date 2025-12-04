package com.adminpro.core.base.util;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取spring context内容
 *
 * @author simon
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static <E> E getBean(String name, Class<E> clazz) {
        assertContextInjected();
        return context.getBean(name, clazz);
    }

    public static <E> E getBean(Class<E> clazz) {
        assertContextInjected();
        return context.getBean(clazz);
    }

    public static Object getBean(String clazz) {
        assertContextInjected();
        return context.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("====注入applicaitonContext属性=====");
        context = applicationContext;
    }

    /**
     * 检查ApplicationContext不为空.
     */
    private static void assertContextInjected() {
        Validate.validState(context != null, "applicaitonContext属性未注入.");
    }
}
