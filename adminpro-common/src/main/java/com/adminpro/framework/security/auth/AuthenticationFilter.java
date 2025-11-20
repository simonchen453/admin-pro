package com.adminpro.framework.security.auth;

import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.framework.common.constants.WebConstants;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.rbac.api.LoginHelper;
import com.adminpro.rbac.domains.entity.user.UserIden;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author simon
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthUserDetailServiceImpl userDetailsService;

    @Autowired
    TokenHelper tokenHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (WebHelper.isRestRequest()) {
            String authHeader = request.getHeader("x-access-token");
            if (StringUtils.isEmpty(authHeader)) {
                authHeader = request.getParameter("x-access-token");
            }
            if (authHeader != null) {
                final String authToken = authHeader;
                /*boolean authed = false;
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();*/
                UserIden userIden = TokenHelper.getInstance().getUserIdenByToken(authToken);
                /*if (auth != null) {
                    Object principal = auth.getPrincipal();
                    if (principal != null && principal instanceof AuthUser) {
                        authed = true;
                    }
                }*/
                if (userIden != null /*&& !authed*/) {
                    LoginUser authUser = (LoginUser) this.userDetailsService.loadUserByUsername(userIden.toSecurityUsername());
                    if (authUser != null) {
                        if (tokenHelper.validateToken(authToken, authUser)) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        } else {
                            throw new BaseRuntimeException(WebConstants.INVALID_AUTH_TOKEN_EXCEPTION);
                        }
                    } else {
                        SecurityContextHolder.getContext().setAuthentication(null);
                    }
                } else {
                    SecurityContextHolder.getContext().setAuthentication(null);
                }
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        } else {
            LoginUser authUser = LoginHelper.getInstance().getLoginUser();
            if (authUser != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }

        chain.doFilter(request, response);
    }
}
