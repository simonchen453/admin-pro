package com.adminpro.system.core.security.auth;

import com.adminpro.system.rbac.api.RbacHelper;
import com.adminpro.system.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.system.rbac.domains.entity.dept.DeptService;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.adminpro.system.rbac.domains.entity.user.UserIden;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author simon
 */
public class LoginUser implements UserDetails {
    private final String userDomain;
    private final String deptNo;
    private final String deptName;
    private final String realName;
    private final String password;
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
    private final UserIden userIden;
    private final UserEntity user;

    private final List<String> permissions;

    public LoginUser(UserIden userIden, String password, String status, String deptNo,
            String deptName, String realName, UserEntity user, List<String> permissions) {
        this.userIden = userIden;
        this.userDomain = userIden.getUserDomain();
        this.password = password;
        this.status = status;
        this.permissions = permissions;
        this.deptNo = deptNo;
        this.deptName = deptName;
        this.realName = realName;
        this.user = user;
    }

    @Override
    public String toString() {
        return this.userIden.getLoginName();
    }

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
                new UserIden(user.getUserDomain(), user.getLoginName()),
                user.getPassword(),
                user.getStatus(),
                deptNo,
                deptName,
                user.getRealName(),
                user,
                Arrays.asList(permissions));
    }

    public String toSecurityUserName() {
        return userIden.toSecurityUsername();
    }

    @Override
    public String getUsername() {
        return userIden.getLoginName();
    }

    public String getStatus() {
        return status;
    }

    public String getUserDomain() {
        return userDomain;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否未过期
     */
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    public UserIden getUserIden() {
        return userIden;
    }

    public String getLoginName() {
        return userIden.getLoginName();
    }

    public String getDeptNo() {
        return deptNo;
    }

    public String getDeptName() {
        return deptName;
    }

    public String getRealName() {
        return realName;
    }

    public UserEntity getUser() {
        return user;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
