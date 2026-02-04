package com.adminpro.system.core.common.helper;

import com.adminpro.framework.base.util.CommonUtil;
import com.adminpro.framework.base.util.ParamUtil;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.system.core.common.helper.text.Convert;
import com.adminpro.system.rbac.common.RbacConstants;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;
import java.util.Locale;

/**
 * Web请求辅助工具类
 * <p>
 * 本类提供HTTP请求处理相关的工具方法，包括：
 * <ul>
 * <li>请求和响应对象的获取</li>
 * <li>参数获取和转换</li>
 * <li>IP地址获取</li>
 * <li>浏览器和操作系统信息识别</li>
 * <li>国际化（i18n）支持</li>
 * <li>页面跳转和渲染</li>
 * </ul>
 * <p>
 * 主要功能：
 * <ul>
 * <li>获取当前HTTP请求/响应对象</li>
 * <li>获取请求参数并类型转换</li>
 * <li>获取客户端真实IP地址</li>
 * <li>识别浏览器类型和版本</li>
 * <li>识别操作系统</li>
 * <li>设置和获取国际化信息</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 * <li>Controller中获取请求信息</li>
 * <li>日志记录中获取客户端信息</li>
 * <li>权限验证中获取IP</li>
 * <li>国际化切换</li>
 * </ul>
 * <p>
 * 注意：本类为Spring组件，通过依赖注入使用
 */
@Component
public class WebHelper {
    /**
     * 正则表达式：占位符格式 {数字}
     */
    public static final String REGEX = "\\{(\\d)\\}";
    private static Logger logger = LoggerFactory.getLogger(WebHelper.class);
    private static final String ATTR_REST_MARKER = "$$REST";
    private static final String ATTR_LOCALE = "$$LOCALE";
    private static final String ATTR_ENCODING = "$$ENCODING";

    private static final String CONF_LOCALE_DEFAULT = "app.locale.default";
    private static final String CONF_LOCALE_ACCEPT = "app.locale.accept";
    private static final String CONF_LOCALE_ENCODING_PREFIX = "app.locale.encoding.";

    public static final String UNKNOWN_BROWSER = "Unknown browser";
    public static final String UNKNOWN_OS = "Unknown OS";
    // browser[search name, display name, search version]
    public static final String[] BROWSER_IE = { "MSIE", "Internet Explorer", "MSIE" };
    public static final String[] BROWSER_FIREFOX = { "Firefox", "Firefox", "Firefox" };
    public static final String[] BROWSER_OPERA = { "Opera", "Opera", "Opera" };
    public static final String[] BROWSER_CHROME = { "Chrome", "Chrome", "Chrome" };
    public static final String[] BROWSER_SAFARI = { "Safari", "Safari", "Version" };
    public static final String[] BROWSER_EDG = { "Edge", "Edge", "Edge" };
    public static final String[][] BROWSERS = { BROWSER_IE, BROWSER_FIREFOX, BROWSER_OPERA, BROWSER_CHROME,
            BROWSER_SAFARI };
    // OS[search name, display name]
    public static final String[] OS_WINDOWS = { "Windows", "Windows" };
    public static final String[] OS_MAC = { "Mac", "Mac OS" };
    public static final String[] OS_LINUX = { "Linux", "Linux" };
    public static final String[] OS_ANDROID = { "Android", "Android" };
    public static final String[] OS_IOS = { "iPhone", "iOS" };
    public static final String[][] OSES = { OS_WINDOWS, OS_MAC, OS_LINUX, OS_ANDROID, OS_IOS };
    public static final String LOGIN_CONTINUE_URL = "continueUrl";

    public static String getEncoding() {
        HttpServletRequest httpRequest = getHttpRequest();
        String encoding = getAttribute(httpRequest, ATTR_ENCODING, String.class);
        if (StringUtils.isNotEmpty(encoding)) {
            return encoding;
        }

        String confKey = CONF_LOCALE_ENCODING_PREFIX + getLocale().toString();
        encoding = ConfigHelper.getString(confKey);
        if (StringUtils.isEmpty(encoding)) {
            encoding = "UTF-8";
        }

        httpRequest.setAttribute(ATTR_ENCODING, encoding);

        return encoding;
    }

