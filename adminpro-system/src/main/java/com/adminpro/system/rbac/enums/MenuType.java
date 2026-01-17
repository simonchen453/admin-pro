package com.adminpro.system.rbac.enums;

import com.adminpro.system.core.common.helper.StringHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 菜单类型枚举
 * <p>
 * 定义系统中菜单资源的类型，用于区分不同层级的权限控制粒度。
 * 菜单类型包括：目录、菜单、按钮三种类型，形成树形权限结构。
 * </p>
 * <ul>
 *   <li>{@link #CATALOG} - 目录，作为菜单的分组容器，通常不对应具体页面</li>
 *   <li>{@link #MENU} - 菜单，对应具体的页面或功能模块</li>
 *   <li>{@link #BUTTON} - 按钮，对应页面内的操作按钮权限</li>
 * </ul>
 *
 * @author simon
 * @date 2019/2/26
 */
public enum MenuType {
    /**
     * 目录类型
     * <p>
     * 表示菜单资源为目录类型，通常用作菜单的分组容器。
     * 目录一般不对应具体的页面，用于组织和分类下级菜单。
     * 在前端导航中通常显示为可展开的菜单分组。
     * </p>
     */
    CATALOG("M", "目录"),
    /**
     * 菜单类型
     * <p>
     * 表示菜单资源为具体的菜单项，对应系统中的某个页面或功能模块。
     * 菜单通常会关联一个具体的URL路由，用户点击后跳转到对应的功能页面。
     * 在权限控制中，控制菜单即控制用户能访问哪些功能页面。
     * </p>
     */
    MENU("C", "菜单"),
    /**
     * 按钮类型
     * <p>
     * 表示菜单资源为按钮类型，用于控制页面内的具体操作权限。
     * 按钮权限属于细粒度权限控制，用于控制用户在某个页面内能执行哪些操作，
     * 如：新增、编辑、删除、导出等功能按钮的显示和可用性。
     * </p>
     */
    BUTTON("F", "按钮");

    private String code;
    private String desc;

    MenuType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 验证给定的类型码是否有效
     * <p>
     * 检查传入的类型码是否匹配本枚举中的任何一个有效类型。
     * 如果类型码为null或不在预定义的类型列表中，则返回false。
     * </p>
     *
     * @param code 待验证的类型码
     * @return 如果类型码有效返回true，否则返回false
     */
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

    /**
     * 获取菜单类型配置列表
     * <p>
     * 将所有菜单类型转换为键值对列表格式，便于前端下拉框等组件使用。
     * 每个类型项包含：
     * <ul>
     *   <li>key: 类型码</li>
     *   <li>value: 类型描述</li>
     * </ul>
     * </p>
     *
     * @return 包含所有菜单类型的键值对列表，每个元素为包含key和value的Map
     */
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

    /**
     * 获取菜单类型配置映射
     * <p>
     * 将所有菜单类型转换为Map映射，key为类型码，value为类型描述。
     * 使用LinkedHashMap保持类型的枚举声明顺序。
     * </p>
     *
     * @return 类型码到类型描述的映射关系
     */
    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (MenuType status : EnumSet.allOf(MenuType.class)) {
            map.put(status.getCode(), status.getDesc());
        }
        return map;
    }

    /**
     * 判断给定类型码是否为目录类型
     *
     * @param code 待判断的类型码
     * @return 如果是目录类型返回true，否则返回false
     */
    public static boolean isCategory(String code) {
        return StringHelper.equals(code, CATALOG.getCode());
    }

    /**
     * 判断给定类型码是否为按钮类型
     *
     * @param code 待判断的类型码
     * @return 如果是按钮类型返回true，否则返回false
     */
    public static boolean isButton(String code) {
        return StringHelper.equals(code, BUTTON.getCode());
    }

    /**
     * 获取类型码
     *
     * @return 类型码字符串，用于存储和传输
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取类型描述
     *
     * @return 类型的中文描述，用于界面展示
     */
    public String getDesc() {
        return desc;
    }
}
