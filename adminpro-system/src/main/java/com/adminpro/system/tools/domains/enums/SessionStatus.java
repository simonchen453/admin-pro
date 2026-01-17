package com.adminpro.system.tools.domains.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 会话状态枚举
 * <p>
 * 定义用户登录会话的生命周期状态，用于会话管理和安全控制。
 * 会话状态影响用户的登录有效性和系统访问权限。
 * </p>
 * <ul>
 *   <li>{@link #ACTIVE} - 有效状态，会话正常活跃</li>
 *   <li>{@link #SUSPEND} - 限制状态，会话被暂时限制使用</li>
 *   <li>{@link #EXPIRE} - 过期状态，会话超时失效</li>
 *   <li>{@link #KILLED} - 强退状态，会话被强制终止</li>
 * </ul>
 *
 * @author simon
 * @date 2019/2/26
 */
public enum SessionStatus {
    /**
     * 有效状态
     * <p>
     * 表示用户会话处于正常活跃状态，可以正常访问系统资源。
     * 这是用户登录成功后的默认状态，会话在有效期内且未被限制。
     * 系统会定期刷新活跃会话的过期时间。
     * </p>
     */
    ACTIVE("active", "有效"),
    /**
     * 限制状态
     * <p>
     * 表示会话被暂时限制使用，通常由于以下原因：
     * <ul>
     *   <li>检测到异常登录行为或安全风险</li>
     *   <li>用户权限被临时调整</li>
     *   <li>系统安全策略触发</li>
     *   <li>多设备登录冲突</li>
     * </ul>
     * 限制状态的会话可能被要求重新验证身份或被部分功能禁用。
     * </p>
     */
    SUSPEND("suspend", "限制"),
    /**
     * 过期状态
     * <p>
     * 表示会话已超过设定的有效期，处于过期失效状态。
     * 会话过期的常见原因：
     * <ul>
     *   <li>用户长时间未操作，会话超时</li>
     *   <li>达到会话最大存活时间</li>
     *   <li>记住登录功能的期限届满</li>
     *   <li>单次登录会话到期</li>
     * </ul>
     * 过期的会话需要用户重新登录才能继续访问系统。
     * </p>
     */
    EXPIRE("expire", "过期"),
    /**
     * 强退状态
     * <p>
     * 表示会话被强制终止，用户被系统踢出登录。
     * 会话被强退的常见原因：
     * <ul>
     *   <li>管理员强制用户下线</li>
     *   <li>用户在其他设备登录，挤占当前会话</li>
     *   <li>用户账户状态变更（停用、锁定等）</li>
     *   <li>检测到严重安全威胁，紧急切断会话</li>
     *   <li>用户主动退出所有设备</li>
     * </ul>
     * 强退状态的会话无法继续使用，必须重新登录。
     * </p>
     */
    KILLED("killed", "强退");

    private String code;
    private String desc;

    SessionStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 验证给定的会话状态码是否有效
     * <p>
     * 检查传入的会话状态码是否匹配本枚举中的任何一个有效状态。
     * 如果状态码为null或不在预定义的状态列表中，则返回false。
     * </p>
     *
     * @param code 待验证的会话状态码
     * @return 如果会话状态码有效返回true，否则返回false
     */
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

    /**
     * 获取会话状态配置列表
     * <p>
     * 将所有会话状态转换为键值对列表格式，便于前端下拉框等组件使用。
     * 每个状态项包含：
     * <ul>
     *   <li>key: 状态码</li>
     *   <li>value: 状态描述</li>
     * </ul>
     * </p>
     *
     * @return 包含所有会话状态的键值对列表，每个元素为包含key和value的Map
     */
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

    /**
     * 获取会话状态配置映射
     * <p>
     * 将所有会话状态转换为Map映射，key为状态码，value为状态描述。
     * 使用LinkedHashMap保持状态的枚举声明顺序。
     * </p>
     *
     * @return 状态码到状态描述的映射关系
     */
    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (SessionStatus status : EnumSet.allOf(SessionStatus.class)) {
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
