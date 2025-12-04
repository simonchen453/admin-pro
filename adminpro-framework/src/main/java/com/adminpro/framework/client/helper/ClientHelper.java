package com.adminpro.framework.client.helper;

import com.adminpro.framework.client.enums.ClientType;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ClientHelper {
    
    private static final String HEADER_CLIENT_TYPE = "X-Client-Type";
    private static final String HEADER_APP_VERSION = "X-App-Version";
    private static final String HEADER_DEVICE_ID = "X-Device-Id";
    
    public static ClientType detectClientType(HttpServletRequest request) {
        if (request == null) {
            return ClientType.UNKNOWN;
        }
        
        String clientTypeHeader = request.getHeader(HEADER_CLIENT_TYPE);
        if (StringUtils.isNotEmpty(clientTypeHeader)) {
            ClientType type = ClientType.fromCode(clientTypeHeader);
            if (type != ClientType.UNKNOWN) {
                return type;
            }
        }
        
        String userAgent = request.getHeader("User-Agent");
        if (StringUtils.isEmpty(userAgent)) {
            return ClientType.UNKNOWN;
        }
        
        String ua = userAgent.toLowerCase();
        String referer = request.getHeader("Referer");
        
        if (ua.contains("micromessenger")) {
            if (StringUtils.isNotEmpty(referer) && 
                (referer.contains("servicewechat.com") || referer.contains("servicewechat.net"))) {
                return ClientType.WECHAT_MINI_PROGRAM;
            }
            String miniProgramHeader = request.getHeader("X-WX-Source");
            if (StringUtils.isNotEmpty(miniProgramHeader) && "miniprogram".equals(miniProgramHeader)) {
                return ClientType.WECHAT_MINI_PROGRAM;
            }
            return ClientType.WEB;
        }
        
        if (ua.contains("alipayclient")) {
            if (StringUtils.isNotEmpty(referer) && 
                (referer.contains("alipay.com") || referer.contains("alipaydev.com"))) {
                return ClientType.ALIPAY_MINI_PROGRAM;
            }
            String miniProgramHeader = request.getHeader("X-Alipay-Source");
            if (StringUtils.isNotEmpty(miniProgramHeader) && "miniprogram".equals(miniProgramHeader)) {
                return ClientType.ALIPAY_MINI_PROGRAM;
            }
            return ClientType.WEB;
        }
        
        if (ua.contains("android") && !ua.contains("wv") && !ua.contains("micromessenger") && !ua.contains("alipayclient")) {
            return ClientType.ANDROID;
        }
        
        if (ua.matches(".*(iphone|ipad|ipod).*") && !ua.contains("micromessenger") && !ua.contains("alipayclient")) {
            return ClientType.IOS;
        }
        
        return ClientType.WEB;
    }
    
    public static String getAppVersion(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getHeader(HEADER_APP_VERSION);
    }
    
    public static String getDeviceId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getHeader(HEADER_DEVICE_ID);
    }
    
    public static boolean isMobileAppRequest(HttpServletRequest request) {
        ClientType clientType = detectClientType(request);
        return clientType.isMobileApp();
    }
    
    public static boolean isMiniProgramRequest(HttpServletRequest request) {
        ClientType clientType = detectClientType(request);
        return clientType.isMiniProgram();
    }
    
    public static boolean isMobileRequest(HttpServletRequest request) {
        ClientType clientType = detectClientType(request);
        return clientType.isMobile();
    }
}

