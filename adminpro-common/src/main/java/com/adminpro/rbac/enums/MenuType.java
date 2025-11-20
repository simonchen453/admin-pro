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
public enum MenuType {
    /**
     * 目录
     */
    CATALOG("M", "目录"),
    /**
     * 菜单
     */
    MENU("C", "菜单"),
    /**
     * 按钮
     */
    BUTTON("F", "按钮");

    private String code;
    private String desc;

    MenuType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (MenuType type : EnumSet.allOf(MenuType.class)) {
            if (StringUtils.equals(type.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (MenuType status : EnumSet.allOf(MenuType.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (MenuType status : EnumSet.allOf(MenuType.class)) {
            map.put(status.getCode(), status.getDesc());
        }
        return map;
    }

    public static boolean isCategory(String code) {
        return StringHelper.equals(code, CATALOG.getCode());
    }

    public static boolean isButton(String code) {
        return StringHelper.equals(code, BUTTON.getCode());
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
