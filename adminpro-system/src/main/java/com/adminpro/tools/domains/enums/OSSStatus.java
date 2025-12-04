package com.adminpro.tools.domains.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author simon
 * @date 2019/2/26
 */
public enum OSSStatus {
    /**
     * 临时文件
     */
    TEMP("temp", "临时文件"),
    /**
     * 有效文件
     */
    ACTIVE("active", "有效文件"),
    /**
     * 过期文件
     */
    EXPIRED("EXPIRED", "过期文件"),

    /**
     * 删除的文件
     */
    DELETED("DELETED", "删除的文件");

    private String code;
    private String desc;

    OSSStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (OSSStatus type : EnumSet.allOf(OSSStatus.class)) {
            if (StringUtils.equals(type.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (OSSStatus status : EnumSet.allOf(OSSStatus.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
