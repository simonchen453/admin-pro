package com.adminpro.system.rbac.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 部门状态枚举
 * <p>
 * 定义系统中组织部门的状态，用于控制部门的可用性和业务处理能力。
 * 部门状态影响该部门下用户和业务操作的有效性。
 * </p>
 * <ul>
 *   <li>{@link #ACTIVE} - 有效状态，部门正常运营，可以处理业务</li>
 *   <li>{@link #LOCK} - 锁定状态，部门被锁定，限制相关业务操作</li>
 * </ul>
 *
 * @author simon
 */
public enum DeptStatus {
    /**
     * 有效状态
     * <p>
     * 表示部门处于正常运营状态，可以正常处理业务和管理用户。
     * 这是部门创建后的默认状态，也是系统运行中部门应保持的正常状态。
     * 有效状态下的部门可以：
     * <ul>
     *   <li>添加和管理部门成员</li>
     *   <li>分配和处理业务任务</li>
     *   <li>参与权限和角色分配</li>
     * </ul>
     * </p>
     */
    ACTIVE("active", "有效的"),
    /**
     * 锁定状态
     * <p>
     * 表示部门被锁定，通常由于以下原因：
     * <ul>
     *   <li>组织架构调整，部门暂时停用</li>
     *   <li>业务重组，部门职能转移中</li>
     *   <li>违规或安全问题，部门被临时冻结</li>
     *   <li>部门撤销流程进行中</li>
     * </ul>
     * 锁定状态下的部门限制操作，可能影响其下用户的业务处理。
     * </p>
     */
    LOCK("lock", "锁定");

    private String code;
    private String desc;

    DeptStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 验证给定的部门状态码是否有效
     * <p>
     * 检查传入的部门状态码是否匹配本枚举中的任何一个有效状态。
     * 如果状态码为null或不在预定义的状态列表中，则返回false。
     * </p>
     *
     * @param code 待验证的部门状态码
     * @return 如果部门状态码有效返回true，否则返回false
     */
    public static boolean isValidStatus(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (DeptStatus userStatus : EnumSet.allOf(DeptStatus.class)) {
            if (StringUtils.equals(userStatus.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    /**
     * 获取部门状态配置列表
     * <p>
     * 将所有部门状态转换为键值对列表格式，便于前端下拉框等组件使用。
     * 每个状态项包含：
     * <ul>
     *   <li>key: 状态码</li>
     *   <li>value: 状态描述</li>
     * </ul>
     * </p>
     *
     * @return 包含所有部门状态的键值对列表，每个元素为包含key和value的Map
     */
    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (DeptStatus status : EnumSet.allOf(DeptStatus.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
    }

    /**
     * 获取部门状态配置映射
     * <p>
     * 将所有部门状态转换为Map映射，key为状态码，value为状态描述。
     * 使用LinkedHashMap保持状态的枚举声明顺序。
     * </p>
     *
     * @return 状态码到状态描述的映射关系
     */
    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (DeptStatus status : EnumSet.allOf(DeptStatus.class)) {
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
