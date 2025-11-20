package com.adminpro.core.base.enums;

import java.util.*;

/**
 * 是否
 *
 * @author simon
 * @date 2019/2/26
 */
public enum YesNo {
    /**
     * 是
     */
    YES(true, "是"),
    /**
     * 停用
     */
    NO(false, "否");

    private boolean code;
    private String desc;

    YesNo(Boolean code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isValidCode(Boolean code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (YesNo type : EnumSet.allOf(YesNo.class)) {
            if (type.getCode().booleanValue() == code.booleanValue()) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static List<Map<String, Object>> getConfigList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YesNo status : EnumSet.allOf(YesNo.class)) {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    public static Map<Boolean, String> getConfigMap() {
        Map<Boolean, String> map = new LinkedHashMap<>();
        for (YesNo status : EnumSet.allOf(YesNo.class)) {
            map.put(status.getCode(), status.getDesc());
        }
        return map;
    }

    public Boolean getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
