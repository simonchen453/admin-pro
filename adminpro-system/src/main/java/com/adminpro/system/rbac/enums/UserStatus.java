package com.adminpro.system.rbac.enums;

import com.adminpro.system.core.common.helper.StringHelper;

import java.util.*;

/**
 * 用户状态枚举
 * <p>
 * 定义系统中用户账户的各种状态，用于控制用户账户的可用性和访问权限。
 * 用户状态包括：正常、停用、锁定三种状态。
 * </p>
 * <ul>
 *   <li>{@link #ACTIVE} - 正常状态，用户可以正常登录和操作系统</li>
 *   <li>{@link #INACTIVE} - 停用状态，用户账户被禁用，无法登录系统</li>
 *   <li>{@link #LOCKED} - 锁定状态，用户账户被临时锁定，通常由于多次登录失败等原因</li>
 * </ul>
 *
 * @author simon
 */
public enum UserStatus {
    /**
     * 正常状态
     * <p>
     * 表示用户账户处于正常激活状态，可以正常登录系统并访问其权限范围内的所有功能。
     * 这是用户账户的默认和推荐状态。
     * </p>
     */
    ACTIVE("active", "正常"),
    /**
     * 停用状态
     * <p>
     * 表示用户账户已被管理员手动停用或因违规等原因被系统自动停用。
     * 处于此状态的用户无法登录系统。
     * </p>
     */
    INACTIVE("inactive", "停用"),
    /**
     * 锁定状态
     * <p>
     * 表示用户账户被临时锁定，通常由于以下原因：
     * <ul>
     *   <li>连续多次登录失败</li>
     *   <li>安全策略触发</li>
     *   <li>异常操作检测</li>
     * </ul>
     * 锁定状态下的用户无法登录，通常需要等待锁定时间到期或由管理员手动解锁。
     * </p>
     */
    LOCKED("locked", "锁定");

    private String code;
    private String desc;

    UserStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 验证给定的状态码是否有效
     * <p>
     * 检查传入的状态码是否匹配本枚举中的任何一个有效状态。
     * 如果状态码为null或不在预定义的状态列表中，则返回false。
     * </p>
     *
     * @param code 待验证的状态码
     * @return 如果状态码有效返回true，否则返回false
     */
    public static boolean isValidStatus(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (UserStatus userStatus : EnumSet.allOf(UserStatus.class)) {
            if (StringHelper.equals(userStatus.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    /**
     * 获取用户状态配置列表
     * <p>
     * 将所有用户状态转换为键值对列表格式，便于前端下拉框等组件使用。
     * 每个状态项包含：
     * <ul>
     *   <li>key: 状态码</li>
     *   <li>value: 状态描述</li>
     * </ul>
     * </p>
     *
     * @return 包含所有用户状态的键值对列表，每个元素为包含key和value的Map
     */
    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (UserStatus status : EnumSet.allOf(UserStatus.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    /**
     * 获取用户状态配置映射
     * <p>
     * 将所有用户状态转换为Map映射，key为状态码，value为状态描述。
     * 使用LinkedHashMap保持状态的枚举声明顺序。
     * </p>
     *
     * @return 状态码到状态描述的映射关系
     */
    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (UserStatus status : EnumSet.allOf(UserStatus.class)) {
            map.put(status.getCode(), status.getDesc());
        }
        return map;
    }

    /**
     * 获取状态码
     *
     * @return 状态码字符串，用于存储和传输
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取状态描述
     *
     * @return 状态的中文描述，用于界面展示
     */
    public String getDesc() {
        return desc;
    }
}
