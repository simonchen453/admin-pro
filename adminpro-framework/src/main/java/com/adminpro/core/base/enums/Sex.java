package com.adminpro.core.base.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 性别
 *
 * @author simon
 * @date 2019/2/26
 */
public enum Sex {
    /**
     * 男
     */
    MALE("male", "男"),
    /**
     * 女
     */
    FEMALE("female", "女"),
    /**
     * 未知
     */
    UNKNOWN("unknown", "未知");

    private String code;
    private String desc;

    Sex(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (Sex type : EnumSet.allOf(Sex.class)) {
            if (StringUtils.equals(type.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (Sex status : EnumSet.allOf(Sex.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Sex status : EnumSet.allOf(Sex.class)) {
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
