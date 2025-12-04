package com.adminpro.rbac.encrypt;

import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.rbac.domains.entity.user.UserIden;

/**
 * @author simon
 * @date 2017/6/10
 */
public interface PasswordEncryptor {
    String encrypt(UserIden userIden, String pwd);

    boolean checkPwd(UserEntity userEntity, String pwd);
}
