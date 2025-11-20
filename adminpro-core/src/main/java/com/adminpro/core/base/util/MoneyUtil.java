package com.adminpro.core.base.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author simon
 */
public class MoneyUtil {
    private MoneyUtil() {
    }

    /**
     * 用于数据库存的long转换成页面显示的值
     *
     * @param money
     * @return
     */
    public static String formatToDoubleString(Long money) {
        if (money == null || money <= 0) {
            return "0.00";
        }
        double d = Double.valueOf(money);
        double a = d / 100.00;
        String sal = new DecimalFormat("#0.00").format(a);
        return sal;
    }

    public static Double formatToDouble(Long money) {
        if (money == null || money <= 0) {
            return 0d;
        }
        double d = Double.valueOf(money);
        double a = d / 100.00;
        String sal = new DecimalFormat("#0.00").format(a);
        return Double.valueOf(sal);
    }

    /**
     * 用于页面取到的double string转成long类型给数据库存储
     *
     * @param money
     * @return
     */
    public static Long formatToLong(String money) {
        double d = 0;
        try {
            d = Double.valueOf(money);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        double a = d * 100.00;
        return (long) a;
    }

    /**
     * 隐藏消息中的金额
     *
     * @param msg
     * @return
     */
    public static String hideMoney(String msg) {
        String reg = "((\\d{1,3}(,\\d{3})+?|\\d+)(\\.\\d{2})?|(\\.\\d{2}))\\s*元";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(msg);
        String s = matcher.replaceAll("** 元");
        return s;
    }
}
