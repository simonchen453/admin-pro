package com.adminpro.framework.client.interceptor;

import com.adminpro.framework.client.enums.ClientType;
import com.adminpro.framework.client.helper.ClientHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class ClientInfoInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientInfoInterceptor.class);
    private static final String MDC_REQUEST_ID = "requestId";
    private static final String MDC_CLIENT_TYPE = "clientType";
    private static final String MDC_APP_VERSION = "appVersion";
    private static final String MDC_DEVICE_ID = "deviceId";
    private static final String HEADER_REQUEST_ID = "X-Request-Id";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = request.getHeader(HEADER_REQUEST_ID);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }
        
        MDC.put(MDC_REQUEST_ID, requestId);
        response.setHeader(HEADER_REQUEST_ID, requestId);
        
        ClientType clientType = ClientHelper.detectClientType(request);
        MDC.put(MDC_CLIENT_TYPE, clientType.getCode());
        
        String appVersion = ClientHelper.getAppVersion(request);
        if (appVersion != null) {
            MDC.put(MDC_APP_VERSION, appVersion);
        }
        
        String deviceId = ClientHelper.getDeviceId(request);
        if (deviceId != null) {
            MDC.put(MDC_DEVICE_ID, deviceId);
        }
        
        request.setAttribute("requestId", requestId);
        request.setAttribute("clientType", clientType);
        request.setAttribute("appVersion", appVersion);
        request.setAttribute("deviceId", deviceId);
        
        if (logger.isDebugEnabled()) {
            logger.debug("请求信息 - RequestId: {}, ClientType: {}, AppVersion: {}, DeviceId: {}, URI: {}", 
                    requestId, clientType.getCode(), appVersion, deviceId, request.getRequestURI());
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(MDC_REQUEST_ID);
        MDC.remove(MDC_CLIENT_TYPE);
        MDC.remove(MDC_APP_VERSION);
        MDC.remove(MDC_DEVICE_ID);
    }
}

