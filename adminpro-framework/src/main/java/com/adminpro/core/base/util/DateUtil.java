package com.adminpro.core.base.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * DateUtil
 *
 * @author simon
 */
public final class DateUtil {
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static final String DATE = "yyyy-MM-dd";
    public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String MONTH = "yyyyMM";

    public static final Locale LOCALE = new Locale("en", "US");

    /**
     * parse the given string following the format: yyyy-MM-dd ~ yyyy-MM-dd
     * the returned dates, dates[0]: from; dates[1]: to
     *
     * @param dateRange
     * @return
     */
    public static Date[] parseDateRange(String dateRange) {
        if (StringUtils.isBlank(dateRange)) {
            return new Date[0];
        }

        int idx = dateRange.indexOf('~');
        try {
            Date from = parseDate(dateRange.substring(0, idx).trim());
            Date to = parseDate(dateRange.substring(idx + 1).trim());
            return new Date[]{from, to};
        } catch (Exception x) {
            logger.warn("Failed to parse date ranger {}.", dateRange);
        }
        return new Date[0];
    }

    public static Date now() {
        return new Date();
    }

    /**
     * Get Date Without Time
     *
     * @param datetime
     * @return
     */
    public static Date extractDate(Date datetime) {
        if (datetime == null)
            return null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(datetime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    public static Date today() {
        return extractDate(now());
    }

    public static Date parseDate(String dateStr) {
        return parseDateTime(dateStr, DATE);
    }

    public static Date parseDateTime(String dateStr) {
        return parseDateTime(dateStr, DATETIME);
    }

    public static Date parseDateTime(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }

        if (pattern == null) {
            pattern = DATETIME;
        }

        Date date = null;
        try {
            DateFormat df = new SimpleDateFormat(pattern);
            date = df.parse(dateStr);
        } catch (ParseException x) {
            logger.warn("Failed to parse the date string [{}, {}].", dateStr, pattern);
        }
        return date;
    }

    public static String formatDate(Date date) {
        return formatDateTime(date, DATE);
    }

    public static String formatDateTime(Date date) {
        return formatDateTime(date, null);
    }

    public static String formatDateTime(Date date, String pattern) {
        if (date == null) {
            return "";
        }

        if (pattern == null) {
            // default pattern will be used.
            pattern = DATETIME;
        }

        DateFormat df = new SimpleDateFormat(pattern, LOCALE);
        return df.format(date);
    }

    /**
     * d1 > d2 => > 0
     * d1 = d2 => = 0
     * d1 < d2 => < 0
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int compare(Date d1, Date d2) {
        if (d1 == null && d2 == null) {
            return 0;
        }

        if (d1 == null) {
            return -1;
        }

        if (d2 == null) {
            return 1;
        }

        long d = d1.getTime() - d2.getTime();
        int result = 0;
        if (d > 0) {
            result = 1;
        } else if (d < 0) {
            result = -1;
        }
        return result;
    }

    /**
     * calculate the minutes difference between two dates
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int minsBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }

        final int MIN_IN_MILLIS = 60 * 1000;
        int diffInMins = 0;
        if (startDate.after(endDate)) {
            diffInMins = (int) ((startDate.getTime() - endDate.getTime()) / MIN_IN_MILLIS);
        } else {
            diffInMins = (int) ((endDate.getTime() - startDate.getTime()) / MIN_IN_MILLIS);
        }

        return diffInMins;
    }

    /**
     * calculate the hours difference between two dates
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static double hoursBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }

        final int HOUR_IN_MILLIS = 60 * 60 * 1000;
        double diffInHours;
        if (startDate.after(endDate)) {
            diffInHours = (double) (startDate.getTime() - endDate.getTime()) / HOUR_IN_MILLIS;
        } else {
            diffInHours = (double) (endDate.getTime() - startDate.getTime()) / HOUR_IN_MILLIS;
        }

        return diffInHours;
    }

    /**
     * calculate the days between two dates
     * if the start date is later than end date, the two dates will be exchanged.
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int daysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }

        if (startDate.after(endDate)) { // exchange
            Date tmpDate = startDate;
            startDate = endDate;
            endDate = tmpDate;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        int days = 0;
        while (compare(cal.getTime(), endDate) < 0) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            days++;
        }
        return days;
    }

    /**
     * compare two dates if they are in same year
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isInSameYear(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    /**
     * compare two dates if they are in same month
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isInSameMonth(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    /**
     * compare two dates if they are in same week
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isInSameWeek(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * compare two dates if they are in same day
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isInSameDay(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 当天的开始时间
     *
     * @return
     */
    public static Date startOfTodDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 当天的结束时间
     *
     * @return
     */
    public static Date endOfTodDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 结束时间
     *
     * @return
     */
    public static Date getEndOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date date2 = calendar.getTime();
        return date2;
    }

