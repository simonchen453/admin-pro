package com.adminpro.system.core.common.helper;

/**
 * Redis所有Keys
 */
public class RedisKeys {

    public static String getSysConfigKey(String key) {
        return "sys:config:" + key;
    }
}
