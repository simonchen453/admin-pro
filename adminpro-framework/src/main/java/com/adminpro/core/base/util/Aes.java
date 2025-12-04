package com.adminpro.core.base.util;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;

@Slf4j
public class Aes {
    /**
     * @author ngh
     * AES128 算法
     * <p>
     * CBC 模式
     * <p>
     * PKCS7Padding 填充模式
     * <p>
     * CBC模式需要添加偏移量参数iv，必须16位
     * 密钥 sessionKey，必须16位
     * <p>
     * 介于java 不支持PKCS7Padding，只支持PKCS5Padding 但是PKCS7Padding 和 PKCS5Padding 没有什么区别
     * 要实现在java端用PKCS7Padding填充，需要用到bouncycastle组件来实现
     */
    public static final String AES_KEY = ConfigUtil.getString("app.aes.key");
    // 偏移量 16位
    public static final String AES_IV = ConfigUtil.getString("app.aes.iv");

    // 算法名称
    private static final String KEY_ALGORITHM = "AES";
    // 加解密算法/模式/填充方式
    private final static String algorithmStr = ConfigUtil.getString("app.aes.algorithm", "AES/CBC/PKCS7Padding");  // "AES/ECB/PKCS7Padding";

    private static Cipher cipher;

    static {
        init();
    }

    public static void init() {
        // 初始化
        Security.addProvider(new BouncyCastleProvider());
        try {
            // 初始化cipher
            cipher = Cipher.getInstance(algorithmStr, "BC");
        } catch (NoSuchAlgorithmException e) {
            log.error("AES init fail:", e);
        } catch (NoSuchPaddingException e) {
            log.error("AES init fail:", e);
        } catch (NoSuchProviderException e) {
            log.error("AES init fail:", e);
        }
    }
    /**
     * 加密方法
     *
     * @param content
     *            要加密的字符串
     * @param key
     *            加密密钥
     * @param iv
     *            偏移量
     * @return
     */
    public static String encrypt(String content, Key key, IvParameterSpec iv) {
        byte[] encryptedText = null;
        byte[] contentByte = content.getBytes();
        init();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            encryptedText = cipher.doFinal(contentByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(Hex.encode(encryptedText));
    }

    public static byte[] encrypt(String content, Key key) {
        byte[] encryptedText = null;
        byte[] contentByte = content.getBytes();
        init();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedText = cipher.doFinal(contentByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText;
    }

    public static String encrypt(String content){
        String aesKey = AES_KEY;
        String aesIv = AES_IV;
        Key key = generateKey(aesKey);
        IvParameterSpec iv = generateIV(aesIv);
        return encrypt(content, key, iv);
    }

    public static String decrypt(String content){
        String aesKey = AES_KEY;
        String aesIv = AES_IV;
        Key key = generateKey(aesKey);
        IvParameterSpec iv = generateIV(aesIv);
        return decrypt(content, key, iv);
    }

    public static Key generateKey(String key){
        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        int base = 16;
        if (keyBytes.length % base != 0) {
            int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }

        Key keySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        return keySpec;
    }

    public static IvParameterSpec generateIV(String iv) {
        return new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解密方法
     *
     * @param encryptedData
     *            要解密的字符串
     * @param key
     *            加密密钥
     * @param iv
     *            偏移量
     * @return
     */
    public static String decrypt(String encryptedData, Key key, IvParameterSpec iv) {
        byte[] encryptedText = null;
        byte[] encryptedDataByte = Hex.decode(encryptedData);
        init();
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            encryptedText = cipher.doFinal(encryptedDataByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(encryptedText);
    }

    public static void main(String[] args) {
        String sn = "8680220675926050";
        String key = "haoyixiu$123456";
        Key key1 = generateKey(key);
        byte[] encrypt = encrypt(sn, key1);
        System.out.println(sn.length());
        System.out.println(Base64Util.encode(encrypt));
    }
}
