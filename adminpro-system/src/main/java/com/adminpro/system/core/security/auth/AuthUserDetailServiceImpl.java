package com.adminpro.system.core.security.auth;

import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import com.adminpro.system.rbac.common.RbacConstants;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.adminpro.system.rbac.domains.entity.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现类
 * <p>
 * 实现Spring Security的UserDetailsService接口，负责根据用户名加载用户详情。
 * 使用 AppCache（分布式缓存）进行缓存，支持无状态服务器架构。
 * <p>
 * 缓存策略：
 * <ul>
 * <li>统一使用AppCache（EhCache/Redis），支持多实例部署</li>
 * <li>不使用HttpSession，保持服务器无状态</li>
 * </ul>
 * <p>
 * 用户名格式：
 * 
 * <pre>
 * 用户域_登录名（例如：system_admin）
 * </pre>
 * <p>
 * 性能优化：
 * <ul>
 * <li>首次加载后缓存用户详情</li>
 * <li>后续请求直接从缓存获取</li>
 * <li>减少数据库查询次数</li>
 * </ul>
 *
 * @author simon
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see LoginUser
 */
@Service
public class AuthUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    /**
     * 根据用户名加载用户详情
     * <p>
     * 处理流程：
     * <ol>
     * <li>检查 AppCache 中是否存在用户详情</li>
     * <li>如果缓存命中，直接返回</li>
     * <li>如果缓存未命中，从数据库查询用户</li>
     * <li>转换用户实体为LoginUser对象</li>
     * <li>将用户详情存入缓存</li>
     * <li>返回用户详情</li>
     * </ol>
     * <p>
     * 注意：如果用户不存在，将抛出UsernameNotFoundException，
     * 这符合Spring Security的UserDetailsService接口契约
     *
     * @param username 用户名，格式为"用户域_登录名"
     * @return 用户详情对象
     * @throws UsernameNotFoundException 当用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从 AppCache 获取缓存的用户详情（无状态，不使用 Session）
        LoginUser userDetails = AppCache.getInstance().get(
                RbacCacheConstants.AUTH_USER_DETAIL_CACHE, username, LoginUser.class);
        if (userDetails != null) {
            return userDetails;
        }

        // 缓存未命中，从数据库查询
        String[] split = username.split(RbacConstants.SPRING_SECURITY_USERIDEN_SPLIT);
        if (split.length < 2) {
            throw new UsernameNotFoundException("用户名格式错误: " + username);
        }

        UserEntity user = userService.findByUserDomainAndLoginName(split[0], split[1]);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        LoginUser authUser = LoginUser.convertFrom(user);

        // 存入 AppCache（分布式缓存）
        AppCache.getInstance().set(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, username, authUser);

        return authUser;
    }
}
