package com.adminpro.framework.base.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 性别枚举
 * <p>
 * 定义系统中用户的性别类型，用于用户基本信息管理。
 * 提供了三种性别选项以适应不同的用户需求和隐私保护需求。
 * </p>
 * <ul>
 *   <li>{@link #MALE} - 男性</li>
 *   <li>{@link #FEMALE} - 女性</li>
 *   <li>{@link #UNKNOWN} - 保密/未知，用于用户不愿透露性别或性别信息不确定的情况</li>
 * </ul>
 *
 * @author simon
 * @date 2019/2/26
 */
public enum Sex {
    /**
     * 男性
     * <p>
     * 表示用户性别为男性。
     * 用于性别相关的统计分析、个性化服务推荐等场景。
     * </p>
     */
    MALE("male", "男"),
    /**
     * 女性
     * <p>
     * 表示用户性别为女性。
     * 用于性别相关的统计分析、个性化服务推荐等场景。
     * </p>
     */
    FEMALE("female", "女"),
    /**
     * 保密/未知
     * <p>
     * 表示用户性别信息保密或未知。
     * 适用场景：
     * <ul>
     *   <li>用户不愿透露个人性别信息</li>
     *   <li>系统未收集用户性别数据</li>
     *   <li>用户注册时选择不填写性别</li>
     *   <li>性别信息不适用于特定用户（如企业账号）</li>
     * </ul>
     * 这是性别字段的默认值，体现了对用户隐私的保护。
     * </p>
     */
    UNKNOWN("unknown", "保密");

    private String code;
    private String desc;

    Sex(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 验证给定的性别码是否有效
     * <p>
     * 检查传入的性别码是否匹配本枚举中的任何一个有效性别。
     * 如果性别码为null或不在预定义的性别列表中，则返回false。
     * </p>
     *
     * @param code 待验证的性别码
     * @return 如果性别码有效返回true，否则返回false
     */
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

    /**
     * 获取性别配置列表
     * <p>
     * 将所有性别选项转换为键值对列表格式，便于前端下拉框等组件使用。
     * 每个选项项包含：
     * <ul>
     *   <li>key: 性别码</li>
     *   <li>value: 性别描述</li>
     * </ul>
     * </p>
     *
     * @return 包含所有性别选项的键值对列表，每个元素为包含key和value的Map
     */
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

    /**
     * 获取性别配置映射
     * <p>
     * 将所有性别选项转换为Map映射，key为性别码，value为性别描述。
     * 使用LinkedHashMap保持选项的枚举声明顺序。
     * </p>
     *
     * @return 性别码到性别描述的映射关系
     */
    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Sex status : EnumSet.allOf(Sex.class)) {
            map.put(status.getCode(), status.getDesc());
        }
        return map;
    }

    /**
     * 获取性别码
     *
     * @return 性别码字符串，用于存储和传输
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取性别描述
     *
     * @return 性别的中文描述，用于界面展示
     */
    public String getDesc() {
        return desc;
    }
}
