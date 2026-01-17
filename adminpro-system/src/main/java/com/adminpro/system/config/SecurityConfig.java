package com.adminpro.system.config;

import com.adminpro.system.core.security.CustomAuthenticationProvider;
import com.adminpro.system.core.security.auth.AuthUserDetailServiceImpl;
import com.adminpro.system.core.security.auth.AuthenticationFilter;
import com.adminpro.system.core.security.handle.AuthenticationEntryPointImpl;
import com.adminpro.system.core.security.handle.LogoutHandlerImpl;
import com.adminpro.system.core.security.handle.LogoutSuccessHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Spring Security 核心配置类
 * <p>
 * 该配置类负责配置应用程序的安全认证和授权机制，主要功能包括：
 * <ul>
 * <li>配置HTTP安全过滤器链，定义URL访问权限规则</li>
 * <li>集成自定义认证提供者和用户详情服务</li>
 * <li>配置密码加密器（BCrypt）</li>
 * <li>配置跨域资源共享（CORS）策略</li>
 * <li>配置认证入口点和退出登录处理器</li>
 * <li>支持方法级别的安全注解（@PreAuthorize、@Secured等）</li>
 * </ul>
 * <p>
 * 该配置通过 {@link SecurityProperties} 从 application.yml 读取公开接口列表，
 * 支持灵活的权限控制策略：
 * <ul>
 * <li>严格模式：所有接口都需要认证（公开接口除外）</li>
 * <li>兼容模式：所有接口允许访问，通过注解控制权限</li>
 * </ul>
 *
 * @author simon
 * @see org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 * @see org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
    /**
     * 自定义用户认证服务
     * <p>
     * 负责从数据库或其他数据源加载用户信息，用于用户身份验证
     */
    @Autowired
    private AuthUserDetailServiceImpl userDetailsService;

    /**
     * 认证失败处理类
     * <p>
     * 当用户未认证或认证失败时，返回401未授权响应
     */
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * 退出登录成功处理器
     * <p>
     * 处理用户退出登录成功后的响应逻辑
     */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /**
     * 退出登录处理器
     * <p>
     * 执行用户退出登录时的清理操作，如清除token、缓存等
     */
    @Autowired
    private LogoutHandlerImpl logoutHandler;

    /**
     * Token认证过滤器
     * <p>
     * 在用户名密码认证过滤器之前执行，从请求头中提取token并进行验证
     */
    @Autowired
    private AuthenticationFilter authenticationFilter;

    /**
     * 自定义认证提供者
     * <p>
     * 提供自定义的用户认证逻辑，支持多种认证方式
     */
    @Autowired
    protected CustomAuthenticationProvider customAuthenticationProvider;

    /**
     * 安全配置属性
     * <p>
     * 从配置文件中读取安全相关配置，包括公开接口、认证模式等
     */
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 允许的跨域请求源
     * <p>
     * 从配置文件读取，多个源用逗号分隔
     */
    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    /**
     * 是否允许所有源的跨域请求
     * <p>
     * 开发环境可设置为true，生产环境建议关闭
     */
    @Value("${app.cors.allow-all-origins:false}")
    private boolean allowAllOrigins;

    /**
     * 配置HTTP安全过滤器链
     * <p>
     * 该方法定义了应用程序的HTTP安全规则，包括：
     * <ul>
     * <li>启用CORS跨域支持</li>
     * <li>禁用CSRF防护（使用JWT token，不需要CSRF）</li>
     * <li>配置认证失败处理</li>
     * <li>配置响应头（X-Frame-Options、HSTS等）</li>
     * <li>配置URL访问权限（公开接口和认证接口）</li>
     * <li>配置退出登录逻辑</li>
     * <li>添加自定义认证过滤器</li>
     * </ul>
     * <p>
     * 支持通过 application.yml 配置公开接口列表，灵活控制接口访问权限
     *
     * @param httpSecurity HttpSecurity构建器，用于配置HTTP安全
     * @return 配置好的SecurityFilterChain实例
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .contentTypeOptions(contentType -> {
                        })
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000)))

                // .sessionManagement(session ->
                // session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .authorizeHttpRequests(auth -> {
                    // 配置公开接口（从 application.yml 读取）
                    String[] publicUrls = securityProperties.getAllPublicUrls();
                    if (publicUrls.length > 0) {
                        List<RequestMatcher> matchers = new ArrayList<>();
                        for (String url : publicUrls) {
                            matchers.add(new AntPathRequestMatcher(url));
                        }
                        auth.requestMatchers(matchers.toArray(new RequestMatcher[0])).permitAll();
                    }

                    // 根据配置决定是否要求认证
                    if (securityProperties.isAuthRequired()) {
                        // 默认所有接口需要认证
                        auth.anyRequest().authenticated();
                    } else {
                        // 兼容模式：所有接口允许访问（通过 @PreAuthorize 注解控制）
                        auth.anyRequest().permitAll();
                    }
                })

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(logoutSuccessHandler))
                .authenticationProvider(customAuthenticationProvider)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    /**
     * 创建BCrypt密码加密器
     * <p>
     * BCrypt是一种强散列哈希加密算法，专门用于密码加密存储。
     * 特点：
     * <ul>
     * <li>自带盐值，每次加密结果都不同</li>
     * <li>可调整加密强度（默认10）</li>
     * <li>单向加密，无法解密</li>
     * <li>抗彩虹表攻击</li>
     * </ul>
     *
     * @return BCryptPasswordEncoder实例，用于密码加密和验证
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 创建身份认证管理器
     * <p>
     * AuthenticationManager是Spring Security的核心认证接口，
     * 负责协调认证过程，包括：
     * <ul>
     * <li>使用UserDetailsService加载用户信息</li>
     * <li>使用PasswordEncoder验证密码</li>
     * <li>处理认证成功或失败的结果</li>
     * </ul>
     *
     * @param httpSecurity HttpSecurity构建器
     * @return 配置好的AuthenticationManager实例
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
        return authenticationManagerBuilder.build();
    }

    /**
     * 配置跨域资源共享（CORS）策略
     * <p>
     * 该方法配置应用程序的CORS策略，支持：
     * <ul>
     * <li>通过配置文件指定允许的源列表</li>
     * <li>允许所有源（开发模式）</li>
     * <li>支持移动端应用调用</li>
     * <li>支持微信小程序和支付宝小程序</li>
     * <li>允许携带凭证（Cookie等）</li>
     * <li>允许所有HTTP方法和请求头</li>
     * </ul>
     * <p>
     * 小程序域名支持：
     * <ul>
     * <li>微信小程序：servicewechat.com、servicewechat.net及其子域名</li>
     * <li>支付宝小程序：alipay.com、alipaydev.com及其子域名</li>
     * </ul>
     * <p>
     * 配置参数：
     * <ul>
     * <li>app.cors.allowed-origins：允许的源列表，逗号分隔</li>
     * <li>app.cors.allow-all-origins：是否允许所有源（默认false）</li>
     * </ul>
     *
     * @return 配置好的CorsConfigurationSource实例
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        List<String> miniProgramOrigins = Arrays.asList(
                "https://servicewechat.com",
                "https://servicewechat.net",
                "https://*.servicewechat.com",
                "https://*.servicewechat.net",
                "https://alipay.com",
                "https://alipaydev.com",
                "https://*.alipay.com",
                "https://*.alipaydev.com");

        if (allowAllOrigins) {
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
        } else if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            String[] origins = allowedOrigins.split(",");
            List<String> originList = new ArrayList<>(Arrays.asList(origins));
            originList.addAll(miniProgramOrigins);
            config.setAllowedOrigins(originList);
        } else {
            List<String> defaultOrigins = new ArrayList<>();
            defaultOrigins.add("http://localhost:3000");
            defaultOrigins.add("http://localhost:5173");
            defaultOrigins.addAll(miniProgramOrigins);
            config.setAllowedOrigins(defaultOrigins);
        }

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
