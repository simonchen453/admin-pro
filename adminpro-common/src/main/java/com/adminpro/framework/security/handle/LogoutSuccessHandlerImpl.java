package com.adminpro.framework.security.handle;

import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.rbac.api.LoginHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义退出处理类 返回成功
 *
 * @author simon
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String continueUrl = (String) request.getAttribute("continueUrl");
        if (StringUtils.isEmpty(continueUrl)) {
            continueUrl = "/";
        }
        LoginHelper.getInstance().logout();
        WebHelper.redirect(request.getContextPath() + continueUrl);
    }
}
