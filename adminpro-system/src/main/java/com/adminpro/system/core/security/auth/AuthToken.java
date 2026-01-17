package com.adminpro.system.core.security.auth;

import java.io.Serializable;

/**
 * 认证Token信息
 * <p>
 * 用于在Spring Security认证过程中传递用户身份和Token信息。
 * 作为Authentication的principal使用，封装了用户的基本认证信息。
 * <p>
 * 包含的信息：
 * <ul>
 * <li>userDomain：用户域，用于多租户场景</li>
 * <li>userId：用户ID（全局唯一主键）</li>
 * <li>token：认证Token字符串</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 * <li>用户登录成功后，创建AuthToken作为认证凭据</li>
 * <li>在SecurityContext中存储此对象作为已认证用户标识</li>
 * <li>支持序列化，可在分布式环境中传递</li>
 * </ul>
 *
 * @see org.springframework.security.core.Authentication
 */
public class AuthToken implements Serializable {
    /**
     * 用户域
     */
    private String userDomain;

    /**
     * 用户ID（全局唯一主键）
     */
    private String userId;

    /**
     * 认证Token字符串
     */
    private String token;

    /**
     * 构造函数（不含Token）
     *
     * @param userDomain 用户域
     * @param userId     用户ID（全局唯一主键）
     */
    public AuthToken(String userDomain, String userId) {
        this.userDomain = userDomain;
        this.userId = userId;
    }

    /**
     * 构造函数（包含Token）
     *
     * @param userDomain 用户域
     * @param userId     用户ID（全局唯一主键）
     * @param token      认证Token字符串
     */
    public AuthToken(String userDomain, String userId, String token) {
        this.userDomain = userDomain;
        this.userId = userId;
        this.token = token;
    }

    /**
     * 获取用户域
     *
     * @return 用户域
     */
    public String getUserDomain() {
        return userDomain;
    }

    /**
     * 设置用户域
     *
     * @param userDomain 用户域
     */
    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    /**
     * 获取用户ID（全局唯一主键）
     *
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户ID（全局唯一主键）
     *
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取认证Token字符串
     *
     * @return Token字符串
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置认证Token字符串
     *
     * @param token Token字符串
     */
    public void setToken(String token) {
        this.token = token;
    }
}
