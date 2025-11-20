package com.adminpro.framework.listener;

import com.adminpro.tools.domains.entity.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener {
    public static final Logger logger = LoggerFactory.getLogger(SessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        String sessionid = httpSessionEvent.getSession().getId();
        SessionService.getInstance().invalid(sessionid);
        logger.info("session destroyed, session id: " + sessionid);
    }
}
