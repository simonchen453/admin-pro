package com.adminpro.rbac.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author simon
 */
public enum DeptStatus {
    /**
     * 注册中
     */
    ACTIVE("active", "有效的"),
    /**
     * 锁定
     */
    LOCK("lock", "锁定");

    private String code;
    private String desc;

    DeptStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isValidStatus(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (DeptStatus userStatus : EnumSet.allOf(DeptStatus.class)) {
            if (StringUtils.equals(userStatus.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (DeptStatus status : EnumSet.allOf(DeptStatus.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (DeptStatus status : EnumSet.allOf(DeptStatus.class)) {
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
