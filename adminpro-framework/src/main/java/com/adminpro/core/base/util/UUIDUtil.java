package com.adminpro.core.base.util;

import java.util.UUID;

/**
 * @author simon
 * @date 2016/12/10
 */
public class UUIDUtil {
    private UUIDUtil() {
    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }
}
