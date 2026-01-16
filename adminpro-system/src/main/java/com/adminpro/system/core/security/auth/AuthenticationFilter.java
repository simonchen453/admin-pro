package com.adminpro.system.core.security.auth;

import com.adminpro.framework.client.helper.ClientHelper;

import com.adminpro.system.rbac.api.LoginHelper;
import com.adminpro.system.rbac.domains.entity.user.UserIden;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author simon
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthUserDetailServiceImpl userDetailsService;

    @Autowired
    TokenHelper tokenHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authToken = extractToken(request);
        if (ClientHelper.isMobileRequest(request) && StringUtils.isNotEmpty(authToken)) {
            UserIden userIden = TokenHelper.getInstance().getUserIdenByToken(authToken);
            if (userIden != null) {
                LoginUser authUser = (LoginUser) this.userDetailsService
                        .loadUserByUsername(userIden.toSecurityUsername());
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
