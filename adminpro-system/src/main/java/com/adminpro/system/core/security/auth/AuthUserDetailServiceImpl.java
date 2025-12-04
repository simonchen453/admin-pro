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

@Service
public class AuthUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;



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
        UserEntity user = userService.findByUserDomainAndUserId(split[0], split[1]);
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
