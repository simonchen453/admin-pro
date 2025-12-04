package com.adminpro.rbac.encrypt;

import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.rbac.domains.entity.user.UserIden;

import java.util.HashMap;
import java.util.Map;

/**
 * @author simon
 * @date 2017/6/10
 */
public class PasswordEncryptExecutor {
    public static final String PWD_ENCRYPT_TYPE = "app.pwd.encrypt.type";
    public static final String PWD_ENCRYPT_SHA256 = "SHA256";
    public static final Map<String, Class<? extends PasswordEncryptor>> MAP = new HashMap<>();

    static {
        MAP.put(PWD_ENCRYPT_SHA256, PasswordSHA256Encryptor.class);
    }

    private static PasswordEncryptExecutor instance;

    public static final synchronized PasswordEncryptExecutor getInstance() {
        if (instance == null) {
            instance = new PasswordEncryptExecutor();
        }

        return instance;
    }

    private PasswordEncryptor getPwdEncryptor() {
        String string = ConfigHelper.getString(PWD_ENCRYPT_TYPE, PWD_ENCRYPT_SHA256);
        Class<? extends PasswordEncryptor> aClass = MAP.get(string);
        PasswordEncryptor encryptor = SpringUtil.getBean(aClass);
        return encryptor;
    }

    public String encryptPwd(UserIden userIden, String pwd) {
        PasswordEncryptor encryptor = getPwdEncryptor();
        return encryptor.encrypt(userIden, pwd);
    }

    public boolean checkPwd(UserEntity userEntity, String pwd) {
        PasswordEncryptor encryptor = getPwdEncryptor();
        return encryptor.checkPwd(userEntity, pwd);
    }
}
