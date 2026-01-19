package com.adminpro.framework.base.context;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author simon
 * @date 2021/2/3
 */
@ConditionalOnMissingBean({ IContextHolder.class })
@Component
public class DefaultContextHolder implements IContextHolder {

    @Override
    public void setAppContext(HttpServletRequest request, AppContext appContext) {
        // Stateless default implementation, do nothing
    }

    @Override
    public AppContext getAppContext() {
        return null;
    }

    @Override
    public void clearAppContext(HttpServletRequest request) {
        // Stateless default implementation, do nothing
    }
}
