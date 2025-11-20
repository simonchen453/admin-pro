package com.adminpro.core.base.util;

import com.adminpro.core.exceptions.BaseRuntimeException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {
    private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

    private static final Random RANDOM = new Random();

    public static int getInt(String val) {
        return getInt(val, 0);
    }

    public static int getInt(String val, int defaultValue) {
        int ret = defaultValue;
        if (isNotBlank(val)) {
            try {
                ret = Integer.parseInt(val);
            } catch (Exception x) {
                logger.warn("The input [{}] is not a integer.", val);
            }
        }
        return ret;
    }

    public static long getLong(String val) {
        return getLong(val, 0L);
    }

    public static long getLong(String val, long defaultValue) {
        long ret = defaultValue;
        if (isNotBlank(val)) {
            try {
                ret = Long.parseLong(val);
            } catch (Exception x) {
                logger.warn("The input [{}] is not a long.", val);
            }
        }
        return ret;
    }

    public static double getDouble(String val) {
        return getDouble(val, 0);
    }

    public static double getDouble(String val, double defaultValue) {
        double ret = defaultValue;
        if (isNotBlank(val)) {
            try {
                ret = Double.parseDouble(val);
            } catch (Exception x) {
                logger.warn("The input [{}] is not a double.", val);
            }
        }
        return ret;
    }

    public static boolean isTrue(String str) {
        return "Y".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str)
                || "true".equalsIgnoreCase(str) || "T".equalsIgnoreCase(str);
    }

    public static int[] getInts(String str) {
        if (isBlank(str)) {
            return null;
        }

        String[] ss = str.trim().split("\\s*,\\s*");    // trim the spaces
        if (CollectionUtil.isEmpty(ss)) {
            return null;
        }

        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < ss.length; i++) {
            try {
                ints.add(Integer.valueOf(ss[i].trim()));
            } catch (Exception x) {
                // do nothing
            }
        }
        return CollectionUtil.toIntArray(ints);
    }

    public static String getString(InputStream is) {
        try {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException x) {
            throw new BaseRuntimeException("Failed to convert inputstream to string.", x);
        }
    }

    /**
     * check if the given string empty (null or "")
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * generate a random string with specified length
     *
     * @param length
     * @return
     */
    public static String genRandomString(int length) {
        String base = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = RANDOM.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String genUuid() {
        return UUID.randomUUID().toString();
    }

    public static boolean isValidUuid(String str) {
        if (isBlank(str)) {
            return false;
        }

        final String regex = "^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static String[] removeBlank(String[] array) {
        if (CollectionUtil.isEmpty(array)) {
            return null;
        }

        return Arrays.stream(array).filter(value -> isNotBlank(value)).toArray(size -> new String[size]);
    }

    public static String getNonNull(String str) {
        if (isBlank(str)) {
            return "";
        }

        return str;
    }

    public static String cout(String in, String defaultStr) {
        String out = defaultStr;
        if (!isBlank(in)) {
            out = escapeHtml(in);
        }
        return out;
    }

    public static String cout(String in) {
        return cout(in, "");
    }

    public static String escapeHtml(String in) {
        return StringEscapeUtils.escapeHtml4(in);
    }

    /**
     * convert new line (\n) to <br>
     *
     * @param string
     * @return
     */
    public static String nl2br(String string) {
        return (string != null) ? string.replace("\n", "<br>") : null;
    }

    public static boolean contains(String[] array, String value) {
        return Arrays.stream(array).anyMatch(value::equals);
    }

    public static boolean contains(String str, String val) {
        String[] vals = str.trim().split("\\s*,\\s*");    // trim the spaces
        return contains(vals, val);
    }

    public static String join(String[] strings) {
        return join(strings, null);
    }

    public static String join(String[] strings, String delimiter) {
        if (CollectionUtil.isEmpty(strings)) {
            return null;
        }

        if (isBlank(delimiter)) {
            delimiter = ",";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            if (i < strings.length - 1) {
                sb.append(delimiter);
            }
        }

        return sb.toString();
    }

    public static String join(int[] ints) {
        if (CollectionUtil.isEmpty(ints))
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ints.length; i++) {
            sb.append(ints[i]);
            if (i < ints.length - 1) {
                sb.append(',');
            }
        }

        return sb.toString();
    }

    public static String join(long[] longs) {
        if (CollectionUtil.isEmpty(longs))
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longs.length; i++) {
            sb.append(longs[i]);
            if (i < longs.length - 1) {
                sb.append(',');
            }
        }

        return sb.toString();
    }

    private StringUtil() {
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }
}
