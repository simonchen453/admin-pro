package com.adminpro.system.tools.domains.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * OSS文件状态枚举
 * <p>
 * 定义对象存储服务（OSS）中文件的生命周期状态。
 * 用于跟踪文件从上传到删除的完整生命周期，支持文件管理和清理策略。
 * </p>
 * <ul>
 *   <li>{@link #TEMP} - 临时文件，刚上传待确认的文件</li>
 *   <li>{@link #ACTIVE} - 有效文件，已确认并正常使用的文件</li>
 *   <li>{@link #EXPIRED} - 过期文件，超过保留期限但仍未删除的文件</li>
 *   <li>{@link #DELETED} - 删除的文件，已标记删除等待物理清理</li>
 * </ul>
 *
 * @author simon
 * @date 2019/2/26
 */
public enum OSSStatus {
    /**
     * 临时文件状态
     * <p>
     * 表示文件刚上传到OSS但尚未确认，处于临时状态。
     * 临时文件通常用于以下场景：
     * <ul>
     *   <li>用户上传但尚未提交表单的附件</li>
     *   <li>编辑器中暂存的图片</li>
     *   <li>导入过程中的临时文件</li>
     * </ul>
     * 临时文件可能被定期清理任务自动删除。
     * </p>
     */
    TEMP("temp", "临时文件"),
    /**
     * 有效文件状态
     * <p>
     * 表示文件已被确认并正式使用，处于正常的活跃状态。
     * 这是文件的主要工作状态，文件与业务数据关联，可正常访问和使用。
     * 有效文件不会被自动清理，除非业务主动删除或达到设定的保留期限。
     * </p>
     */
    ACTIVE("active", "有效文件"),
    /**
     * 过期文件状态
     * <p>
     * 表示文件已超过设定的保留期限，处于过期状态。
     * 过期文件通常仍然可访问，但会被标记为待清理状态。
     * 系统清理任务会将过期文件转为删除状态或物理删除。
     * </p>
     */
    EXPIRED("EXPIRED", "过期文件"),

    /**
     * 删除文件状态
     * <p>
     * 表示文件已被用户或系统标记为删除状态。
     * 文件可能处于软删除状态（逻辑删除）或等待物理删除队列中。
     * 删除状态的文件不应再被业务使用，将在合适时机被永久删除。
     * </p>
     */
    DELETED("DELETED", "删除的文件");

    private String code;
    private String desc;

    OSSStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 验证给定的OSS状态码是否有效
     * <p>
     * 检查传入的OSS状态码是否匹配本枚举中的任何一个有效状态。
     * 如果状态码为null或不在预定义的状态列表中，则返回false。
     * </p>
     *
     * @param code 待验证的OSS状态码
     * @return 如果OSS状态码有效返回true，否则返回false
     */
    public static boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }

        boolean valid = false;
        for (OSSStatus type : EnumSet.allOf(OSSStatus.class)) {
            if (StringUtils.equals(type.getCode(), code)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    /**
     * 获取OSS状态配置列表
     * <p>
     * 将所有OSS文件状态转换为键值对列表格式，便于前端下拉框等组件使用。
     * 每个状态项包含：
     * <ul>
     *   <li>key: 状态码</li>
     *   <li>value: 状态描述</li>
     * </ul>
     * </p>
     *
     * @return 包含所有OSS文件状态的键值对列表，每个元素为包含key和value的Map
     */
    public static List<Map<String, String>> getConfigList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (OSSStatus status : EnumSet.allOf(OSSStatus.class)) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("key", status.getCode());
            tmp.put("value", status.getDesc());
            list.add(tmp);
        }
        return list;
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
