package com.adminpro.framework.security;

import com.adminpro.framework.security.auth.AuthToken;
import com.adminpro.framework.security.auth.LoginUser;
import com.adminpro.rbac.domains.entity.user.UserIden;
import com.adminpro.rbac.domains.entity.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 自定义身份认证验证组件
 *
 * @author simon
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Resource(name = "authUserDetailServiceImpl")
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取认证的用户名 & 密码
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 认证逻辑
        LoginUser authUser = (LoginUser) userDetailsService.loadUserByUsername(name);
        if (null != authUser) {
            UserIden userIden = new UserIden(authUser.getUserDomain(), authUser.getUsername());

            String token = UserService.getInstance().authLogin(userIden, password);
            if (StringUtils.isNotEmpty(token)) {
                // 生成令牌
                Authentication auth = new UsernamePasswordAuthenticationToken(new AuthToken(userIden.getUserDomain(), userIden.getUserId(), token), password, authUser.getAuthorities());
                return auth;
            } else {
                throw new BadCredentialsException("密码不正确");
            }
        } else {
            throw new UsernameNotFoundException("用户不存在~");
        }
//        return  null;
    }

    // 是否可以提供输入类型的认证服务
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
