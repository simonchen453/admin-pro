package com.adminpro.system.config;

import com.adminpro.system.core.security.auth.AuthenticationFilter;
import com.adminpro.system.core.security.handle.AuthenticationEntryPointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * 应用端API安全配置类（Sessionless模式）
 * <p>
 * 该配置类专门用于配置移动端、小程序等无状态客户端的API安全策略。
 * 与 {@link SecurityConfig} 的主要区别：
 * <ul>
 *   <li>匹配路径：仅处理 /app/** 开头的请求</li>
 *   <li>会话策略：无状态（STATELESS），不使用HTTP Session</li>
 *   <li>认证方式：完全依赖JWT Token进行认证</li>
 *   <li>优先级：Order(1)，优先于主配置执行</li>
 *   <li>权限控制：所有请求允许访问，通过过滤器进行认证</li>
 * </ul>
 * <p>
 * 适用场景：
 * <ul>
 *   <li>移动应用（iOS、Android）</li>
 *   <li>微信小程序</li>
 *   <li>支付宝小程序</li>
 *   <li>前后端分离的单页应用</li>
 * </ul>
 *
 * @author simon
 * @see SecurityConfig
 * @see org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 */
@Configuration
@Order(1)
public class AppWebSecurityConfig {

    /**
     * Token认证过滤器
     * <p>
     * 从请求头中提取JWT Token并进行验证，验证通过后设置认证信息到安全上下文
     */
    @Autowired
    private AuthenticationFilter authenticationFilter;

    /**
     * 认证失败处理类
     * <p>
     * 当API请求未携带Token或Token无效时，返回JSON格式的401未授权响应
     */
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * CORS配置源
     * <p>
     * 配置跨域资源共享策略，支持移动端和小程序的跨域请求
     */
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    /**
     * 配置应用端API安全过滤器链
     * <p>
     * 该方法为 /app/** 路径的API请求配置安全规则：
     * <ul>
     *   <li>路径匹配：仅处理 /app/** 开头的请求</li>
     *   <li>CSRF防护：禁用（使用JWT Token，不需要CSRF保护）</li>
     *   <li>CORS支持：启用跨域资源共享</li>
     *   <li>异常处理：自定义认证失败响应（JSON格式）</li>
     *   <li>会话管理：无状态模式，不创建或使用HTTP Session</li>
     *   <li>权限控制：所有请求允许访问，通过Token过滤器认证</li>
     *   <li>过滤器：在用户名密码过滤器前添加Token认证过滤器</li>
     * </ul>
     * <p>
     * 无状态设计：
     * <ul>
     *   <li>服务器不保存会话信息</li>
     *   <li>每次请求都携带完整的认证信息（Token）</li>
     *   <li>便于水平扩展和负载均衡</li>
     *   <li>适合移动端和小程序场景</li>
     * </ul>
     *
     * @param httpSecurity HttpSecurity构建器，用于配置HTTP安全
     * @return 配置好的SecurityFilterChain实例
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/app/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
