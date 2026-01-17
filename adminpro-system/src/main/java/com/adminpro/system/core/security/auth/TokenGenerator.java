package com.adminpro.system.core.security.auth;

import com.adminpro.framework.exceptions.BaseRuntimeException;

import java.security.MessageDigest;
import java.util.UUID;

/**
 * Token生成器
 * <p>
 * 负责生成唯一的、不可预测的Token字符串，用于用户认证。
 * 使用UUID和MD5哈希算法确保Token的唯一性和安全性。
 * <p>
 * 工作原理：
 * <ol>
 * <li>生成UUID作为原始字符串</li>
 * <li>使用MD5哈希算法对原始字符串进行加密</li>
 * <li>返回32位十六进制字符串作为Token</li>
 * </ol>
 * <p>
 * 安全特性：
 * <ul>
 * <li>基于UUID：保证全局唯一性</li>
 * <li>MD5哈希：不可逆，无法从Token反推原始值</li>
 * <li>固定长度：生成的Token始终为32位十六进制字符串</li>
 * <li>无状态：不需要存储已生成的Token历史</li>
 * </ul>
 *
 * @author simon
 * @see java.util.UUID
 * @see java.security.MessageDigest
 */
public class TokenGenerator {

    /**
     * 生成Token值
     * <p>
     * 使用UUID生成随机字符串，然后通过MD5哈希转换为32位十六进制Token
     *
     * @return 32位十六进制Token字符串
     */
    public static String generateValue() {
        return generateValue(UUID.randomUUID().toString());
    }

    /**
     * 十六进制字符集
     * <p>
     * 用于将字节数组转换为十六进制字符串
     */
    private static final char[] HEX_CODE = "0123456789abcdef".toCharArray();

    /**
     * 将字节数组转换为十六进制字符串
     * <p>
     * 每个字节转换为两个十六进制字符，确保输出长度固定
     *
     * @param data 字节数组
     * @return 十六进制字符串，如果输入为null则返回null
     */
    public static String toHexString(byte[] data) {
        if (data == null) {
            return null;
        }
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(HEX_CODE[(b >> 4) & 0xF]);
            r.append(HEX_CODE[(b & 0xF)]);
        }
        return r.toString();
    }

    /**
     * 根据指定参数生成Token值
     * <p>
     * 使用MD5哈希算法对输入参数进行加密，生成32位十六进制Token。
     * 相同的输入参数会生成相同的Token，不同的输入参数会生成不同的Token。
     * <p>
     * 注意：此方法使用MD5算法，虽然MD5在密码学上不再安全，
     * 但用于生成Token（而非存储密码）是可接受的。
     *
     * @param param 输入参数（通常是UUID或其他唯一字符串）
     * @return 32位十六进制Token字符串
     * @throws BaseRuntimeException 如果生成Token失败
     */
    public static String generateValue(String param) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(param.getBytes());
            byte[] messageDigest = algorithm.digest();
            return toHexString(messageDigest);
        } catch (Exception e) {
            throw new BaseRuntimeException("生成Token失败", e);
        }
    }
}
