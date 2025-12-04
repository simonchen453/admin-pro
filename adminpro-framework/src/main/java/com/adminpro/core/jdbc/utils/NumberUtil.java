package com.adminpro.core.jdbc.utils;

import java.math.BigDecimal;

public final class NumberUtil {
    private NumberUtil() {
    }

    public static final String SUP_SCRIPT = "P";
    public static final String SUB_SCRIPT = "B";

    public static boolean isPositive(Integer number) {
        if (number == null || number <= 0) {
            return false;
        }
        return true;
    }

    public static boolean isPositive(Long number) {
        if (number == null || number <= 0) {
            return false;
        }
        return true;
    }

    public static boolean isPositive(Double number) {
        if (number == null || compare(number, 0.0) <= 0) {
            return false;
        }
        return true;
    }

    public static String toString(Number number) throws Exception {
        if (number == null) {
            return null;
        }
        BigDecimal bigDecimal = new BigDecimal(number.toString());
        return bigDecimal.toString();
    }

    public static String toString(Double number) throws Exception {
        if (number == null) {
            return null;
        }
        BigDecimal bigDecimal = new BigDecimal(number);
        return bigDecimal.toString();
    }

    public static String toOrdinal(Integer i) {
        return toOrdinal(i, null);
    }

    public static String toOrdinal(Integer i, String script) {
        if (i == null) {
            return null;
        }
        String result = "";
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                result = "th";
                break;
            default:
                result = sufixes[i % 10];
        }
        if (SUP_SCRIPT.equals(script)) {
            result = "<span class=\"sup\">" + result + "</span>";
        } else if (SUB_SCRIPT.equals(script)) {
            result = "<span class=\"sub\">" + result + "</span>";
        }
        return i + result;
    }

    public static int compare(Double val1, Double val2) {
        if (val1 == null) {
            val1 = 0D;
        }
        if (val2 == null) {
            val2 = 0D;
        }
        return compare(new BigDecimal(val1), new BigDecimal(val2));
    }

    public static int compare(BigDecimal val1, BigDecimal val2) {
        return val1.compareTo(val2);
    }
}
