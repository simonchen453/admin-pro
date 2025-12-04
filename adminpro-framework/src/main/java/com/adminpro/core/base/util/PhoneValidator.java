package com.adminpro.core.base.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 电话号码校验类
 *
 * @author simon
 */
public class PhoneValidator {

    public static final Pattern mobile = Pattern.compile("^[1][0-9][0-9]{9}$");
    /**
     * 验证带区号的
     */
    public static final Pattern phone1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");
    /**
     * 验证没有区号的
     */
    public static final Pattern phone2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(final String str) {
        Matcher m = null;
        boolean b = false;
        m = mobile.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isPhone(final String str) {
        Matcher m = null;
        boolean b = false;

        if (str.length() > 9) {
            m = phone1.matcher(str);
            b = m.matches();
        } else {
            m = phone2.matcher(str);
            b = m.matches();
        }
        return b;
    }
}
