package com.adminpro.rbac.enums;

import com.adminpro.framework.common.helper.StringHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 菜单显示状态
 *
 * @author simon
 * @date 2019/2/26
 */
public enum MenuDisplay {
    SHOW("show", "显示"),
    /**
     * 隐藏
     */
    HIDDEN("hidden", "隐藏");

    private String code;
    private String desc;

    MenuDisplay(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (MenuDisplay type : EnumSet.allOf(MenuDisplay.class)) {
            if (StringUtils.equals(type.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (MenuDisplay status : EnumSet.allOf(MenuDisplay.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (MenuDisplay status : EnumSet.allOf(MenuDisplay.class)) {
            map.put(status.getCode(), status.getDesc());
        }
        return map;
    }

    public static boolean isShow(String code) {
        return StringHelper.equals(code, SHOW.getCode());
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
