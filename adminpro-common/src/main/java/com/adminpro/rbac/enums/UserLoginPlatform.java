package com.adminpro.rbac.enums;

import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.rbac.common.RbacConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public enum UserLoginPlatform {
    /**
     * 因特网用户
     */
    INTERNET("INTERNET", "因特网用户"),

    /**
     * 局域网用户
     */
    INTRANET("INTRANET", "局域网用户"),

    /**
     * 系统用户
     */
    SYSTEM("SYSTEM", "系统用户");

    private String code;
    private String desc;

    private UserLoginPlatform(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isValidLoginPlatform(String loginType) {
        if (StringUtils.isEmpty(loginType)) {
            return false;
        }

        boolean valid = false;
        for (UserLoginPlatform userLoginPlatform : EnumSet.allOf(UserLoginPlatform.class)) {
            if (userLoginPlatform.toString().equals(loginType)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static String getUserDomain(String platform) {
        String userDomain = null;
        if (StringUtils.equals(platform, UserLoginPlatform.SYSTEM.getCode())) {
            userDomain = RbacConstants.SYSTEM_DOMAIN;
        } else if (StringUtils.equals(platform, UserLoginPlatform.INTERNET.getCode())) {
            userDomain = RbacConstants.INTERNET_DOMAIN;
        } else if (StringUtils.equals(platform, UserLoginPlatform.INTRANET.getCode())) {
            userDomain = RbacConstants.INTRANET_DOMAIN;
        } else {
            throw new BaseRuntimeException("非法登陆平台");
        }
        return userDomain;
    }

    public static String getPlatForm(String domain) {
        String platform = null;
        if (StringUtils.equals(domain, RbacConstants.SYSTEM_DOMAIN)) {
            platform = UserLoginPlatform.SYSTEM.getCode();
        } else if (StringUtils.equals(domain, RbacConstants.INTERNET_DOMAIN)) {
            platform = UserLoginPlatform.INTERNET.getCode();
        } else if (StringUtils.equals(domain, RbacConstants.INTRANET_DOMAIN)) {
            platform = UserLoginPlatform.INTRANET.getCode();
        } else {
            throw new BaseRuntimeException("非法User Domain");
        }
        return platform;
    }

    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (UserLoginPlatform status : EnumSet.allOf(UserLoginPlatform.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (UserLoginPlatform status : EnumSet.allOf(UserLoginPlatform.class)) {
            map.put(status.getCode(), status.getDesc());
        }
        return map;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return code;
    }
}
