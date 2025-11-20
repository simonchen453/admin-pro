package com.adminpro.core.base.util;

import com.adminpro.core.exceptions.SysException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * SecurityUtil
 *
 * @author simon
 */
public final class SecurityUtil {

    private static final SecureRandom secureRandom = new SecureRandom();    //threadsafe

    private static final String AES_KEY = "ThisIsASecretKey";
    private static final String AES_IV_PARAM = "+ThisIsIvParam.+";

    private static IvParameterSpec initVector;
    private static SecretKeySpec keySpec;
    private static Cipher aesCipher;

    static {
        try {
            initVector = new IvParameterSpec(AES_IV_PARAM.getBytes(StandardCharsets.UTF_8));
            keySpec = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException x) {
            throw new SysException("Failed to init the AES cipher.", x);
        }
    }

    public static String genToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }

    public static String encodeBase64(String in) {
        if (in == null) {
            return null;
        }

        return Base64.getEncoder().encodeToString(in.getBytes(StandardCharsets.UTF_8));
    }

    public static String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static String encodeBase64Url(String url) {
        if (StringUtil.isBlank(url)) {
            return null;
        }

        return Base64.getUrlEncoder().encodeToString(url.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] decodeBase64(String in) {
        if (StringUtil.isBlank(in)) {
            return null;
        }

        return Base64.getDecoder().decode(in.getBytes(StandardCharsets.UTF_8));
    }

    public static String md5(String clearText) {
        return md5(clearText, null);
    }

    public static String md5(String clearText, String salt) {
        if (StringUtil.isBlank(clearText)) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            if (StringUtil.isNotBlank(salt)) {
                md.update(salt.getBytes(StandardCharsets.UTF_8));
            }
            md.update(clearText.getBytes(StandardCharsets.UTF_8));

            byte[] hashInBytes = md.digest();
            // convert bytes to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException x) {
            throw new SysException("Error occurred on MD5 hashing", x);
        }
    }

    public static String encryptInAes(String plainText) {
        try {
            aesCipher.init(Cipher.ENCRYPT_MODE, keySpec, initVector);
            byte[] encryptedData = aesCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return toHexString(encryptedData);
        } catch (Exception x) {
            throw new SysException("Error occurred on AES encryption.", x);
        }
    }

    public static String decryptInAes(String encryptedText) {
        try {
            aesCipher.init(Cipher.DECRYPT_MODE, keySpec, initVector);
            byte[] originalData = aesCipher.doFinal(toByteArray(encryptedText));
            return new String(originalData, StandardCharsets.UTF_8);
        } catch (Exception x) {
            throw new SysException("Error occurred on AES decryption.", x);
        }
    }

    public static String toHexString(byte[] data) {
        if (data == null || data.length <= 0) {
            return null;
        }

        // convert bytes to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] toByteArray(String hexStr) {
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            int j = Integer.parseInt(hexStr.substring(index, index + 2), 16);
            bytes[i] = (byte) j;
        }
        return bytes;
    }

    private SecurityUtil() {
    }
}
