package com.adminpro.system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class Startup implements ApplicationRunner {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        // loadSysConfigs();
        logger.info("Application started.");
    }
}
