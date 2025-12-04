package com.adminpro.core.base.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author simon
 */
public class IdCardUtil {
    private IdCardUtil() {
    }

    public static final String NUMBER_REGEX = "[0-9]*";
    public static final String DATE_REGEX = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";

    public static boolean isMan(String idStr) {

        if (StringUtils.isNotEmpty(idStr)) {
            if (idStr.length() == 18) {
                String substring = idStr.substring(16, 17);
                if (Integer.valueOf(substring) % 2 == 1) {
                    return true;
                }
            } else if (idStr.length() == 15) {
                String substring = idStr.substring(14, 15);
                if (Integer.valueOf(substring) % 2 == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getAge(String strBirthday) {
        int returnAge;
        String[] strBirthdayArr = strBirthday.split("-");
        int birthYear = Integer.valueOf(strBirthdayArr[0]);
        int birthMonth = Integer.valueOf(strBirthdayArr[1]);
        int birthDay = Integer.valueOf(strBirthdayArr[2]);

        Date d = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int nowYear = cal.get(Calendar.YEAR);
        int nowMonth = cal.get(Calendar.MONTH) + 1;
        int nowDay = cal.get(Calendar.DAY_OF_MONTH);

        if (nowYear == birthYear) {
            returnAge = 0;//同年 则为0岁
        } else {
            int ageDiff = nowYear - birthYear; //年之差
            if (ageDiff > 0) {
                if (nowMonth == birthMonth) {
                    int dayDiff = nowDay - birthDay;//日之差
                    if (dayDiff < 0) {
                        returnAge = ageDiff - 1;
                    } else {
                        returnAge = ageDiff;
                    }
                } else {
                    int monthDiff = nowMonth - birthMonth;//月之差
                    if (monthDiff < 0) {
                        returnAge = ageDiff - 1;
                    } else {
                        returnAge = ageDiff;
                    }
                }
            } else {
                returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
            }
        }
        return returnAge;//返回周岁年龄

    }

    public static String getBirthday(String idStr) {
        String ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (idStr.length() != 15 && idStr.length() != 18) {
            return "";
        }
        // ================ 数字 除最后以为都为数字 ================
        if (idStr.length() == 18) {
            ai = idStr.substring(0, 17);
        } else if (idStr.length() == 15) {
            ai = idStr.substring(0, 6) + "19" + idStr.substring(6, 15);
        }
        if (isNumeric(ai) == false) {
            return "";
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = ai.substring(6, 10);// 年份
        String strMonth = ai.substring(10, 12);// 月份
        String strDay = ai.substring(12, 14);// 月份
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == true) {
            return strYear + "-" + strMonth + "-" + strDay;
        }

        return "";
    }

    public static String idCardValidate(String idStr) {
        String errorInfo = "";// 记录错误信息
        String[] valCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
        String ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (idStr.length() != 15 && idStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位。";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        if (idStr.length() == 18) {
            ai = idStr.substring(0, 17);
        } else if (idStr.length() == 15) {
            ai = idStr.substring(0, 6) + "19" + idStr.substring(6, 15);
        }
        if (isNumeric(ai) == false) {
            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = ai.substring(6, 10);// 年份
        String strMonth = ai.substring(10, 12);// 月份
        String strDay = ai.substring(12, 14);// 月份
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "身份证生日无效。";
            return errorInfo;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                errorInfo = "身份证生日不在有效范围。";
                return errorInfo;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            return errorInfo;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            return errorInfo;
        }
        // =====================(end)=====================

        // ================ 地区码时候有效 ================
        Hashtable<String, String> h = getAreaCode();
        if (h.get(ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误。";
            return errorInfo;
        }
        // ==============================================

        // ================ 判断最后一位的值 ================
        int totalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            totalmulAiWi = totalmulAiWi + Integer.parseInt(String.valueOf(ai.charAt(i))) * Integer.parseInt(wi[i]);
        }
        int modValue = totalmulAiWi % 11;
        String strVerifyCode = valCodeArr[modValue];
        ai = ai + strVerifyCode;

        if (idStr.length() == 18) {
            if (!StringUtils.equalsIgnoreCase(ai, idStr)) {
                errorInfo = "身份证无效，不是合法的身份证号码";
                return errorInfo;
            }
        } else {
            return "";
        }
        // =====================(end)=====================
        return "";
    }

    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    private static Hashtable<String, String> getAreaCode() {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile(NUMBER_REGEX);
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 功能：判断字符串是否为日期格式
     *
     * @param
     * @return
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern.compile(DATE_REGEX);
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
