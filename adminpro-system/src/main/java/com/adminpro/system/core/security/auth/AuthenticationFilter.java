package com.adminpro.system.core.security.auth;

import com.adminpro.framework.client.helper.ClientHelper;
import com.adminpro.system.rbac.api.LoginHelper;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 认证过滤器
 * <p>
 * 继承Spring Security的OncePerRequestFilter，确保每个请求只执行一次过滤。
 * 负责从请求中提取Token，验证用户身份，并将认证信息设置到SecurityContext中。
 * <p>
 * 支持两种认证模式：
 * <ul>
 * <li>移动端模式：基于Token的无状态认证</li>
 * <li>Web端模式：基于Session的有状态认证</li>
 * </ul>
 * <p>
 * Token提取支持多种方式（按优先级）：
 * <ol>
 * <li>Authorization: Bearer {token} - 标准HTTP Header方式（推荐移动端使用）</li>
 * <li>x-access-token Header - 兼容现有前端</li>
 * <li>x-access-token Query参数 - 兼容旧版本</li>
 * </ol>
 * <p>
 * 安全特性：
 * <ul>
 * <li>自动检测请求类型，选择合适的认证方式</li>
 * <li>Token验证失败时清除认证信息</li>
 * <li>支持Token自动刷新机制</li>
 * </ul>
 *
 * @author simon
 * @see org.springframework.web.filter.OncePerRequestFilter
 * @see TokenHelper
 * @see LoginUser
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthUserDetailServiceImpl userDetailsService;

    @Autowired
    TokenHelper tokenHelper;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 公开接口路径模式（无需认证）
     */
    private static final String[] PUBLIC_PATTERNS = {
            "/api/v1/auth/login",
            "/api/v1/auth/logout",
            "/api/v1/auth/captcha.jpg",
            "/api/v1/auth/captcha/**",
            "/api/v1/common/**",
            "/api/v1/public/**",
            "/error",
            "/favicon.ico",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/doc.html",
            "/actuator/**",
            "/js/**",
            "/css/**",
            "/images/**",
            "/static/**",
            "/assets/**"
    };

    /**
     * 判断请求是否应该跳过认证过滤
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        for (String pattern : PUBLIC_PATTERNS) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 执行过滤逻辑
     * <p>
     * 处理流程：
     * <ol>
     * <li>从请求中提取Token</li>
     * <li>判断请求类型（移动端/Web端）</li>
     * <li>移动端：验证Token并加载用户信息</li>
     * <li>Web端：从Session中获取用户信息</li>
     * <li>将认证信息设置到SecurityContext</li>
     * <li>继续执行后续过滤器</li>
     * </ol>
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param chain    过滤器链
     * @throws IOException      如果发生I/O错误
     * @throws ServletException 如果发生Servlet相关错误
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authToken = extractToken(request);
        if (ClientHelper.isMobileRequest(request) && StringUtils.isNotEmpty(authToken)) {
            UserEntity user = TokenHelper.getInstance().getUserByToken(authToken);
            if (user != null) {
                // 构造 securityUsername: userDomain_loginName
                String securityUsername = user.getUserDomain() + "_" + user.getLoginName();
                LoginUser authUser = (LoginUser) this.userDetailsService.loadUserByUsername(securityUsername);
                if (authUser != null) {
                    if (tokenHelper.validateToken(authToken, authUser)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                authUser, null, authUser.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        SecurityContextHolder.getContext().setAuthentication(null);
                    }
                } else {
                    SecurityContextHolder.getContext().setAuthentication(null);
                }
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        } else {
            LoginUser authUser = LoginHelper.getInstance().getLoginUser();
            if (authUser != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUser,
                        null, authUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * 从请求中提取Token
     * 支持多种方式：
     * 1. Authorization: Bearer <token> (标准方式，推荐移动端使用)
     * 2. x-access-token header (兼容现有前端)
     * 3. x-access-token query参数 (兼容旧版本)
     *
     * @param request HTTP请求
     * @return Token字符串，如果未找到则返回null
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        authHeader = request.getHeader("x-access-token");
        if (StringUtils.isNotEmpty(authHeader)) {
            return authHeader;
        }

        authHeader = request.getParameter("x-access-token");
        if (StringUtils.isNotEmpty(authHeader)) {
            return authHeader;
        }

        return null;
    }
}
