package com.adminpro.rbac.encrypt;

import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.rbac.domains.entity.user.UserIden;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

/**
 * @author simon
 * @date 2017/6/10
 */
@Component
public class PasswordSHA256Encryptor implements PasswordEncryptor {
    /**
     * {userDomain}_{userId}_{pwd}
     */
    protected static final String PWD_FORMAT = "{0}_{1}_{2}";

    protected static final Logger logger = LoggerFactory.getLogger(PasswordSHA256Encryptor.class);

    @Override
    public String encrypt(UserIden userIden, String pwd) {
        String format = MessageFormat.format(PWD_FORMAT, userIden.getUserDomain(), userIden.getUserId(), pwd);
        return sha256(format);
    }

    @Override
    public boolean checkPwd(UserEntity userEntity, String pwd) {
        String encryptPwd = encrypt(userEntity.getUserIden(), pwd);
        return StringUtils.equals(encryptPwd, userEntity.getPassword());
    }

    /**
     * sha256
     *
     * @param strText
     * @return
     */
    private static String sha256(final String strText) {
        // 返回值
        String strResult = null;
        if (StringUtils.isNotEmpty(strText)) {
            try {
                // SHA 加密开始
                // 创建加密对象 并傳入加密类型
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                // 传入要加密的字符串
                messageDigest.update(strText.getBytes());
                byte[] byteBuffer = messageDigest.digest();
                strResult = Hex.encodeHexString(byteBuffer);
            } catch (NoSuchAlgorithmException e) {
                logger.error(e.getMessage());
            }
        }
        return strResult;
    }

    public static void main(String[] args) {
        String pwd = "csc123ldrk";
        UserIden userIden = new UserIden("system", "admin");
        String encrypt = new PasswordSHA256Encryptor().encrypt(userIden, pwd);
        System.out.println(encrypt);
    }
}
