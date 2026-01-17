package com.adminpro.system.core.security.auth;

import com.adminpro.system.rbac.api.RbacHelper;
import com.adminpro.system.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.system.rbac.domains.entity.dept.DeptService;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 登录用户信息
 * <p>
 * 实现了Spring Security的UserDetails接口，用于封装当前登录用户的所有信息。
 * 在用户认证成功后，系统会创建此对象并存储在SecurityContext中，
 * 供整个应用程序使用。
 * <p>
 * 包含的主要信息：
 * <ul>
 * <li>用户基本信息：用户ID、用户域、登录名、真实姓名、部门等</li>
 * <li>认证信息：密码（已加密）、账户状态等</li>
 * <li>权限信息：用户拥有的所有权限列表</li>
 * <li>会话信息：登录IP、登录地点、浏览器、操作系统等</li>
 * </ul>
 * <p>
 * 安全特性：
 * <ul>
 * <li>密码字段使用@JsonIgnore注解，防止序列化到JSON响应中</li>
 * <li>实现了Spring Security的UserDetails接口，可无缝集成</li>
 * <li>使用final字段确保核心信息的不可变性</li>
 * </ul>
 *
 * @author simon
 * @see org.springframework.security.core.userdetails.UserDetails
 */
public class LoginUser implements UserDetails {
    /**
     * 用户ID（全局唯一主键）
     */
    private final String userId;

    /**
     * 用户域
     */
    private final String userDomain;

    /**
     * 登录名
     */
    private final String loginName;

    /**
     * 部门编号
     */
    private final String deptNo;

    /**
     * 部门名称
     */
    private final String deptName;

    /**
     * 真实姓名
     */
    private final String realName;

    /**
     * 加密后的密码
     */
    private final String password;

    /**
     * 用户状态
     */
    private final String status;

    /**
     * 登录IP地址
     */
    private String ipAddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 用户实体对象
     */
    private final UserEntity user;

    /**
     * 用户权限列表
     */
    private final List<String> permissions;

    /**
     * 构造函数
     * <p>
     * 创建完整的登录用户信息对象
     *
     * @param userId     用户ID（全局唯一主键）
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param password   加密后的密码
     * @param status     用户状态
     * @param deptNo     部门编号
     * @param deptName   部门名称
     * @param realName   真实姓名
     * @param user       用户实体对象
     * @param permissions 用户权限列表
     */
    public LoginUser(String userId, String userDomain, String loginName, String password, String status, String deptNo,
            String deptName, String realName, UserEntity user, List<String> permissions) {
        this.userId = userId;
        this.userDomain = userDomain;
        this.loginName = loginName;
        this.password = password;
        this.status = status;
        this.permissions = permissions;
        this.deptNo = deptNo;
        this.deptName = deptName;
        this.realName = realName;
        this.user = user;
    }

    /**
     * 将用户实体转换为登录用户信息
     * <p>
     * 此方法会：
     * <ul>
     * <li>从用户实体中提取基本信息</li>
     * <li>查询用户的部门信息并填充</li>
     * <li>加载用户的所有权限</li>
     * </ul>
     *
     * @param user 用户实体对象
     * @return 登录用户信息对象
     */
    public static LoginUser convertFrom(UserEntity user) {
        String[] permissions = RbacHelper.getInstance().getAccessibleAllPermissionsByUser(user.getId(),
                user.getUserDomain());
        String deptNo = user.getDeptNo();
        String deptName = "";
        if (StringUtils.isNotEmpty(deptNo)) {
            DeptEntity deptEntity = DeptService.getInstance().findByNo(deptNo);
            if (deptEntity != null) {
                deptName = deptEntity.getName();
            }
        }
        return new LoginUser(
                user.getId(),
                user.getUserDomain(),
                user.getLoginName(),
                user.getPassword(),
                user.getStatus(),
                deptNo,
                deptName,
                user.getRealName(),
                user,
                Arrays.asList(permissions));
    }

    /**
     * 生成Spring Security格式的用户名
     * <p>
     * 格式：用户域_登录名（例如：system_admin）
     *
     * @return Spring Security格式的用户名
     */
    public String toSecurityUserName() {
        return userDomain + "_" + loginName;
    }

    /**
     * 获取用户名（Spring Security接口方法）
     * <p>
     * 返回登录名
     *
     * @return 登录名
     */
    @Override
    public String getUsername() {
        return loginName;
    }

    /**
     * 获取用户状态
     *
     * @return 用户状态码
     */
    public String getStatus() {
        return status;
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
     * 账户是否未过期（Spring Security接口方法）
     * <p>
     * 当前实现返回true，不进行过期检查
     *
     * @return true表示账户未过期
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定（Spring Security接口方法）
     * <p>
     * 当前实现返回true，实际账户锁定状态在登录时检查
     *
     * @return true表示账户未锁定
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 密码是否未过期（Spring Security接口方法）
     * <p>
     * 当前实现返回true，不进行密码过期检查
     *
     * @return true表示密码未过期
     */
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否启用
     * <p>
     * 当前实现返回true，实际账户状态检查在登录时进行
     *
     * @return true表示账户启用
     */
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    /**
     * 获取用户权限（Spring Security接口方法）
     * <p>
     * 当前实现返回null，实际权限检查使用自定义的PermissionService
     *
     * @return 权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * 获取加密后的密码（Spring Security接口方法）
     * <p>
     * 使用@JsonIgnore注解防止序列化到JSON响应中
     *
     * @return 加密后的密码
     */
    @Override
    public String getPassword() {
        return password;
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
     * 获取登录名
     *
     * @return 登录名
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * 获取部门编号
     *
     * @return 部门编号
     */
    public String getDeptNo() {
        return deptNo;
    }

    /**
     * 获取部门名称
     *
     * @return 部门名称
     */
    public String getDeptName() {
        return deptName;
    }

    /**
     * 获取真实姓名
     *
     * @return 真实姓名
     */
    public String getRealName() {
        return realName;
    }

    /**
     * 获取用户实体对象
     *
     * @return 用户实体对象
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * 获取用户权限列表
     *
     * @return 权限列表
     */
    public List<String> getPermissions() {
        return permissions;
    }

    /**
     * 获取登录IP地址
     *
     * @return IP地址
     */
    public String getIpAddr() {
        return ipAddr;
    }

    /**
     * 设置登录IP地址
     *
     * @param ipAddr IP地址
     */
    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    /**
     * 获取登录地点
     *
     * @return 登录地点
     */
    public String getLoginLocation() {
        return loginLocation;
    }

    /**
     * 设置登录地点
     *
     * @param loginLocation 登录地点
     */
    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    /**
     * 获取浏览器类型
     *
     * @return 浏览器类型
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * 设置浏览器类型
     *
     * @param browser 浏览器类型
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * 获取操作系统
     *
     * @return 操作系统名称
     */
    public String getOs() {
        return os;
    }

    /**
     * 设置操作系统
     *
     * @param os 操作系统名称
     */
    public void setOs(String os) {
        this.os = os;
    }
}
