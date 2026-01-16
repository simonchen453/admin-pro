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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * spring security配置
 *
 * @author simon
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
    /**
     * 自定义用户认证逻辑
     */
    @Autowired
    private AuthUserDetailServiceImpl userDetailsService;

    /**
     * 认证失败处理类
     */
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * 退出处理类
     */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    @Autowired
    private LogoutHandlerImpl logoutHandler;

    /**
     * token认证过滤器
     */
    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    protected CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private SecurityProperties securityProperties;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    @Value("${app.cors.allow-all-origins:false}")
    private boolean allowAllOrigins;

    /**
     * 配置安全过滤器链
     * 支持通过 application.yml 配置公开接口
     *
     * @param httpSecurity HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 配置异常
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
                        auth.requestMatchers(publicUrls).permitAll();
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
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 身份认证管理器
     *
     * @param httpSecurity HttpSecurity
     * @return AuthenticationManager
     * @throws Exception 配置异常
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
     * CORS 配置源
     * 配置跨域请求，支持通过配置文件设置允许的源
     * 支持移动端调用：允许所有源或指定源列表
     * 支持小程序调用：微信小程序和支付宝小程序的域名
     *
     * @return CorsConfigurationSource
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
