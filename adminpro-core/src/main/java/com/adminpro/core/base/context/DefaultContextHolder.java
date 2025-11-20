package com.adminpro.core.base.context;

import com.adminpro.core.base.util.CommonUtil;
import com.adminpro.core.base.util.ParamUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author simon
 * @date 2021/2/3
 */
@ConditionalOnMissingBean({IContextHolder.class})
@Component
public class DefaultContextHolder implements IContextHolder {

    @Override
    public void setAppContext(HttpServletRequest request, AppContext appContext) {
        ParamUtil.setSessionAttr(request, APP_CONTEXT_KEY, appContext);
    }

    @Override
    public AppContext getAppContext() {
        AppContext ctx = (AppContext) ParamUtil.getSessionAttr(CommonUtil.getCurrentRequest(), APP_CONTEXT_KEY);
        return ctx;
    }

    @Override
    public void clearAppContext(HttpServletRequest request) {
        ParamUtil.setSessionAttr(request, APP_CONTEXT_KEY, null);
    }
}
