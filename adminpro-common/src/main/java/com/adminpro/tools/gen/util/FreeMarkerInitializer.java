package com.adminpro.tools.gen.util;

import com.adminpro.rbac.common.RbacConstants;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * FreeMarker模板引擎初始化器
 */
public class FreeMarkerInitializer {
    private static Configuration config = null;

    /**
     * 初始化FreeMarker配置
     */
    public static synchronized Configuration getConfiguration() {
        if (config == null) {
            config = new Configuration(Configuration.VERSION_2_3_31);
            config.setClassForTemplateLoading(FreeMarkerInitializer.class, "/");
            config.setDefaultEncoding(RbacConstants.UTF8);
            config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            config.setLogTemplateExceptions(false);
            config.setWrapUncheckedExceptions(true);
            config.setFallbackOnNullLoopVariable(false);
        }
        return config;
    }
}

