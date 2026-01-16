package com.adminpro.system.rbac.encrypt;

import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.adminpro.system.rbac.domains.entity.user.UserIden;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt encryptor
 *
 * @author simon
 */
@Component
public class PasswordBCryptEncryptor implements PasswordEncryptor {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encrypt(UserIden userIden, String pwd) {
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
