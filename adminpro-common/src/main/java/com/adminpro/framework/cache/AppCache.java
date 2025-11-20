package com.adminpro.framework.cache;

import com.adminpro.core.base.util.SpringUtil;

public abstract class AppCache {
    public static AppCache getInstance() {
        return SpringUtil.getBean(AppCache.class);
    }

    public abstract void set(String cacheName, String key, Object value);

    public abstract void set(String cacheName, String key, Object value, Integer expire);

    public abstract <T> T get(String cacheName, String key, Class<T> clazz);

    public abstract void delete(String cacheName, String key);
}