    /**
     * 开始时间
     *
     * @return
     */
    public static Date getStartOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date2 = calendar.getTime();
        return date2;
    }

    /**
     * 昨天的开始时间
     *
     * @return
     */
    public static Date startOfYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 昨天的结束时间
     *
     * @return
     */
    public static Date endOfYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 某天的开始时间
     *
     * @param dayUntilNow 距今多少天的日期（正数是往后多少天，负数是往前多少天）
     * @return 日期
     */
    public static Date startOfSomeDay(int dayUntilNow) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, dayUntilNow);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 某天的开始时间
     *
     * @param dayUntilNow 距今多少天的日期（正数是往后多少天，负数是往前多少天）
     * @return 日期
     */
    public static Date endOfSomeDay(int dayUntilNow) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.add(Calendar.DATE, dayUntilNow);
        Date date = calendar.getTime();
        return date;
    }

    public static Date endOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date d = calendar.getTime();
        return d;
    }

    public static Date minutesAgo(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, -minute);
        Date date = calendar.getTime();
        return date;
    }

    public static Date minutesAfter(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, minute);
        Date date = calendar.getTime();
        return date;
    }

    public static Date minutesAgo(Date d, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.MINUTE, -minute);
        Date date = calendar.getTime();
        return date;
    }

    public static Date minutesAfter(Date d, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.MINUTE, minute);
        Date date = calendar.getTime();
        return date;
    }

    public static Date offset(int type, int step) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(type, step);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 某天的年月日
     *
     * @param dayUntilNow 距今多少天以前
     * @return 年月日map  key为  year month day
     */
    public static Map<String, Object> getYearMonthAndDay(int dayUntilNow) {
        Map<String, Object> map = new HashMap<String, Object>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -dayUntilNow);
        map.put("year", calendar.get(Calendar.YEAR));
        map.put("month", calendar.get(Calendar.MONTH) + 1);
        map.put("day", calendar.get(Calendar.DAY_OF_MONTH));
        return map;
    }

    public static Date toDate(String date) {
        return toDate(date, null);
    }

    /**
     * 将一个字符串转换成日期格式
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Date toDate(String date, String pattern) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        if (pattern == null) {
            pattern = DATE;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date newDate = null;
        try {
            newDate = sdf.parse(date);
        } catch (Exception ex) {
            logger.error("日期格式化异常， pattern：{}", pattern);
        }
        return newDate;
    }

    /**
     * 把日期转换成字符串型
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String toString(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        if (pattern == null) {
            pattern = "yyyy-MM-dd";
        }
        String dateString = "";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            dateString = sdf.format(date);
        } catch (Exception ex) {
            logger.error("异常, {}", ex.getMessage());
        }
        return dateString;
    }

    public static String toString(Long time, String pattern) {
        if (time > 0) {
            if (time.toString().length() == 10) {
                time = time * 1000;
            }
            Date date = new Date(time);
            String str = toString(date, pattern);
            return str;
        }
        return "";
    }


    /**
     * 获取上个月的开始结束时间
     *
     * @return
     */
    public static Long[] getLastMonth() {
        // 取得系统当前时间
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        // 取得系统当前时间所在月第一天时间对象
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // 日期减一,取得上月最后一天时间对象
        cal.add(Calendar.DAY_OF_MONTH, -1);

        // 输出上月最后一天日期
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String months = "";
        String days = "";

        if (month > 1) {
            month--;
        } else {
            year--;
            month = 12;
        }
        if (!(String.valueOf(month).length() > 1)) {
            months = "0" + month;
        } else {
            months = String.valueOf(month);
        }
        if (!(String.valueOf(day).length() > 1)) {
            days = "0" + day;
        } else {
            days = String.valueOf(day);
        }
        String firstDay = "" + year + "-" + months + "-01";
        String lastDay = "" + year + "-" + months + "-" + days;

        Long[] lastMonth = new Long[2];
        lastMonth[0] = getDateline(firstDay);
        lastMonth[1] = getDateline(lastDay);

        return lastMonth;
    }


    /**
     * 获取当月的开始结束时间
     *
     * @return
     */
    public static Long[] getCurrentMonth() {
        // 取得系统当前时间
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        // 输出下月第一天日期
        int notMonth = cal.get(Calendar.MONTH) + 2;
        // 取得系统当前时间所在月第一天时间对象
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // 日期减一,取得上月最后一天时间对象
        cal.add(Calendar.DAY_OF_MONTH, -1);


        String months = "";
        String nextMonths = "";


        if (!(String.valueOf(month).length() > 1)) {
            months = "0" + month;
        } else {
            months = String.valueOf(month);
        }
        if (!(String.valueOf(notMonth).length() > 1)) {
            nextMonths = "0" + notMonth;
        } else {
            nextMonths = String.valueOf(notMonth);
        }
        String firstDay = "" + year + "-" + months + "-01";
        String lastDay = "" + year + "-" + nextMonths + "-01";
        Long[] currentMonth = new Long[2];
        currentMonth[0] = getDateline(firstDay);
        currentMonth[1] = getDateline(lastDay);

        return currentMonth;
    }

    public static long getDateline() {
        return System.currentTimeMillis() / 1000;
    }

    public static long getDateline(String date) {
        return getDateline(date, "yyyy-MM-dd");
    }

    public static long getDateHaveHour(String date) {
        return getDateline(date, "yyyy-MM-dd HH");
    }

    public static long getDateline(String date, String pattern) {
        Date date1 = toDate(date, pattern);
        if (logger != null) {
            return (long) (date1.getTime() / 1000);
        } else {
            return -1;
        }
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 格式化为年月日
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            return df.format(date);
        } catch (Exception e) {
            logger.error("异常, {}", e.getMessage());
            return null;
        }
    }

    public static String getWeekOfDate(Date date) {
        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekOfDays[w];
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (60 * 1000);
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前
     */
    public static String convertTimeToFormat(Date before) {
        long curTime = System.currentTimeMillis() / (long) 1000;
        long time = curTime - before.getTime();

        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天前";
        } else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 + "个月前";
        } else if (time >= 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 / 12 + "年前";
        } else {
            return "刚刚";
        }
    }

    //获取本周的开始时间
    public static Date getBeginDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date date = cal.getTime();
        return date;
    }

    //获取本周的结束时间
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date date = cal.getTime();
        return date;
    }

    //获取上周的开始时间
    public static Date getBeginDayOfLastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, -7);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date date = cal.getTime();
        return date;
    }

    //获取上周的结束时间
    public static Date getEndDayOfLastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, -7);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date date = cal.getTime();
        return date;
    }

    /**
     * 根据生日计算年龄
     *
     * @param birthday
     * @return
     */
    public static int getAgeByBirth(Date birthday) {
        int age = 0;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(birthday);

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            logger.error("异常, {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取当年的开始时间
     *
     * @return
     */
    public static Date startOfCurrentYear() {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.DATE, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 获取下一年的开始时间
     *
     * @return
     */
    public static Date startOfNextYear() {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.add(Calendar.YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 当月的开始时间
     *
     * @return
     */
    public static Date startOfCurrentMth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 下个月的开始时间
     *
     * @return
     */
    public static Date startOfNextMth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        return day + "天" + hour + "小时" + min + "分钟";
    }

    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        Date date = new Date(time);
        return date;
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return formatDate(now(), DATE);
    }

    private DateUtil() {
    }
}
