package com.adminpro.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * JWT 配置属性类
 * <p>
 * 对应 application.yml 中的 app.jwt 配置
 * 包含密钥、算法、有效期等关键配置
 * </p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥（生产环境必须从环境变量读取）
     * 长度至少 256 位
     */
    private String secret = "dev-only-secret-key-do-not-use-in-production-must-be-very-long-and-secure";

    /**
     * 签名算法
     */
    private String algorithm = "HS256";

    /**
     * 签发者
     */
    private String issuer = "adminpro";

    /**
     * Access Token 有效期（秒），按平台配置
     */
    private Map<String, Integer> accessTokenValidity = Map.of(
            "web", 900, // 15分钟
            "mobile", 1800, // 30分钟
            "miniprogram", 1800 // 30分钟
    );

    /**
     * Refresh Token 有效期（秒），按平台配置
     */
    private Map<String, Integer> refreshTokenValidity = Map.of(
            "web", 604800, // 7天
            "mobile", 2592000, // 30天
            "miniprogram", 2592000 // 30天
    );

    /**
     * 记住我时 Refresh Token 有效期（秒）
     */
    private Map<String, Integer> rememberMeValidity = Map.of(
            "web", 2592000, // 30天
            "mobile", 7776000, // 90天
            "miniprogram", 7776000 // 90天
    );

    /**
     * 是否启用 Refresh Token 轮换
     * 启用后每次刷新都会签发新的 RT，旧的立即失效
     */
    private boolean enableRefreshRotation = true;

    /**
     * 每用户最大设备数
     */
    private int maxDevicesPerUser = 5;

    /**
     * 是否检测 Token 重放攻击
     */
    private boolean detectTokenReuse = true;

    /**
     * 获取指定平台 Access Token 有效期
     */
    public int getAccessTokenValidity(String platform) {
        if (platform == null) {
            return 900;
        }
        return accessTokenValidity.getOrDefault(platform, 900);
    }

    /**
     * 获取指定平台 Refresh Token 有效期
     */
    public int getRefreshTokenValidity(String platform, boolean rememberMe) {
        if (platform == null) {
            return 604800;
        }
        if (rememberMe) {
            return rememberMeValidity.getOrDefault(platform, 2592000);
        }
        return refreshTokenValidity.getOrDefault(platform, 604800);
    }
}
