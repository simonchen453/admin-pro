package com.adminpro.rbac.enums;

import com.adminpro.framework.common.helper.StringHelper;

import java.util.*;

/**
 * @author simon
 */
public enum UserStatus {
    /**
     * 正常
     */
    ACTIVE("active", "正常"),
    /**
     * 停用
     */
    INACTIVE("inactive", "停用"),
    /**
     * 锁定
     */
    LOCKED("locked", "锁定");

    private String code;
    private String desc;

    UserStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isValidStatus(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (UserStatus userStatus : EnumSet.allOf(UserStatus.class)) {
            if (StringHelper.equals(userStatus.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (UserStatus status : EnumSet.allOf(UserStatus.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (UserStatus status : EnumSet.allOf(UserStatus.class)) {
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
}
