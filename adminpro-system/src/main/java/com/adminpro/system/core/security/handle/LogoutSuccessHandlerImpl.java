package com.adminpro.system.core.security.handle;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.util.JsonUtil;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.rbac.api.LoginHelper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * 自定义退出处理类 返回JSON响应（前后端分离）
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
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            // 执行登出
            LoginHelper.getInstance().logout();
            // 返回JSON响应
            WebHelper.renderString(response, JsonUtil.toJson(R.ok("登出成功")));
        } catch (Exception e) {
            WebHelper.renderString(response, JsonUtil.toJson(R.error("登出失败: " + e.getMessage())));
        }
    }
}
