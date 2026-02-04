package com.adminpro.framework.base.util;

import com.adminpro.framework.exceptions.BaseRuntimeException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

public final class ParamUtil {
    private static final Logger logger = LoggerFactory.getLogger(ParamUtil.class);

    /**
     * get data from request body
     *
     * @param request
     * @return
     */
    public static String getString(HttpServletRequest request) {
        try {
            return IOUtils.toString(request.getReader());
        } catch (IOException x) {
            throw new BaseRuntimeException("Error occurred on getting data from request body.", x);
        }
    }

    public static String getString(HttpServletRequest request, String param) {
        String str = request.getParameter(param);
        if (StringUtils.isBlank(str)) {
            return null;
        }

        return str.trim();
    }

    public static String getString(HttpServletRequest request, String param, String defaultVal) {
        String val = request.getParameter(param);
        return val == null ? defaultVal : val;
    }

    public static String[] getStrings(HttpServletRequest request, String param) {
        return request.getParameterValues(param);
    }

    public static int getInt(HttpServletRequest request, String param) {
        String val = getString(request, param);
        int ret = 0;
        if (StringUtils.isNotBlank(val)) {
            try {
                ret = Integer.parseInt(val);
            } catch (Exception ex) {
                logger.warn("The input [{}] is not a integer.", val);
            }
        }
        return ret;
    }

    public static int[] getInts(HttpServletRequest request, String param) {
        String[] vals = getStrings(request, param);
        int[] rets = null;
        if (vals != null) {
            rets = new int[vals.length];
            for (int i = 0; i < vals.length; i++) {
                if (StringUtils.isNotBlank(vals[i])) {
                    try {
                        rets[i] = Integer.parseInt(vals[i]);
                    } catch (Exception ex) {
                        logger.warn("The input [{}] is not a integer.", vals[i]);
                        rets[i] = -1;
                    }
                }
            }
        }
        return rets;
    }

    public static int getInt(HttpServletRequest request, String param, int defaultVal) {
        String val = getString(request, param);
        int ret = defaultVal;
        if (StringUtils.isNotBlank(val)) {
            try {
                ret = Integer.parseInt(val);
            } catch (Exception ex) {
                logger.warn("The input [{}] is not a integer.", val);
            }
        }
        return ret;
    }

    public static long getLong(HttpServletRequest request, String param) {
        return getLong(request, param, 0L);
    }

    public static long getLong(HttpServletRequest request, String param, long defaultVal) {
        String val = getString(request, param);
        long ret = defaultVal;
        try {
            ret = Long.parseLong(val);
        } catch (Exception ex) {
            logger.warn("The input [{}] is not a long.", val);
        }
        return ret;
    }

    public static long[] getLongs(HttpServletRequest request, String param) {
        String[] vals = getStrings(request, param);
        long[] rets = null;
        if (vals != null) {
            rets = new long[vals.length];
            for (int i = 0; i < vals.length; i++) {
                if (StringUtils.isNotBlank(vals[i])) {
                    try {
                        rets[i] = Long.parseLong(vals[i]);
                    } catch (Exception ex) {
                        logger.warn("The input [{}] is not a long.", vals[i]);
                        rets[i] = 0L;
                    }
                }
            }
        }
        return rets;
    }

    public static double getDouble(HttpServletRequest request, String param) {
        return getDouble(request, param, 0.0);
    }

    public static double getDouble(HttpServletRequest request, String param, double defaultVal) {
        String val = getString(request, param);
        double ret = defaultVal;
        if (StringUtils.isNotBlank(val)) {
            try {
                ret = Double.parseDouble(val);
            } catch (Exception ex) {
                logger.warn("The input [{}] is not a double.", val);
            }
        }
        return ret;
    }

    public static double[] getDoubles(HttpServletRequest request, String param) {
        String[] vals = getStrings(request, param);
        double[] rets = null;
        if (vals != null) {
            rets = new double[vals.length];
            for (int i = 0; i < vals.length; i++) {
                try {
                    rets[i] = Double.parseDouble(vals[i]);
                } catch (Exception ex) {
                    logger.warn("The input [{}] is not a double.", vals[i]);
                    rets[i] = 0D;
                }
            }
        }
        return rets;
    }

    public static double[] getDoubles(HttpServletRequest request, String param, double defaultVal) {
        String[] vals = getStrings(request, param);
        double[] rets = null;
        if (vals != null) {
            rets = new double[vals.length];
            for (int i = 0; i < vals.length; i++) {
                try {
                    rets[i] = Double.parseDouble(vals[i]);
                } catch (Exception ex) {
                    logger.warn("The input [{}] is not a double.", vals[i]);
                    rets[i] = defaultVal;
                }
            }
        }
        return rets;
    }

    public static Date getDate(HttpServletRequest request, String param) {
        return getDate(request, param, null);
    }

    public static Date getDateTime(HttpServletRequest request, String param) {
        return DateUtil.parseDateTime(getString(request, param));
    }

    public static Date getDate(HttpServletRequest request, String param, Date defaultVal) {
        String val = getString(request, param);
        Date ret = defaultVal;
        if (val != null) {
            try {
                ret = DateUtil.parseDate(val);
            } catch (BaseRuntimeException ex) {
                logger.warn("The input [{}] is not a date.", val);
            }
        }
        return ret;
    }

    public static Date[] getDates(HttpServletRequest request, String param) {
        String[] vals = getStrings(request, param);
        Date[] rets = null;
        if (vals != null) {
            rets = new Date[vals.length];
            for (int i = 0; i < vals.length; i++) {
                try {
                    rets[i] = DateUtil.parseDate(vals[i]);
                } catch (Exception ex) {
                    logger.warn("The input [{}] is not a date.", vals[i]);
                    rets[i] = null;
                }
            }
        }
        return rets;
    }

    public static void setRequestAttr(HttpServletRequest request, String paramName, Object obj) {
        request.setAttribute(paramName, obj);
    }

    public static Object getRequestAttr(HttpServletRequest request, String paramName) {
        return request.getAttribute(paramName);
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
        Object obj = request.getAttribute(key);
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        }

        return null;
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
        Object obj = context.getAttribute(key);
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        }

        return null;
    }

    private ParamUtil() {
    }
}
