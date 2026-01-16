package com.adminpro.system.rbac.encrypt;

import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.system.core.common.helper.ConfigHelper;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 密码加密执行器
 * 
 * @author simon
 * @date 2017/6/10
 */
public class PasswordEncryptExecutor {
    public static final String PWD_ENCRYPT_TYPE = "app.pwd.encrypt.type";
    public static final String PWD_ENCRYPT_SHA256 = "SHA256";
    public static final String PWD_ENCRYPT_BCRYPT = "BCRYPT";
    public static final Map<String, Class<? extends PasswordEncryptor>> MAP = new HashMap<>();

    static {
        MAP.put(PWD_ENCRYPT_BCRYPT, PasswordBCryptEncryptor.class);
    }

    private static PasswordEncryptExecutor instance;

    public static final synchronized PasswordEncryptExecutor getInstance() {
        if (instance == null) {
            instance = new PasswordEncryptExecutor();
        }
        return instance;
    }

    private PasswordEncryptor getPwdEncryptor() {
        String string = ConfigHelper.getString(PWD_ENCRYPT_TYPE, PWD_ENCRYPT_BCRYPT);
        Class<? extends PasswordEncryptor> aClass = MAP.get(string);
        PasswordEncryptor encryptor = SpringUtil.getBean(aClass);
        return encryptor;
    }

    /**
     * 加密密码
     * 
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param pwd        原始密码
     * @return 加密后的密码
     */
    public String encryptPwd(String userDomain, String loginName, String pwd) {
        PasswordEncryptor encryptor = getPwdEncryptor();
        return encryptor.encrypt(userDomain, loginName, pwd);
    }

    public boolean checkPwd(UserEntity userEntity, String pwd) {
        PasswordEncryptor encryptor = getPwdEncryptor();
        return encryptor.checkPwd(userEntity, pwd);
    }
}
