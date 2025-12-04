package com.adminpro.core.jdbc.utils;

import com.adminpro.core.jdbc.Consts;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.List;

public final class StringUtil {

    /**
     * check the str if it's empty (null or '').
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return StringUtils.isBlank(str);
    }

    public static boolean equals(String str1, String str2) {
        return str1 != null && str2 != null && str1.equals(str2);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 != null && str2 != null && str1.equalsIgnoreCase(str2);
    }

    /**
     * capitalize the first letter of word
     *
     * @param str
     * @return
     */
    public static String capitalize(String str) {
        return StringUtils.capitalize(str);
    }

    /**
     * uncapitalize the first letter of word
     *
     * @param str
     * @return
     */
    public static String uncapitalize(String str) {
        return StringUtils.uncapitalize(str);
    }

    /**
     * Note that, please use getBoolen() instead.
     *
     * @param str
     * @return
     */
    public static boolean isTrue(String str) {
        return Consts.YES.equalsIgnoreCase(str) || Consts.TRUE.equalsIgnoreCase(str);
    }

    public static boolean getBoolean(String str) {
        return Consts.YES.equalsIgnoreCase(str) || Consts.TRUE.equalsIgnoreCase(str);
    }

    public static String getString(boolean b) {
        return b ? Consts.YES : Consts.NO;
    }

    public static String escapeHtml(String in) {
        if (StringUtils.isBlank(in)) {
            return in;
        }

        return StringEscapeUtils.escapeHtml(in);
    }

    public static String escapeJavascript(String in) {
        if (StringUtils.isBlank(in)) {
            return "";
        }

        return StringEscapeUtils.escapeJavaScript(in);
    }

    /**
     * target should be string or number
     * source can be array, list or string splitted by '|'
     *
     * @param target
     * @param source
     * @return
     */
    public static boolean isIn(Object target, Object source) {
        if (target == null || source == null) {
            return false;
        }

        if (source instanceof Object[]) {
            Object[] sources = (Object[]) source;
            for (Object object : sources) {
                if (object == null) {
                    continue;
                }
                if (object.toString().equals(target.toString())) {
                    return true;
                }
            }
        } else if (source instanceof List<?>) {
            List<?> sources = (List<?>) source;
            for (Object object : sources) {
                if (object == null) {
                    continue;
                }
                if (object.toString().equals(target.toString())) {
                    return true;
                }
            }
        } else {
            String[] sources = source.toString().split("\\|");
            for (String object : sources) {
                if (object == null) {
                    continue;
                }
                if (object.equals(target.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String arrayToString(Object[] array) {
        return arrayToString(array, Consts.DFT_DELIMITER);
    }

    public static String arrayToString(Object[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i].toString());
            if (i != (array.length - 1)) {
                sb.append(delimiter);
            }
        }

        return sb.toString();
    }

    /**
     * return '-' if input string is null or '', otherwise input string.
     *
     * @param in
     * @return
     */
    public static String getNonEmpty(String in) {
        String out = Consts.EMPTY_STR;
        if (!isEmpty(in)) {
            out = in;
        }
        return out;
    }

    /**
     * return '' if input Long is null, otherwise input Long's string.
     *
     * @param in
     * @return
     */
    public static String getNonNull(Long in) {
        String out = "";
        if (in != null) {
            out = String.valueOf(in);
        }
        return out;
    }

    /**
     * return '' if input string is null, otherwise input string.
     *
     * @param in
     * @return
     */
    public static String getNonNull(String in) {
        String out = "";
        if (!isEmpty(in)) {
            out = in;
        }
        return out;
    }

    public static boolean isDigit(String str) {
        return NumberUtils.isDigits(str);
    }

    /**
     * return sub-string of input string. by default first 120 characters.
     *
     * @param desc
     * @return
     */
    public static String getShortDesc(String desc) {
        return getShortDesc(desc, 72);
    }

    public static String getShortDesc(String desc, int size) {
        if (isEmpty(desc)) {
            return null;
        }

        if (desc.length() > size) {
            return desc.substring(0, size) + "...";
        }
        return desc;
    }

    public static String[] cut(final String in, final int length) {
        if (isEmpty(in)) {
            return null;
        }

        int size = (int) Math.ceil(((double) in.length() / length));
        String[] out = new String[size];
        for (int i = 0; i < size; i++) {
            int from = i * length;
            int to = (i + 1) * length;
            if (to > in.length()) {
                to = in.length();
            }
            out[i] = in.substring(from, to);
        }
        return out;
    }

    public static String replaceLast(String text, String regex, String replacement) {
        if (isEmpty(text) || isEmpty(regex)) {
            return text;
        }

        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", getNonNull(replacement));
    }

    public static String showTextArea(String val) {
        return getNonNull(escapeHtml(val)).replaceAll("\r\n", "<br/>").replaceAll("\r", "<br/>").replaceAll("\n", "<br/>");
    }

    public static String unEscapeHtml(String val) {
        if (isEmpty(val)) {
            return val;
        }
        val = StringEscapeUtils.unescapeHtml(val);
        val = StringEscapeUtils.unescapeJavaScript(val);
        return val;
    }
}
