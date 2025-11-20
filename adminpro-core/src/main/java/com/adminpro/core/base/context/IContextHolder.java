package com.adminpro.core.base.context;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author simon
 * @date 2021/2/3
 */
public interface IContextHolder {
    String APP_CONTEXT_KEY = "app.context.holder.key";

    void setAppContext(HttpServletRequest request, AppContext appContext);

    AppContext getAppContext();

    void clearAppContext(HttpServletRequest request);
}
