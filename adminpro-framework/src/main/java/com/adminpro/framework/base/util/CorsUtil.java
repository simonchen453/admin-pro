package com.adminpro.framework.base.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
public class CorsUtil {

    private CorsUtil() {
    }

    public static void addCorsResponse(HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(response.getHeader("Access-Control-Allow-Credentials"))) {
            String origin = request.getHeader("Origin");
            List<String> allowedOrigin = new ArrayList<>();
            allowedOrigin.add("localhost");
            allowedOrigin.add("127.0.0.1");
            if (StringUtils.isNotEmpty(origin) && allowedOrigin.stream().anyMatch(origin::contains)) {
                response.addHeader("Access-Control-Allow-Origin", origin);
            }
            response.addHeader("Access-Control-Allow-Methods", "*");
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");

        }
    }
}
