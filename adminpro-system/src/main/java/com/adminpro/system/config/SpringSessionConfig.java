package com.adminpro.system.config;

import com.adminpro.system.tools.domains.entity.session.SessionService;
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
 * Spring Session 配置类（基于Redis）
 * <p>
 * 该配置类负责配置Spring Session与Redis的集成，实现分布式会话管理。
 * 当配置文件中设置 spring.session.store-type=redis 时，该配置类生效。
 * <p>
 * 主要功能：
 * <ul>
 *   <li>将HTTP Session存储到Redis，实现分布式会话</li>
 *   <li>配置Session Cookie的序列化策略</li>
 *   <li>配置Redis的Session序列化器</li>
 *   <li>监听Session生命周期事件（创建、过期、删除）</li>
 *   <li>自动清理本地Session缓存</li>
 * </ul>
 * <p>
 * 配置条件：
 * <ul>
 *   <li>spring.session.store-type=redis：启用Redis存储Session</li>
 *   <li>需要spring-session-data-redis依赖</li>
 *   <li>需要Redis服务可用</li>
 * </ul>
 * <p>
 * 应用场景：
 * <ul>
 *   <li>多服务器部署，需要共享Session</li>
 *   <li>负载均衡环境，保持用户登录状态</li>
 *   <li>分布式系统，统一会话管理</li>
 *   <li>微服务架构，Session共享</li>
 * </ul>
 * <p>
 * 注意事项：
 * <ul>
 *   <li>Spring Session的事件是自定义实现，不是标准HttpSessionEvent</li>
 *   <li>Session数据存储在Redis中，服务器重启不影响用户登录</li>
 *   <li>需要注意Redis的性能和可用性</li>
 * </ul>
 *
 * @author simon
 * @see org.springframework.session.config.annotation.web.http.EnableSpringHttpSession
 * @see org.springframework.session.data.redis.RedisIndexedSessionRepository
 */
@Configuration
@EnableSpringHttpSession
@ConditionalOnProperty(value = "spring.session.store-type", havingValue = "redis")
public class SpringSessionConfig {
    private static final Logger logger = LoggerFactory.getLogger(SpringSessionConfig.class);

    /**
     * 配置Session Cookie序列化器
     * <p>
     * 该方法配置Session Cookie的序列化和反序列化策略：
     * <ul>
     *   <li>使用DefaultCookieSerializer处理Cookie</li>
     *   <li>设置Cookie路径为根路径（/）</li>
     *   <li>确保整个应用共享Session Cookie</li>
     *   <li>支持跨子域共享Session（可选）</li>
     * </ul>
     * <p>
     * Cookie配置说明：
     * <ul>
     *   <li>路径设置为/：所有路径都能访问Session</li>
     *   <li>Cookie名称：默认为SESSION</li>
     *   <li>支持HttpOnly和Secure标志（可选配置）</li>
     * </ul>
     * <p>
     * 注意：正确设置Cookie路径很重要，否则可能导致Session丢失。
     *
     * @return 配置好的CookieSerializer实例
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookiePath("/");
        return serializer;
    }

    /**
     * 配置Spring Session的Redis序列化器
     * <p>
     * 该方法配置Session对象在Redis中的序列化方式：
     * <ul>
     *   <li>使用JDK序列化（保持对象类型）</li>
     *   <li>增强异常处理（反序列化失败返回null）</li>
     *   <li>避免因序列化问题导致Session读取失败</li>
     *   <li>记录调试日志便于问题排查</li>
     * </ul>
     * <p>
     * 异常处理策略：
     * <ul>
     *   <li>反序列化失败：记录警告日志，返回null</li>
     *   <li>序列化失败：抛出异常，由上层处理</li>
     *   <li>容错性强，不会因单个Session问题影响整体</li>
     * </ul>
     * <p>
     * 注意事项：
     * <ul>
     *   <li>JDK序列化要求对象实现Serializable接口</li>
     *   <li>序列化后的数据不易读（二进制格式）</li>
     *   <li>Session对象修改后需要实现Serializable</li>
     * </ul>
     *
     * @return 自定义的Redis序列化器
     */
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

    /**
     * 监听Session过期事件
     * <p>
     * 该方法监听Spring Session的过期事件，当Session在Redis中过期时触发：
     * <ul>
     *   <li>自动清理本地Session缓存</li>
     *   <li>调用SessionService使Session失效</li>
     *   <li>释放相关资源</li>
     *   <li>记录调试日志</li>
     * </ul>
     * <p>
     * 触发时机：
     * <ul>
     *   <li>Session达到最大空闲时间</li>
     *   <li>Redis中的Session key被TTL淘汰</li>
     *   <li>Redis内存不足时淘汰</li>
     * </ul>
     * <p>
     * 使用@EventListener注解实现事件监听。
     *
     * @param expiredEvent Session过期事件，包含过期Session的信息
     */
    @EventListener
    public void onSessionExpired(SessionExpiredEvent expiredEvent) {
        String sessionId = expiredEvent.getSessionId();
        SessionService.getInstance().invalid(sessionId);
        logger.debug("### on session expired");
    }

    /**
     * 监听Session删除事件
     * <p>
     * 该方法监听Spring Session的删除事件，当Session被显式删除时触发：
     * <ul>
     *   <li>用户主动退出登录</li>
     *   <li>管理员强制踢出用户</li>
     *   <li>程序调用session.invalidate()</li>
     *   <li>Redis中的Session key被删除</li>
     * </ul>
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>清理本地Session缓存</li>
     *   <li>调用SessionService使Session失效</li>
     *   <li>释放相关资源</li>
     *   <li>记录调试日志</li>
     * </ul>
     * <p>
     * 注意：删除事件和过期事件的处理逻辑相同，但触发时机不同。
     *
     * @param deletedEvent Session删除事件，包含被删除Session的信息
     */
    @EventListener
    public void onSessionDeleted(SessionDeletedEvent deletedEvent) {
        String sessionId = deletedEvent.getSessionId();
        SessionService.getInstance().invalid(sessionId);
        logger.debug("### on session deleted");
    }

    /**
     * 监听Session创建事件
     * <p>
     * 该方法监听Spring Session的创建事件，当新Session被创建时触发：
     * <ul>
     *   <li>用户首次访问应用</li>
     *   <li>用户登录成功</li>
     *   <li>调用request.getSession(true)</li>
     * </ul>
     * <p>
     * 用途说明：
     * <ul>
     *   <li>记录Session创建日志</li>
     *   <li>统计在线用户数</li>
     *   <li>初始化Session相关数据</li>
     *   <li>调试和监控Session生命周期</li>
     * </ul>
     * <p>
     * 注意：该方法目前只记录日志，可以根据业务需求扩展。
     *
     * @param createdEvent Session创建事件，包含新创建Session的信息
     */
    @EventListener
    public void onSessionCreated(SessionCreatedEvent createdEvent) {
        logger.debug("### on session created");
    }
}
