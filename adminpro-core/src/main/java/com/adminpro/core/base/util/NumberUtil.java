package com.adminpro.core.base.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * NumberUtil
 *
 * @author simon
 */
public final class NumberUtil {
    public static final String NUMBER = "#,##0.00";

    private static final Random RANDOM = new Random();

    public static String genRandomNumber(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(String.valueOf(RANDOM.nextInt(10)));
        }
        return sb.toString();
    }

    public static String formatNumber(Number number, String pattern) {
        if (pattern == null)
            pattern = NUMBER;

        NumberFormat nf = new DecimalFormat(pattern);
        return nf.format(number);
    }

    private NumberUtil() {
    }
}
