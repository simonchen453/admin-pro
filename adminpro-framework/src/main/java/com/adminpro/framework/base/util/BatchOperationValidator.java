package com.adminpro.framework.base.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 批量操作参数验证工具类
 *
 * @author simon
 * @date 2026-01-16
 */
public class BatchOperationValidator {

    /**
     * 默认最大批量操作数量
     */
    private static final int DEFAULT_MAX_BATCH_SIZE = 1000;

    /**
     * 最大批量操作数量（可通过配置覆盖）
     */
    private static int maxBatchSize = DEFAULT_MAX_BATCH_SIZE;

    /**
     * 设置最大批量操作数量
     *
     * @param size 最大数量
     */
    public static void setMaxBatchSize(int size) {
        maxBatchSize = size;
    }

    /**
     * 获取最大批量操作数量
     *
     * @return 最大数量
     */
    public static int getMaxBatchSize() {
        return maxBatchSize;
    }

    /**
     * 验证并解析 ID 字符串
     *
     * @param ids ID 字符串，格式：id1,id2,id3
     * @return ID 列表
     * @throws IllegalArgumentException 如果参数无效
     */
    public static List<String> validateAndParseIds(String ids) {
        return validateAndParseIds(ids, maxBatchSize);
    }

    /**
     * 验证并解析 ID 字符串
     *
     * @param ids ID 字符串，格式：id1,id2,id3
     * @param maxSize 最大数量限制
     * @return ID 列表
     * @throws IllegalArgumentException 如果参数无效
     */
    public static List<String> validateAndParseIds(String ids, int maxSize) {
        if (StringUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("IDs不能为空");
        }

        String[] idArray = ids.split(",");
        if (idArray.length == 0) {
            throw new IllegalArgumentException("IDs不能为空");
        }

        if (idArray.length > maxSize) {
            throw new IllegalArgumentException("批量操作数量不能超过" + maxSize);
        }

        List<String> result = Arrays.stream(idArray)
            .map(String::trim)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new IllegalArgumentException("IDs不能为空");
        }

        return result;
    }

    /**
     * 验证 ID 字符串但不解析
     *
     * @param ids ID 字符串
     * @return 是否有效
     */
    public static boolean isValid(String ids) {
        try {
            validateAndParseIds(ids);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 验证 ID 字符串但不解析（带自定义最大数量）
     *
     * @param ids ID 字符串
     * @param maxSize 最大数量
     * @return 是否有效
     */
    public static boolean isValid(String ids, int maxSize) {
        try {
            validateAndParseIds(ids, maxSize);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 验证并解析 ID 字符串，返回数组（为了兼容旧代码）
     *
     * @param ids ID 字符串
     * @return ID 数组
     * @throws IllegalArgumentException 如果参数无效
     */
    public static String[] validateAndParseAsArray(String ids) {
        List<String> list = validateAndParseIds(ids);
        return list.toArray(new String[0]);
    }

    /**
     * 验证列表大小
     *
     * @param list 列表
     * @param maxSize 最大数量
     * @throws IllegalArgumentException 如果列表为空或超过限制
     */
    public static void validateListSize(List<?> list, int maxSize) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("列表不能为空");
        }
        if (list.size() > maxSize) {
            throw new IllegalArgumentException("批量操作数量不能超过" + maxSize);
        }
    }

    /**
     * 验证列表大小（使用默认最大数量）
     *
     * @param list 列表
     * @throws IllegalArgumentException 如果列表为空或超过限制
     */
    public static void validateListSize(List<?> list) {
        validateListSize(list, maxBatchSize);
    }
}
