package com.adminpro.config;

import com.adminpro.tools.domains.entity.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author simon
 * 只有当spring session开的时候该类才会起作用，spring.session.store-type=redis
 *
 * 之所以要分开，是因为Spring session的事件是他自己实现的, 不是HttpSessionEnvent
 */
@Configuration
@EnableSpringHttpSession
@ConditionalOnProperty(value = "spring.session.store-type", havingValue = "redis")
public class SpringSessionConfig {
    private static final Logger logger = LoggerFactory.getLogger(SpringSessionConfig.class);

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookiePath("/");
        return serializer;
    }

    @Bean(name = "springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> defaultRedisSerializer() {
        return new JdkSerializationRedisSerializer() {
            @Override
            public Object deserialize(byte[] bytes) {
                try {
                    return super.deserialize(bytes);
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.warn(e.getMessage());
                    }
                    return null;
                }
            }

            @Override
            public byte[] serialize(Object object) {
                return super.serialize(object);
            }
        };
    }

    @EventListener
    public void onSessionExpired(SessionExpiredEvent expiredEvent) {
        String sessionId = expiredEvent.getSessionId();
        SessionService.getInstance().invalid(sessionId);
        logger.debug("### on session expired");
    }

    @EventListener
    public void onSessionDeleted(SessionDeletedEvent deletedEvent) {
        String sessionId = deletedEvent.getSessionId();
        SessionService.getInstance().invalid(sessionId);
        logger.debug("### on session deleted");
    }

    @EventListener
    public void onSessionCreated(SessionCreatedEvent createdEvent) {
        logger.debug("### on session created");
    }
}
