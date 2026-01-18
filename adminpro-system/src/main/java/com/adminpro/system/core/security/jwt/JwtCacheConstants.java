package com.adminpro.system.core.security.jwt;

/**
 * JWT 缓存常量
 * 
 * @author adminpro
 * @since 1.0.0
 */
public class JwtCacheConstants {

    /**
     * Access Token 缓存 (白名单)
     * Key: jti
     * Value: userId
     */
    public static final String ACCESS_TOKEN_CACHE = "jwt:access_token";

    /**
     * Refresh Token 缓存
     * Key: token (rt_...)
     * Value: RefreshTokenData
     */
    public static final String REFRESH_TOKEN_CACHE = "jwt:refresh_token";

    /**
     * 用户 Token 索引缓存
     * 用于通过 userId 查找所有关联的 refresh token，以便进行批量注销
     * Key: userId
     * Value: Set<String> (refresh token 列表)
     */
    public static final String USER_TOKENS_CACHE = "jwt:user_tokens";
}
