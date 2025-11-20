package com.adminpro.rbac.api;

/**
 * 设备类型接口
 * spring-mobile-device 已停止维护，使用简单的 Device 接口替代
 * 
 * @author simon
 */
public interface Device {
    boolean isNormal();
    boolean isMobile();
    boolean isTablet();
}

