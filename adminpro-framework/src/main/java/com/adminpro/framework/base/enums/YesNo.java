package com.adminpro.framework.base.enums;

import java.util.*;

/**
 * 是否布尔枚举
 * <p>
 * 定义系统中的布尔类型枚举，用于表示是/否的二元状态。
 * 该枚举使用布尔值作为内部码，提供更明确的语义表达。
 * </p>
 * <ul>
 *   <li>{@link #YES} - 是，表示肯定、启用、存在等含义</li>
 *   <li>{@link #NO} - 否，表示否定、禁用、不存在等含义</li>
 * </ul>
 *
 * @author simon
 * @date 2019/2/26
 */
public enum YesNo {
    /**
     * 是
     * <p>
     * 表示布尔值为true，通常用于以下场景：
     * <ul>
     *   <li>功能是否启用</li>
     *   <li>选项是否选中</li>
     *   <li>条件是否满足</li>
     *   <li>权限是否拥有</li>
     *   <li>状态是否激活</li>
     * </ul>
     * </p>
     */
    YES(true, "是"),
    /**
     * 否
     * <p>
     * 表示布尔值为false，通常用于以下场景：
     * <ul>
     *   <li>功能是否禁用</li>
     *   <li>选项是否未选中</li>
     *   <li>条件是否不满足</li>
     *   <li>权限是否不拥有</li>
     *   <li>状态是否未激活</li>
     * </ul>
     * </p>
     */
    NO(false, "否");

    private boolean code;
    private String desc;

    YesNo(Boolean code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 验证给定的布尔码是否有效
     * <p>
     * 检查传入的布尔值是否匹配本枚举中的任何一个有效值。
     * 如果布尔值为null，则返回false。
     * </p>
     *
     * @param code 待验证的布尔值
     * @return 如果布尔值有效返回true，否则返回false
     */
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

    /**
     * 获取是否枚举配置列表
     * <p>
     * 将所有是否选项转换为键值对列表格式，便于前端下拉框等组件使用。
     * 每个选项项包含：
     * <ul>
     *   <li>key: 布尔值</li>
     *   <li>value: 描述文本</li>
     * </ul>
     * </p>
     *
     * @return 包含所有是否选项的键值对列表，每个元素为包含key和value的Map
     */
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

    /**
     * 获取是否枚举配置映射
     * <p>
     * 将所有是否选项转换为Map映射，key为布尔值，value为描述文本。
     * 使用LinkedHashMap保持选项的枚举声明顺序。
     * </p>
     *
     * @return 布尔值到描述文本的映射关系
     */
    public static Map<Boolean, String> getConfigMap() {
        Map<Boolean, String> map = new LinkedHashMap<>();
        for (YesNo status : EnumSet.allOf(YesNo.class)) {
            map.put(status.getCode(), status.getDesc());
        }
        return map;
    }

    /**
     * 获取布尔值码
     *
     * @return 布尔值，用于存储和逻辑判断
     */
    public Boolean getCode() {
        return code;
    }

    /**
     * 获取描述文本
     *
     * @return 描述文本，用于界面展示
     */
    public String getDesc() {
        return desc;
    }
}
