package com.adminpro.core.base.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author simon
 */
@Component
public class EnvironmentUtils {

    public static EnvironmentUtils getInstance() {
        return SpringUtil.getBean(EnvironmentUtils.class);
    }

    private static final transient Map<String, String> enVir = System.getenv();
    private static final transient Map<Object, Object> profile = System.getProperties();
    private static final ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<String, String>();

    static {
        cp(enVir, properties);
        cp(profile, properties);
    }

    private static final <K, V> void cp(Map<K, V> source, Map<String, String> target) {
        for (Map.Entry<K, V> entry : source.entrySet()) {
            target.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
    }

    public String get(String k) {
        return properties.get(k);
    }
}
