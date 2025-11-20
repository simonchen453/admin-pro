package com.adminpro.core.base.util;

import com.adminpro.core.base.context.AppContext;
import com.adminpro.core.base.context.IContextHolder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @author simon
 * @date 2021/2/3
 */
@Component
public class CommonUtil implements ServletContextAware {
    private ServletContext context;

    public static CommonUtil getInstance() {
        return SpringUtil.getBean(CommonUtil.class);
    }

    /**
     * save the context to request session
     *
     * @param request
     * @param appContext
     */
    public static void setAppContext(HttpServletRequest request, AppContext appContext) {
        SpringUtil.getBean(IContextHolder.class).setAppContext(request, appContext);
    }

    public static void clearAppContext(HttpServletRequest request) {
        SpringUtil.getBean(IContextHolder.class).clearAppContext(request);
    }

    /**
     * get the context from current thread, if failed then session.
     *
     * @return
     */
    public static AppContext getAppContext() {
        return SpringUtil.getBean(IContextHolder.class).getAppContext();
    }

    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        } else {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        context = servletContext;
    }

    public ServletContext getServletContext() {
        return context;
    }

    /**
     * get context path
     *
     * @return
     */
    public String getContextPath() {
        return context.getContextPath();
    }

    /**
     * 获取不带context path的uri
     *
     * @param request
     * @return
     */
    public static String getRequestUriWithoutContextPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String result = requestUri.substring(contextPath.length());
        // remove sessionid appended after ";", e.g.
        // "/login;jsessionid=A4F14C7D0B3E11C8DFBED5FAFE120A18"
        int index = result.indexOf(";");
        if (index < 0) {
            return result;
        }

        return result.substring(0, index);
    }

    /**
     * 判断当前请求是不是Rest API
     *
     * @return
     */
    public static boolean isRestRequest() {
        HttpServletRequest httpRequest = getCurrentRequest();
        if (httpRequest != null) {
            return isRestUrl(getRequestUriWithoutContextPath(httpRequest));
        } else {
            return false;
        }
    }

    /**
     * 判断当前URL是不是Rest URL
     *
     * @return
     */
    public static boolean isRestUrl(String url) {
        AntPathMatcher matcher = new AntPathMatcher("/");
        boolean match = matcher.match("/api/**", url);
        return match;
    }

    /**
     * 判断当前URL是不是Rest URL
     *
     * @return
     */
    public static boolean isAjaxUrl(String url) {
        AntPathMatcher matcher = new AntPathMatcher("/");
        boolean match = matcher.match("/rest/**", url);
        return match;
    }

    /**
     * 是否是Ajax异步请求
     *
     * @param request
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf("application/json") != -1) {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1) {
            return true;
        }

        String uri = request.getRequestURI();
        if (StringUtil.inStringIgnoreCase(uri, ".json", ".xml")) {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        if (StringUtil.inStringIgnoreCase(ajax, "json", "xml")) {
            return true;
        }
        return false;
    }

    private static final String ATTR_LOCALE = "$$LOCALE";
    private static final String CONF_LOCALE_DEFAULT = "app.locale.default";
    private static final String CONF_LOCALE_ACCEPT = "app.locale.accept";

    /**
     * get locale
     *
     * @return
     */
    public static Locale getLocale() {
        HttpServletRequest httpRequest = getCurrentRequest();

        Locale locale = null;

        if (httpRequest != null) {
            Locale savedLocale = ParamUtil.getAttribute(httpRequest, ATTR_LOCALE, Locale.class);
            if (null != savedLocale) {
                return savedLocale;
            }

            LocaleResolver r = SpringUtil.getBean(LocaleResolver.class);
            locale = r.resolveLocale(httpRequest);
            String[] accept = ConfigUtil.getStringArray(CONF_LOCALE_ACCEPT);
            if (ArrayUtils.isEmpty(accept)) {
                httpRequest.setAttribute(ATTR_LOCALE, locale);
                return locale;
            }

            for (String strItem : accept) {
                if (StringUtils.equals(strItem, locale.toString())) {
                    httpRequest.setAttribute(ATTR_LOCALE, locale);
                    return locale;
                }
            }
            httpRequest.setAttribute(ATTR_LOCALE, locale);
        } else {
            String defLoc = ConfigUtil.getString(CONF_LOCALE_DEFAULT);
            Locale defaultLocal = LocaleUtils.toLocale(defLoc);
            locale = defaultLocal;
        }

        return locale;
    }
}
