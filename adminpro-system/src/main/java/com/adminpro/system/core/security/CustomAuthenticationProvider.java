package com.adminpro.system.core.security;

import com.adminpro.system.core.security.auth.AuthToken;
import com.adminpro.system.core.security.auth.LoginUser;

import com.adminpro.system.rbac.domains.entity.user.UserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 自定义身份认证验证组件
 * <p>
 * 实现Spring Security的AuthenticationProvider接口，提供自定义的用户认证逻辑。
 * 负责验证用户名和密码，并在认证成功后生成Token。
 * <p>
 * 认证流程：
 * <ol>
 * <li>从Authentication对象中提取用户名和密码</li>
 * <li>通过UserDetailsService加载用户详情</li>
 * <li>调用UserService的authLogin方法验证密码</li>
 * <li>验证成功后生成Token并创建认证对象</li>
 * <li>验证失败则抛出相应的认证异常</li>
 * </ol>
 * <p>
 * 安全特性：
 * <ul>
 * <li>密码验证：使用UserService中的加密密码比对逻辑</li>
 * <li>Token生成：认证成功后自动生成Token</li>
 * <li>异常处理：区分用户不存在和密码错误两种情况</li>
 * <li>用户名格式：支持"用户域_登录名"格式</li>
 * </ul>
 * <p>
 * 注意：此组件会被Spring Security自动调用，不需要手动调用
 *
 * @author simon
 * @see org.springframework.security.authentication.AuthenticationProvider
 * @see org.springframework.security.core.userdetails.UserDetailsService
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Resource(name = "authUserDetailServiceImpl")
    private UserDetailsService userDetailsService;

    /**
     * 执行身份认证
     * <p>
     * 验证用户的用户名和密码，并在验证成功后返回认证对象。
     * <p>
     * 处理流程：
     * <ol>
     * <li>从Authentication中提取用户名（格式：用户域_登录名）和密码</li>
     * <li>加载用户详情信息</li>
     * <li>调用UserService验证密码</li>
     * <li>如果验证成功，生成Token并创建认证对象</li>
     * <li>如果验证失败，抛出相应的异常</li>
     * </ol>
     * <p>
     * 异常说明：
     * <ul>
     * <li>UsernameNotFoundException：用户不存在</li>
     * <li>BadCredentialsException：密码不正确</li>
     * </ul>
     *
     * @param authentication 认证对象，包含用户名和密码
     * @return 认证成功后的Authentication对象，principal为AuthToken
     * @throws AuthenticationException 认证失败时抛出，包括：
     *                                  UsernameNotFoundException（用户不存在）
     *                                  BadCredentialsException（密码错误）
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取认证的用户名 & 密码
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 认证逻辑
        LoginUser authUser = (LoginUser) userDetailsService.loadUserByUsername(name);
        if (null != authUser) {
            String userDomain = authUser.getUserDomain();
            String loginName = authUser.getUsername();

            String token = UserService.getInstance().authLogin(userDomain, loginName, password);
            if (StringUtils.isNotEmpty(token)) {
                // 生成令牌
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        new AuthToken(userDomain, loginName, token), password,
                        authUser.getAuthorities());
                return auth;
            } else {
                throw new BadCredentialsException("密码不正确");
            }
        } else {
            throw new UsernameNotFoundException("用户不存在~");
        }
        // return null;
    }

    /**
     * 判断是否支持处理指定的认证类型
     * <p>
     * 此方法告诉Spring Security，这个Provider可以处理哪种类型的认证请求。
     * 当前实现只支持UsernamePasswordAuthenticationToken类型的认证。
     *
     * @param authentication 认证类型
     * @return 如果支持该认证类型返回true，否则返回false
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
