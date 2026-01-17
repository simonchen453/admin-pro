package com.adminpro.framework.base.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 通用状态枚举
 * <p>
 * 定义系统中通用的状态类型，适用于多种业务场景的统一状态管理。
 * 该枚举提供了最常用的基础状态，可作为其他模块状态定义的参考标准。
 * </p>
 * <ul>
 *   <li>{@link #ACTIVE} - 正常状态，表示实体处于活跃可用状态</li>
 *   <li>{@link #PENDING_APPROVE} - 待审批状态，表示实体等待审批流程</li>
 *   <li>{@link #INACTIVE} - 停用状态，表示实体被停用不可用</li>
 * </ul>
 *
 * @author simon
 * @date 2019/2/26
 */
public enum CommonStatus {
    /**
     * 正常状态
     * <p>
     * 表示实体处于正常的活跃状态，可以正常使用和访问。
     * 这是大多数实体的默认状态，表示已创建、已激活且未被限制。
     * 正常状态的实体可以参与业务流程，被用户查询和操作。
     * </p>
     */
    ACTIVE("active", "正常"),
    /**
     * 待审批状态
     * <p>
     * 表示实体处于等待审批的状态，通常用于以下场景：
     * <ul>
     *   <li>用户注册后等待管理员审核</li>
     *   <li>数据变更提交后等待审批</li>
     *   <li>敏感操作请求待处理</li>
     *   <li>资源申请等待授权</li>
     * </ul>
     * 待审批状态下，实体的部分功能可能被限制，直到审批通过。
     * </p>
     */
    PENDING_APPROVE("pending_approve", "待审批"),
    /**
     * 停用状态
     * <p>
     * 表示实体已被停用或禁用，无法正常使用。
     * 停用状态的常见原因：
     * <ul>
     *   <li>管理员手动停用</li>
     *   <li>实体已完成历史使命，不再需要</li>
     *   <li>违规或政策原因被系统停用</li>
     *   <li>有效期届满自动停用</li>
     * </ul>
     * 停用状态的实体通常保留数据，但限制业务操作。
     * </p>
     */
    INACTIVE("inactive", "停用");

    private String code;
    private String desc;

    CommonStatus(String code, String desc) {
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

    /**
     * 获取通用状态配置列表
     * <p>
     * 将所有通用状态转换为键值对列表格式，便于前端下拉框等组件使用。
     * 每个状态项包含：
     * <ul>
     *   <li>key: 状态码</li>
     *   <li>value: 状态描述</li>
     * </ul>
     * </p>
     *
     * @return 包含所有通用状态的键值对列表，每个元素为包含key和value的Map
     */
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

    /**
     * 获取通用状态配置映射
     * <p>
     * 将所有通用状态转换为Map映射，key为状态码，value为状态描述。
     * 使用LinkedHashMap保持状态的枚举声明顺序。
     * </p>
     *
     * @return 状态码到状态描述的映射关系
     */
    public static Map<String, String> getConfigMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (CommonStatus status : EnumSet.allOf(CommonStatus.class)) {
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
