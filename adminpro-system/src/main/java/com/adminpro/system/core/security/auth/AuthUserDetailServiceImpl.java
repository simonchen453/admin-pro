package com.adminpro.system.core.security.auth;

import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import com.adminpro.system.rbac.common.RbacConstants;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.adminpro.system.rbac.domains.entity.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.adminpro.framework.client.helper.ClientHelper;

/**
 * 用户详情服务实现类
 * <p>
 * 实现Spring Security的UserDetailsService接口，负责根据用户名加载用户详情。
 * 支持移动端和Web端两种不同的缓存策略。
 * <p>
 * 缓存策略：
 * <ul>
 * <li>移动端：使用AppCache（分布式缓存），支持多实例部署</li>
 * <li>Web端：使用HttpSession（本地缓存），性能更好</li>
 * </ul>
 * <p>
 * 用户名格式：
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
     * <li>检查缓存中是否存在用户详情（根据请求类型选择缓存）</li>
     * <li>如果缓存命中，直接返回</li>
     * <li>如果缓存未命中，从数据库查询用户</li>
     * <li>转换用户实体为LoginUser对象</li>
     * <li>将用户详情存入缓存</li>
     * <li>返回用户详情</li>
     * </ol>
     * <p>
     * 注意：如果用户不存在，返回null而不是抛出异常，
     * 这是为配合自定义的AuthenticationProvider使用
     *
     * @param username 用户名，格式为"用户域_登录名"
     * @return 用户详情对象，如果用户不存在则返回null
     * @throws UsernameNotFoundException 虽然方法签名声明了此异常，
     *                                      但当前实现不会抛出，而是返回null
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean isMobileRequest = ClientHelper.isMobileRequest(WebHelper.getHttpRequest());
        if (isMobileRequest) {
            LoginUser userDetails = AppCache.getInstance().get(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, username, LoginUser.class);
            if (userDetails != null) {
                return userDetails;
            }
        } else {
            HttpSession session = WebHelper.getHttpRequest().getSession();
            LoginUser userDetail = (LoginUser) session.getAttribute(RbacCacheConstants.AUTH_USER_DETAIL_CACHE + username);
            if (userDetail != null) {
                return userDetail;
            }
        }

        String[] split = username.split(RbacConstants.SPRING_SECURITY_USERIDEN_SPLIT);
        UserEntity user = userService.findByUserDomainAndLoginName(split[0], split[1]);
        if (user == null) {
            //throw new UsernameNotFoundException("找不到此用户");
            return null;
        } else {
            LoginUser authUser = LoginUser.convertFrom(user);
            if (isMobileRequest) {
                AppCache.getInstance().set(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, username, authUser);
            }else{
                HttpSession session = WebHelper.getHttpRequest().getSession();
                session.setAttribute(RbacCacheConstants.AUTH_USER_DETAIL_CACHE + username, authUser);
            }

            return authUser;
        }
    }
}
