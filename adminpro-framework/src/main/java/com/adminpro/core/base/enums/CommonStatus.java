package com.adminpro.core.base.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 通用状态
 *
 * @author simon
 * @date 2019/2/26
 */
public enum CommonStatus {
    /**
     * 正常
     */
    ACTIVE("active", "正常"),
    /**
     * 待审批
     */
    PENDING_APPROVE("pending_approve", "待审批"),
    /**
     * 停用
     */
    INACTIVE("inactive", "停用");

    private String code;
    private String desc;

    CommonStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (CommonStatus type : EnumSet.allOf(CommonStatus.class)) {
            if (StringUtils.equals(type.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (CommonStatus status : EnumSet.allOf(CommonStatus.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (CommonStatus status : EnumSet.allOf(CommonStatus.class)) {
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