    /**
     * set locale
     *
     * @param locale
     */
    public static void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        LocaleResolver r = SpringUtil.getBean(LocaleResolver.class);
        r.setLocale(request, response, locale);
        request.removeAttribute(ATTR_LOCALE);
    }

    /**
     * get locale
     *
     * @return
     */
    public static Locale getLocale() {
        HttpServletRequest httpRequest = getHttpRequest();
        Locale savedLocale = getAttribute(httpRequest, ATTR_LOCALE, Locale.class);
        if (null != savedLocale) {
            return savedLocale;
        }

        LocaleResolver r = SpringUtil.getBean(LocaleResolver.class);
        Locale locale = r.resolveLocale(httpRequest);
        String[] accept = ConfigHelper.getStringArray(CONF_LOCALE_ACCEPT);
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

        String defLoc = ConfigHelper.getString(CONF_LOCALE_DEFAULT);
        locale = LocaleUtils.toLocale(defLoc);
        httpRequest.setAttribute(ATTR_LOCALE, locale);
        return locale;
    }

    /**
     * servlet context
     *
     * @return
     */
    public static ServletContext getServletContext() {
        return CommonUtil.getInstance().getServletContext();
    }

    /**
     * get context path
     *
     * @return
     */
    public static String getContextPath() {
        return CommonUtil.getInstance().getContextPath();
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
        int index = result.indexOf(";");
        if (index < 0) {
            return result;
        }

        return result.substring(0, index);
    }

    /**
     * get http request object
     *
     * @return
     */
    public static HttpServletRequest getHttpRequest() {
        return CommonUtil.getCurrentRequest();
    }

    public static HttpServletResponse getHttpResponse() {
        ServletRequestAttributes reqAttrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return reqAttrs.getResponse();
    }

    /**
     * convert servlet request object to http request
     *
     * @param request
     * @return
     */
    public static HttpServletRequest getHttpRequest(ServletRequest request) {
        return (HttpServletRequest) request;
    }

    /**
     * convert servlet response to http response
     *
     * @param response
     * @return
     */
    public static HttpServletResponse getHttpResponse(ServletResponse response) {
        return (HttpServletResponse) response;
    }

    /**
     * get attribute from request
     *
     * @param request
     * @param key
     * @return
     */
    public static Object getAttribute(HttpServletRequest request, String key) {
        return getAttribute(request, key, Object.class);
    }

    /**
     * get attribute from request
     *
     * @param request
     * @param key
     * @param clazz
     * @param <E>
     * @return
     */
    public static <E> E getAttribute(HttpServletRequest request, String key, Class<E> clazz) {
        return ParamUtil.getAttribute(request, key, clazz);
    }

    /**
     * get attribute from servlet context
     *
     * @param context
     * @param key
     * @return
     */
    public static Object getAttribute(ServletContext context, String key) {
        return getAttribute(context, key, Object.class);
    }

    /**
     * get attribute from servlet context
     *
     * @param context
     * @param key
     * @param clazz
     * @param <E>
     * @return
     */
    public static <E> E getAttribute(ServletContext context, String key, Class<E> clazz) {
        return ParamUtil.getAttribute(context, key, clazz);
    }

    public static String getRequestValue(HttpServletRequest request, String paramName) {
        Object value = request.getAttribute(paramName);
        if (value != null && value instanceof String) {
            return (String) value;
        } else {
            return request.getParameter(paramName);
        }
    }

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;

        if (request != null) {
            try {
                ip = request.getHeader("x-forwarded-for");
                if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_CLIENT_IP");
                }
                if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                }
                if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
            } catch (Exception e) {
                logger.error("IPUtils ERROR ", e);
            }

            // 使用代理，则获取第一个IP地址
            if (StringUtils.isEmpty(ip) && ip.length() > 15) {
                if (ip.indexOf(",") > 0) {
                    ip = ip.substring(0, ip.indexOf(","));
                }
            }
        }
        return ip;
    }

    public static boolean isDevModel() {
        return StringUtils.equalsIgnoreCase(RbacConstants.getDeploymentMode(), "dev");
    }

    public static final String getUserAgent(HttpServletRequest request) {
        Assert.notNull(request, "HTTP Servlet Request cannot be null.");

        return request.getHeader("User-Agent");
    }

    public static final String getBrowserInfo(HttpServletRequest request) {
        String agent = getUserAgent(request);

        if (!StringHelper.isEmpty(agent)) {
            agent = agent.replaceAll(";", " ").replaceAll("/", " ") + " ";
            if (agent.contains(BROWSER_EDG[0])) {
                int start = agent.indexOf(BROWSER_EDG[2]);

                if (start != -1) {
                    start += BROWSER_EDG[2].length();
                    if (start < agent.length()) {
                        agent = agent.substring(start);
                        agent = StringUtils.trimToNull(agent);

                        int end = agent.indexOf(" ");

                        if (end != -1) {
                            agent = agent.substring(0, end);
                        }

                        return BROWSER_EDG[1] + " " + agent;
                    }
                }
            } else {
                for (int i = 0; i < BROWSERS.length; i++) {
                    if (!agent.contains(BROWSERS[i][0])) {
                        continue;
                    }

                    int start = agent.indexOf(BROWSERS[i][2]);

                    if (start == -1) {
                        continue;
                    } else {
                        start += BROWSERS[i][2].length();
                    }

                    if (start >= agent.length()) {
                        continue;
                    }

                    agent = agent.substring(start);
                    agent = StringUtils.trimToNull(agent);

                    int end = agent.indexOf(" ");

                    if (end != -1) {
                        agent = agent.substring(0, end);
                    }

                    return BROWSERS[i][1] + " " + agent;
                }
            }
            if (agent.indexOf("rv") != -1 && agent.indexOf("firefox") == -1) {
                agent = agent.substring(agent.indexOf("rv") + 3);
                agent = agent.substring(0, agent.indexOf(')'));
                return BROWSERS[0][1] + " " + agent;
            }
        }

        return UNKNOWN_BROWSER;
    }

    /**
     * 获取操作系统信息
     */
    public static final String getOsInfo(HttpServletRequest request) {
        String agent = getUserAgent(request);

        if (!StringHelper.isEmpty(agent)) {
            agent = agent.replaceAll(";", " ").replaceAll("/", " ") + " ";
            for (int i = 0; i < OSES.length; i++) {
                if (!agent.contains(OSES[i][0])) {
                    continue;
                }
                return OSES[i][1];
            }
        }

        return UNKNOWN_OS;
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        return getHttpRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue) {
        return Convert.toStr(getHttpRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name) {
        return Convert.toInt(getHttpRequest().getParameter(name));
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(getHttpRequest().getParameter(name), defaultValue);
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            logger.error("渲染字符串到客户端失败", e);
        }
        return null;
    }

    public static void redirect(String url) throws IOException {
        HttpServletResponse httpResponse = getHttpResponse();
        httpResponse.sendRedirect(url);
    }

    public static void forward(String url) throws IOException, ServletException {
        HttpServletResponse response = getHttpResponse();
        HttpServletRequest request = getHttpRequest();
        request.getRequestDispatcher(url).forward(request, response);
    }

    public static String getLoginContinueUrl(HttpServletRequest request) {
        return getRequestValue(request, LOGIN_CONTINUE_URL);
    }

    public static void setLoginContinueUrl(HttpServletRequest request, String loginContinueUrl) {
        request.setAttribute(LOGIN_CONTINUE_URL, loginContinueUrl);
    }
}
