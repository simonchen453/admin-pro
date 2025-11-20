package com.adminpro.core.base.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

public final class LoggerUtil {

    public static String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }

    public static String buildMessage(Object[] args) {
        StringBuilder msg = new StringBuilder();
        boolean isFirst = true;
        if (args != null && args.length > 0) {
            int len = args.length;
            for (int i = 0; i < len; i++) {
                if (!isFirst) {
                    msg.append(", ");
                }
                if (args[i] instanceof Date) {
                    msg.append(DateUtil.formatDateTime((Date) args[i]));
                } else {
                    msg.append(args[i]);
                }
                isFirst = false;
            }
        }
        return msg.toString();
    }

    public static String buildMessage(Map<String, Object> map) {
        StringBuilder msg = new StringBuilder();
        boolean isFirst = true;
        if (map != null && map.size() > 0) {
            Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                Object o = entry.getValue();

                if (!isFirst) {
                    msg.append(", ");
                }
                if (o instanceof Date) {
                    msg.append(key + " : " + DateUtil.formatDateTime((Date) o));
                } else {
                    msg.append(key + " : " + o);
                }
                isFirst = false;
            }
        }
        return msg.toString();
    }

    protected static Map<String, String> extractRequestData(HttpServletRequest request) {
        Map<String, String> data = new HashMap<String, String>();
        Enumeration<?> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paramName = (String) enu.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues != null) {
                if (paramValues.length > 1) {
                    for (int i = 0; i < paramValues.length; i++) {
                        data.put(new StringBuffer().append(paramName).append("[").append(i).append("]").toString(), paramValues[i]);
                    }
                } else {
                    data.put(paramName, paramValues[0]);
                }
            }
        }
        return data;
    }

    private LoggerUtil() {
    }
}
