package com.adminpro.framework.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "app.cache.ehcache.enabled", havingValue = "true", matchIfMissing = false)
public class AppEhcache extends AppCache {
    private static net.sf.ehcache.CacheManager ehCacheManager;

    @Autowired(required = false)
    public void setManager(CacheManager manager) {
        // 直接使用 EhCache CacheManager，不依赖 Spring 的包装
        // 即使没有 Spring 的 CacheManager，也可以直接使用 EhCache
        if (ehCacheManager == null) {
            try {
                ehCacheManager = net.sf.ehcache.CacheManager.create();
            } catch (Exception e) {
                throw new RuntimeException("无法初始化 EhCache CacheManager", e);
            }
        }
    }

    private static Cache cache(String cacheName) {
        if (ehCacheManager == null) {
            try {
                ehCacheManager = net.sf.ehcache.CacheManager.create();
            } catch (Exception e) {
                throw new RuntimeException("无法初始化 EhCache CacheManager", e);
            }
        }
        if (!ehCacheManager.cacheExists(cacheName)) {
            ehCacheManager.addCache(cacheName);
        }
        return ehCacheManager.getCache(cacheName);
    }

    @Override
    public void set(String cacheName, String key, Object value) {
        Element e = new Element(key, value);
        cache(cacheName).put(e);
    }

    @Override
    public void set(String cacheName, String key, Object value, Integer expire) {
        Element e = new Element(key, value);
        //不设置则使用xml配置
        if (expire != null) {
            e.setTimeToIdle(expire);
        }
        cache(cacheName).put(e);
    }

    @Override
    public <T> T get(String cacheName, String key, Class<T> clazz) {
        Element e = cache(cacheName).get(key);
        if (e != null) {
            return (T) e.getObjectValue();
        }
        return null;
    }

    @Override
    public void delete(String cacheName, String key) {
        Cache cache = cache(cacheName);
        cache.remove(key);
    }
}
