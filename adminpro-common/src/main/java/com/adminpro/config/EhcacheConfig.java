package com.adminpro.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@ConditionalOnProperty(value = "app.cache.ehcache.enabled", havingValue = "true", matchIfMissing = false)
public class EhcacheConfig {
}
