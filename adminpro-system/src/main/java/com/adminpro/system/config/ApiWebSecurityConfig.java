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
 * SpringSecurity的配置
 *
 * @author simon
 */
@Configuration
@Order(1)
public class ApiWebSecurityConfig {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    /**
     * 设置 HTTP 验证规则
     *
     * @param httpSecurity HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
