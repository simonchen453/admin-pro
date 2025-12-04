package com.adminpro.tools.domains.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author simon
 * @date 2019/2/26
 */
public enum SessionStatus {
    /**
     * 有效
     */
    ACTIVE("active", "有效"),
    /**
     * 限制
     */
    SUSPEND("suspend", "限制"),
    /**
     * 过期
     */
    EXPIRE("expire", "过期"),
    /**
     * 无效
     */
    KILLED("killed", "强退");

    private String code;
    private String desc;

    SessionStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (SessionStatus type : EnumSet.allOf(SessionStatus.class)) {
            if (StringUtils.equals(type.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (SessionStatus status : EnumSet.allOf(SessionStatus.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (SessionStatus status : EnumSet.allOf(SessionStatus.class)) {
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
