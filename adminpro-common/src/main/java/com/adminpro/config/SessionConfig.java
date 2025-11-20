package com.adminpro.config;

import com.adminpro.tools.domains.entity.session.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * session监听器
 * @author simon
 *
 * 只有当spring session关的时候该类起作用，spring.session.store-type=none
 *
 */
@Configuration
@Slf4j
@ConditionalOnProperty(value = "spring.session.store-type", havingValue = "none", matchIfMissing = true)
public class SessionConfig implements HttpSessionListener{

    @EventListener
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        SessionService.getInstance().invalid(sessionId);
        log.debug("### On Session Destroyed");
    }

    @EventListener
    public void sessionCreated(HttpSessionEvent deletedEvent) {
        log.debug("### On Session Created");
    }
}