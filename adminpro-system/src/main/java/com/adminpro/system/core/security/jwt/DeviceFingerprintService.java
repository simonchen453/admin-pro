package com.adminpro.system.core.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * 设备指纹服务
 * <p>
 * 基于 HTTP 请求特征生成稳定的设备标识，用于识别同一设备的多次登录
 * </p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Slf4j
@Component
public class DeviceFingerprintService {

    /**
     * 用于生成指纹的 HTTP 头（按优先级排序）
     */
    private static final List<String> FINGERPRINT_HEADERS = Arrays.asList(
            "User-Agent", // 浏览器/应用标识
            "Accept-Language", // 语言设置
            "Accept-Encoding", // 编码方式
            "Accept" // 接受的内容类型
    );

    /**
     * 生成设备指纹
     * <p>
     * 基于 HTTP 请求头生成 SHA256 哈希值，同一浏览器的请求会生成相同的指纹
     * </p>
     *
     * @param request HTTP 请求对象
     * @return 设备指纹（32位十六进制字符串）
     */
    public String generateFingerprint(HttpServletRequest request) {
        StringBuilder fingerprintData = new StringBuilder();

        for (String header : FINGERPRINT_HEADERS) {
            String value = request.getHeader(header);
            if (StringUtils.hasText(value)) {
                fingerprintData.append(header).append(":").append(value).append("|");
            }
        }

        // 如果没有任何请求头，使用 IP 作为兜底
        if (fingerprintData.length() == 0) {
            String ip = getIpAddress(request);
            fingerprintData.append("ip:").append(ip);
        }

        String data = fingerprintData.toString();
        log.debug("设备指纹原始数据: {}", data);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            // 转换为十六进制字符串，取前 32 位
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < Math.min(16, hash.length); i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("生成设备指纹失败", e);
            // 降级：使用简单的哈希码
            return String.valueOf(data.hashCode());
        }
    }

    /**
     * 获取客户端 IP 地址
     * <p>
     * 优先从代理头获取（X-Forwarded-For），然后从 RemoteAddr 获取
     * </p>
     *
     * @param request HTTP 请求对象
     * @return IP 地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个 IP 值，第一个才是真实 IP
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            }
            return ip;
        }

        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }

    /**
     * 生成设备名称（用于展示）
     * <p>
     * 从 User-Agent 解析浏览器和操作系统信息
     * </p>
     *
     * @param request HTTP 请求对象
     * @return 设备名称（如 "Chrome on Windows"）
     */
    public String generateDeviceName(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (!StringUtils.hasText(userAgent)) {
            return "Unknown Device";
        }

        String ua = userAgent.toLowerCase();

        // 检测操作系统
        String os = "Unknown OS";
        if (ua.contains("windows nt 10")) {
            os = "Windows 10/11";
        } else if (ua.contains("windows nt 6.3")) {
            os = "Windows 8.1";
        } else if (ua.contains("windows nt 6.2")) {
            os = "Windows 8";
        } else if (ua.contains("windows nt 6.1")) {
            os = "Windows 7";
        } else if (ua.contains("windows nt 6.0")) {
            os = "Windows Vista";
        } else if (ua.contains("windows nt 5.1")) {
            os = "Windows XP";
        } else if (ua.contains("windows nt")) {
            os = "Windows";
        } else if (ua.contains("mac os x")) {
            os = "macOS";
        } else if (ua.contains("linux")) {
            os = "Linux";
        } else if (ua.contains("android")) {
            os = "Android";
        } else if (ua.contains("iphone") || ua.contains("ipad")) {
            os = "iOS";
        }

        // 检测浏览器
        String browser = "Unknown Browser";
        if (ua.contains("edg/")) {
            browser = "Edge";
        } else if (ua.contains("chrome") && !ua.contains("edg")) {
            browser = "Chrome";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            browser = "Safari";
        } else if (ua.contains("firefox")) {
            browser = "Firefox";
        } else if (ua.contains("opera") || ua.contains("opr")) {
            browser = "Opera";
        } else if (ua.contains("trident") || ua.contains("msie")) {
            browser = "Internet Explorer";
        }

        return browser + " on " + os;
    }

    /**
     * 获取设备ID (即设备指纹)
     *
     * @param request HTTP 请求对象
     * @return 设备ID
     */
    public String getDeviceId(HttpServletRequest request) {
        return generateFingerprint(request);
    }
}
