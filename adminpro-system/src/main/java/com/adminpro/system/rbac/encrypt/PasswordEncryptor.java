package com.adminpro.system.rbac.encrypt;

import com.adminpro.system.rbac.domains.entity.user.UserEntity;

/**
 * 密码加密接口
 * 
 * @author simon
 * @date 2017/6/10
 */
public interface PasswordEncryptor {
    /**
     * 加密密码
     * 
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param pwd        原始密码
     * @return 加密后的密码
     */
    String encrypt(String userDomain, String loginName, String pwd);

    /**
     * 校验密码
     * 
     * @param userEntity 用户实体
     * @param pwd        原始密码
     * @return 是否匹配
     */
    boolean checkPwd(UserEntity userEntity, String pwd);
}
