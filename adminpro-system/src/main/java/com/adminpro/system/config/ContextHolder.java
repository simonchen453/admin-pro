package com.adminpro.system.config;

import com.adminpro.framework.base.context.AppContext;
import com.adminpro.framework.base.context.IContextHolder;
import com.adminpro.framework.base.util.CommonUtil;
import com.adminpro.framework.base.util.ParamUtil;
import com.adminpro.system.framework.security.auth.LoginUser;
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
        ParamUtil.setSessionAttr(request, APP_CONTEXT_KEY, appContext);
    }

    @Override
    public AppContext getAppContext() {
        AppContext ctx = (AppContext) ParamUtil.getSessionAttr(CommonUtil.getCurrentRequest(), APP_CONTEXT_KEY);
        if (ctx == null) {
            LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
            if (loginUser != null) {
                ctx = new AppContext();
                ctx.setLoginName(loginUser.getLoginName());
                ctx.setRealName(loginUser.getRealName());
                ctx.setUserId(loginUser.getUserIden().getUserId());
                ctx.setUserDomain(loginUser.getUserIden().getUserDomain());
            }
        }
        return ctx;
    }

    @Override
    public void clearAppContext(HttpServletRequest request) {
        ParamUtil.setSessionAttr(request, APP_CONTEXT_KEY, null);
    }
}
