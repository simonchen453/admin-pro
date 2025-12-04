package com.adminpro.config;

import com.adminpro.tools.domains.entity.session.SessionEntity;
import com.adminpro.tools.domains.entity.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Startup implements ApplicationRunner {
    private boolean init = false;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SessionService sessionService;

    private void loadSysConfigs() {
        if (init) {
            return;
        }

        init = true;
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
//        loadSysConfigs();
        List<SessionEntity> activeSessions = sessionService.findActiveSessions();
        sessionService.invalid(activeSessions);
    }
}
