package com.adminpro.system.rbac.encrypt;

import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt 密码加密器
 *
 * @author simon
 */
@Component
public class PasswordBCryptEncryptor implements PasswordEncryptor {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encrypt(String userDomain, String loginName, String pwd) {
        // BCrypt 不需要用户信息，直接加密密码
        return encoder.encode(pwd);
    }

    @Override
    public boolean checkPwd(UserEntity userEntity, String pwd) {
        if (userEntity == null || pwd == null) {
            return false;
        }
        return encoder.matches(pwd, userEntity.getPassword());
    }
}
