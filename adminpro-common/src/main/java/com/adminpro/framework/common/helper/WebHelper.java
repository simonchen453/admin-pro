package com.adminpro.framework.common.helper;

import com.adminpro.core.base.util.CommonUtil;
import com.adminpro.core.base.util.ParamUtil;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.framework.common.constants.WebConstants;
import com.adminpro.framework.common.helper.text.Convert;
import com.adminpro.rbac.common.RbacConstants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebHelper {
    public static final String REGEX = "\\{(\\d)\\}";
    private static Logger logger = LoggerFactory.getLogger(WebHelper.class);
    private static final String ATTR_REST_MARKER = "$$REST";
    private static final String ATTR_LOCALE = "$$LOCALE";
    private static final String ATTR_ENCODING = "$$ENCODING";


    private static final String CONF_LOCALE_DEFAULT = "app.locale.default";
    private static final String CONF_LOCALE_ACCEPT = "app.locale.accept";
    private static final String CONF_LOCALE_ENCODING_PREFIX = "app.locale.encoding.";

    public static final String UNKNOWN_BROWSER = "Unknown browser";
    // browser[search name, display name, search version]
    public static final String[] BROWSER_IE = {"MSIE", "Internet Explorer", "MSIE"};
    public static final String[] BROWSER_FIREFOX = {"Firefox", "Firefox", "Firefox"};
    public static final String[] BROWSER_OPERA = {"Opera", "Opera", "Opera"};
    public static final String[] BROWSER_CHROME = {"Chrome", "Chrome", "Chrome"};
    public static final String[] BROWSER_SAFARI = {"Safari", "Safari", "Version"};
    public static final String[] BROWSER_EDG = {"Edge", "Edge", "Edge"};
    public static final String[][] BROWSERS = {BROWSER_IE, BROWSER_FIREFOX, BROWSER_OPERA, BROWSER_CHROME, BROWSER_SAFARI};
    public static final String LOGIN_CONTINUE_URL = "continueUrl";

    public static void setCurrentMenuId(HttpServletResponse response, String menuId) {
        Cookie cookie = new Cookie(WebConstants.COOKIE_CUR_MENU, menuId);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * @param cur   between start and end
     * @param start
     * @param end
     * @param size  should be odd number
     * @return
     */
    public static int[] getPageNoRange(int cur, int start, int end, int size) {
        Validate.isTrue(cur >= start && cur <= end);
        Validate.isTrue(size % 2 == 1);
        Validate.isTrue(start <= end);

        int[] ori = new int[size];
        int item = cur - size / 2;

        for (int i = 0; i < size; i++) {
            ori[i] = item++;
        }

        if (ori[0] < start) {
            int offset = start - ori[0];
            for (int i = 0; i < size; i++) {
                ori[i] = ori[i] + offset;
            }
        }

        if (ori[size - 1] > end) {
            int offset = ori[size - 1] - end;
            for (int i = 0; i < size; i++) {
                ori[i] = ori[i] - offset;
            }
        }

        return Arrays.stream(ori).filter(value -> value >= start).toArray();
    }

    /**
     * 从request获取action参数，并转存为request的属性
     *
     * @param request
     * @return
     */
    public static String getActionParam(HttpServletRequest request) {
        String parameter = request.getParameter(WebConstants.PARAM_ACTION);
        request.setAttribute(WebConstants.PARAM_ACTION, parameter);
        return parameter;
    }

    /**
     * 从multi request获取action参数，并转存为request的属性
     *
     * @param request
     * @return
     */
    public static String getActionParam(MultipartHttpServletRequest request) {
        String parameter = request.getParameter(WebConstants.PARAM_ACTION);
        request.setAttribute(WebConstants.PARAM_ACTION, parameter);
        return parameter;
    }

    /**
     * 从request获取action value参数，并转存为request的属性
     *
     * @param request
     * @return
     */
    public static String getActionValueParam(HttpServletRequest request) {
        String parameter = request.getParameter(WebConstants.PARAM_ACTION_VALUE);
        request.setAttribute(WebConstants.PARAM_ACTION_VALUE, parameter);
        return parameter;
    }

    public static String appendParameter(String url, String key, String value) {
        String sep = "?";
        if (url.indexOf('?') > 0) {
            sep = "&";
        }

        try {
            return url + sep + URLEncoder.encode(key, WebConstants.ENCODING) + "=" + URLEncoder.encode(value, WebConstants.ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new BaseRuntimeException(e);
        }
    }

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
        HttpServletRequest httpRequest = getHttpRequest();
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
        if (StringHelper.inStringIgnoreCase(uri, ".json", ".xml")) {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        if (StringHelper.inStringIgnoreCase(ajax, "json", "xml")) {
            return true;
        }
        return false;
    }

    /**
     * get http request object
     *
     * @return
     */
    public static HttpServletRequest getHttpRequest() {
        return CommonUtil.getCurrentRequest();
    }

    public static String getSessionId() {
        HttpServletRequest request = CommonUtil.getCurrentRequest();
        if (request != null) {
            HttpSession session = request.getSession();
            if (session != null) {
                return session.getId();
            }
        }
        return null;
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
     * get attribute from session
     *
     * @param session
     * @param key
     * @return
     */
    public static Object getAttribute(HttpSession session, String key) {
        return getAttribute(session, key, Object.class);
    }

    /**
     * get attribute from session
     *
     * @param session
     * @param key
     * @param clazz
     * @param <E>
     * @return
     */
    public static <E> E getAttribute(HttpSession session, String key, Class<E> clazz) {
        return ParamUtil.getAttribute(session, key, clazz);
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

    public static String fillStringByArgs(String str, String[] arr) {
        Matcher m = Pattern.compile(REGEX).matcher(str);
        while (m.find()) {
            str = str.replace(m.group(), arr[Integer.parseInt(m.group(1))]);
        }
        return str;
    }

    public static String formatMoney(double d) {
        String sal = new DecimalFormat("#.00").format(d);
        return sal;
    }

    public static String formatMoney(String money) {
        if (StringUtils.isEmpty(money)) {
            return "0.00";
        }
        double d = Double.valueOf(money);
//		int d = Integer.parseInt(money);
        double a = d / 100.00;
        String sal = new DecimalFormat("#0.00").format(a);
        return sal;
    }

    public static int getMoneyFen(String moneyStr) {
        double d = Double.valueOf(moneyStr);
        return (int) (d * 100);
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

        if(request != null) {
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

//        //使用代理，则获取第一个IP地址
            if (StringUtils.isEmpty(ip) && ip.length() > 15) {
                if (ip.indexOf(",") > 0) {
                    ip = ip.substring(0, ip.indexOf(","));
                }
            }
        }
        return ip;
    }

    public static String getSessionIdFromHeaders(HttpHeaders headers) {
        String sessionId = null;
        if (headers != null) {
            List<String> cookies = headers.get("Set-Cookie");
            if (!CollectionUtils.isEmpty(cookies)) {
                for (String cookie : cookies) {
                    if (cookie.startsWith("JSESSIONID=")) {
                        sessionId = cookie;
                    }
                }
            }
        }
        return sessionId;
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
            e.printStackTrace();
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
