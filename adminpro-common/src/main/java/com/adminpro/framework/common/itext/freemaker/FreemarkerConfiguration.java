package com.adminpro.framework.common.itext.freemaker;

import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FreemarkerConfiguration {

    protected static Logger logger = LoggerFactory.getLogger(FreemarkerConfiguration.class);

    private static Configuration config = null;

    public static synchronized Configuration getConfiguation() {
        if (config == null) {
            setConfiguation();
        }
        return config;
    }

    private static void setConfiguation() {
        config = new Configuration();
        config.setClassForTemplateLoading(FreemarkerConfiguration.class, "/");
    }

}
