package com.adminpro.system.core.security.jwt;

import com.adminpro.system.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * JWT 令牌提供者
 * 负责 Token 的生成、解析和校验
 * 
 * @author adminpro
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {

    private final JwtProperties jwtProperties;
    private SecretKey key;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public void afterPropertiesSet() {
        // 初始化密钥
        byte[] keyBytes;
        String secret = jwtProperties.getSecret();
        if (!StringUtils.hasText(secret)) { // 早期版本使用 isEmpty
            throw new IllegalArgumentException("JWT secret cannot be empty");
        }

        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception e) {
            // 如果不是 Base64，尝试直接使用 UTF-8 字节（仅用于兼容，建议 Base64）
            keyBytes = secret.getBytes();
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Access Token
     *
     * @param subject 用户ID (sub)
     * @param claims  自定义载荷
     * @return JWT 字符串
     */
    public String createAccessToken(String subject, Map<String, Object> claims) {
        String platform = (String) claims.get("aud"); // aud 用于存平台
        int validitySeconds = jwtProperties.getAccessTokenValidity(platform);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validitySeconds * 1000L);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .header().add("typ", "JWT").and()
                .subject(subject)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(validity)
                .id(jti) // jti 用于黑/白名单
                .claims(claims) // 设置自定义 Claims
                .signWith(key) // 自动推断算法
                .compact();
    }

    /**
     * 解析 Token
     *
     * @param token JWT 字符串
     * @return Claims
     * @throws JwtException 如果 Token 无效
     */
    public Claims parseToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 校验 Token 签名和有效期
     * (不包含黑/白名单校验，那在 Filter 层处理)
     *
     * @param token JWT 字符串
     * @return 校验通过返回 true
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 Token 中获取 JTI
     */
    public String getJti(String token) {
        try {
            return parseToken(token).getId();
        } catch (Exception e) {
            return null;
        }
    }
}
