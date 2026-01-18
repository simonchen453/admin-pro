package com.adminpro.system.core.security.jwt;

import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器
 * 负责从请求中提取 Token，校验有效性（签名+白名单），并设置 SecurityContext
 * 
 * @author adminpro
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        log.debug("JwtAuthenticationFilter processing request: {}", requestUri);

        try {
            String token = extractToken(request);
            log.debug("Extracted token: {}", token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null");

            if (StringUtils.hasText(token)) {
                boolean isValid = jwtTokenProvider.validateToken(token);
                log.debug("Token validation result: {}", isValid);

                if (isValid) {
                    String jti = jwtTokenProvider.getJti(token);
                    log.debug("Token JTI: {}", jti);

                    // 白名单校验：检查 Token 是否在缓存中 (Key: at_{jti})
                    // 注意：设计文档中 Key 是 "at_" + jti，但 JwtCacheConstants.ACCESS_TOKEN_CACHE 定义为
                    // CacheName。
                    // EhCache 是 CacheName -> Key -> Value 结构。
                    // 所以我们应该是 get(CacheName, jti)

                    String cachedUserId = AppCache.getInstance().get(
                            JwtCacheConstants.ACCESS_TOKEN_CACHE,
                            jti,
                            String.class);
                    log.debug("Cached userId for JTI '{}': {}", jti, cachedUserId);

                    if (cachedUserId != null) {
                        // Token 有效且在白名单中
                        Claims claims = jwtTokenProvider.parseToken(token);
                        LoginUser loginUser = buildLoginUserFromClaims(claims);
                        log.debug("Built LoginUser: userId={}, loginName={}", loginUser.getUserId(), loginUser.getLoginName());

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                loginUser, null, loginUser.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("SecurityContext set with authentication for user: {}", loginUser.getLoginName());
                    } else {
                        log.debug("JWT Token not in whitelist (expired or revoked): {}", jti);
                    }
                } else {
                    log.debug("Token validation failed");
                }
            } else {
                log.debug("No token found in request");
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取 Token
     * 1. Authorization Header: Bearer <token>
     * 2. Cookie: accessToken=<token>
     */
    private String extractToken(HttpServletRequest request) {
        // 1. Check Header
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. Check Cookie (适配 Web 端)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 根据 Claims 构建 LoginUser
     */
    private LoginUser buildLoginUserFromClaims(Claims claims) {
        String userId = claims.getSubject();
        String userDomain = claims.get("userDomain", String.class);
        String loginName = claims.get("loginName", String.class);
        String realName = claims.get("realName", String.class);
        String deptNo = claims.get("deptNo", String.class);
        List<String> roles = claims.get("roles", List.class);
        List<String> permissions = claims.get("permissions", List.class);

        // 构建最小化 UserEntity
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUserDomain(userDomain);
        user.setLoginName(loginName);
        user.setRealName(realName);
        user.setDeptNo(deptNo);
        // user.setRoles(roles); // UserEntity 可能没有 setRoles，或者 roles 是关联表

        // LoginUser(userId, userDomain, loginName, password, status, deptNo, deptName,
        // realName, user, permissions)
        return new LoginUser(
                userId,
                userDomain,
                loginName,
                "", // password (not needed for JWT auth)
                "1", // status (assumed active if token exists)
                deptNo,
                "", // deptName (optional/fetch if needed)
                realName,
                user,
                permissions);
    }
}
