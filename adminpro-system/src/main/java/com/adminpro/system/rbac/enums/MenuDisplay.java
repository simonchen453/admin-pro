package com.adminpro.system.rbac.enums;

import com.adminpro.system.core.common.helper.StringHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 菜单显示状态枚举
 * <p>
 * 定义菜单资源在前端导航中的显示控制状态。
 * 用于控制菜单项是否在导航栏中显示，某些内部功能菜单可能需要隐藏但保留权限访问。
 * </p>
 * <ul>
 *   <li>{@link #SHOW} - 显示，菜单在导航栏中正常显示</li>
 *   <li>{@link #HIDDEN} - 隐藏，菜单不在导航栏中显示，但可能通过其他方式访问</li>
 * </ul>
 *
 * @author simon
 * @date 2019/2/26
 */
public enum MenuDisplay {
    /**
     * 显示状态
     * <p>
     * 表示菜单项在前端导航栏中正常显示。
     * 这是菜单的默认状态，有权限的用户可以看到并点击该菜单。
     * </p>
     */
    SHOW("show", "显示"),
    /**
     * 隐藏状态
     * <p>
     * 表示菜单项不在前端导航栏中显示，但仍可能存在权限配置。
     * 常用于以下场景：
     * <ul>
     *   <li>内部功能页面，通过特定链接或二维码访问</li>
     *   <li>辅助功能页面，从其他页面跳转访问</li>
     *   <li>临时功能或测试页面，不暴露给普通用户</li>
     * </ul>
     * </p>
     */
    HIDDEN("hidden", "隐藏");

    private String code;
    private String desc;

    MenuDisplay(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 验证给定的显示状态码是否有效
     * <p>
     * 检查传入的显示状态码是否匹配本枚举中的任何一个有效状态。
     * 如果状态码为null或不在预定义的状态列表中，则返回false。
     * </p>
     *
     * @param code 待验证的显示状态码
     * @return 如果显示状态码有效返回true，否则返回false
     */
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

    /**
     * 获取菜单显示状态配置列表
     * <p>
     * 将所有菜单显示状态转换为键值对列表格式，便于前端下拉框等组件使用。
     * 每个状态项包含：
     * <ul>
     *   <li>key: 显示状态码</li>
     *   <li>value: 显示状态描述</li>
     * </ul>
     * </p>
     *
     * @return 包含所有菜单显示状态的键值对列表，每个元素为包含key和value的Map
     */
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

    /**
     * 获取菜单显示状态配置映射
     * <p>
     * 将所有菜单显示状态转换为Map映射，key为显示状态码，value为显示状态描述。
     * 使用LinkedHashMap保持状态的枚举声明顺序。
     * </p>
     *
     * @return 显示状态码到显示状态描述的映射关系
     */
    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (MenuDisplay status : EnumSet.allOf(MenuDisplay.class)) {
            map.put(status.getCode(), status.getDesc());
        }
        return map;
    }

    /**
     * 判断给定显示状态码是否为显示状态
     *
     * @param code 待判断的显示状态码
     * @return 如果是显示状态返回true，否则返回false
     */
    public static boolean isShow(String code) {
        return StringHelper.equals(code, SHOW.getCode());
    }

    /**
     * 获取显示状态码
     *
     * @return 显示状态码字符串，用于存储和传输
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取显示状态描述
     *
     * @return 显示状态的中文描述，用于界面展示
     */
    public String getDesc() {
        return desc;
    }
}
