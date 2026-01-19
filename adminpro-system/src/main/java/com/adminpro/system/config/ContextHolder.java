package com.adminpro.system.config;

import com.adminpro.framework.base.context.AppContext;
import com.adminpro.framework.base.context.IContextHolder;

import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.rbac.api.LoginHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * @author simon
 * @date 2021/2/3
 */
@Component
public class ContextHolder implements IContextHolder {

    @Override
    public void setAppContext(HttpServletRequest request, AppContext appContext) {
        // Stateless, do nothing
    }

    @Override
    public AppContext getAppContext() {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser != null) {
            AppContext ctx = new AppContext();
            ctx.setLoginName(loginUser.getLoginName());
            ctx.setRealName(loginUser.getRealName());
            ctx.setId(loginUser.getUser().getId());
            ctx.setUserDomain(loginUser.getUserDomain());
            return ctx;
        }
        return null;
    }

    @Override
    public void clearAppContext(HttpServletRequest request) {
        // Stateless, do nothing
    }
}
