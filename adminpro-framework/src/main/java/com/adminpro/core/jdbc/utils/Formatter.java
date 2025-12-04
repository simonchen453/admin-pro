package com.adminpro.core.jdbc.utils;

import com.adminpro.core.jdbc.Consts;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class Formatter {
    public static final String NUMBER = "#,##0.00";
    public static final String WHOLE_NUMBER = "#.##";
    public static final String DATE = "dd/MM/yyyy";
    public static final String TIME_SHORT = "hh:mm";
    public static final String TIME = "hh:mm a";
    public static final String TIME2 = "hh.mm a";
    public static final String TIME3 = "hh:mm:ss a";
    public static final String TIME4 = "hh:mma";
    public static final String TIME5 = "hh.mma";
    public static final String TIME6 = "HH.mm";
    public static final String TIME7 = "HH:mm";
    public static final String DATETIME = DATE + " " + TIME;
    public static final String DATETIME2 = DATE + " " + TIME2;
    public static final String DATETIME3 = DATE + " " + TIME3;
    public static final String DATE_LONG = "EEEEE, dd/MM/yyyy";
    public static final String DATE_ID = "yyyyMMdd";
    public static final Locale LOCALE = new Locale("en", "SG");
    public static final String DATE_ORDINAL = " 'day of' MMMM yyyy";
    public static final String DATE_TIME_ORDINAL = " 'day of' MMMM yyyy hh:mm a";
    public static final String DATE_TIME_DESC = DATE + " 'at' " + TIME;
    public static final String PDF_DATE = "dd MMMM yyyy";
    public static final String PDF_DATE1 = "dd MMM yyyy";
    public static final String NON_DATE = "dd-MMMM-yyyy";

    //=========================================================
    // number format/parse methods
    //=========================================================

    /**
     * format the given number with default pattern.
     * e.g. 2,009.05
     *
     * @param num
     * @return
     */
    public static String formatNumber(Double num) {
        String pattern = NUMBER;
        if (num == null) {
            num = 0D;
        }
        return formatNumber(num, pattern);
    }

    public static String formatEditNumber(Double num) {
        if (num == null) {
            num = 0D;
        }

        String pattern = "###0.00";
        if (Math.floor(num) == num) {
            pattern = "####";
        }
        return formatNumber(num, pattern);
    }

    /**
     * format the given number with the specified pattern
     *
     * @param number
     * @param pattern
     * @return
     */
    public static String formatNumber(Number number, String pattern) {
        if (number == null) {
            return "";
        }

        if (pattern == null) {
            pattern = NUMBER;
        }

        NumberFormat nf = new DecimalFormat(pattern);
        return nf.format(number);
    }

    /**
     * format percent value;
     *
     * @param num
     * @return
     */
    public static String formatPercent(double num) {
        if (num == 0.0d) {
            return "0";
        }

        NumberFormat nf = NumberFormat.getPercentInstance(LOCALE);
        return nf.format(num);
    }

    /**
     * format the currency value.
     *
     * @param num
     * @return
     */
    public static String formatAmount(Double num) {
        return "$" + formatNumber(num);
    }

    /**
     * format the currency value.
     *
     * @param num
     * @return
     */
    public static String formatAmount(String currency, Double num) {
        return (StringUtil.isEmpty(currency) ? "$" : currency) + formatNumber(num);
    }

    /**
     * format the currency value.
     *
     * @param num
     * @return
     */
    public static String formatAmount(String currency, String otherCurrency, Double num) {
        String currencyDesc = StringUtil.isEmpty(currency) ? "$" : currency;
        if (Consts.OTHERS.equals(currency) && !StringUtil.isEmpty(otherCurrency)) {
            currencyDesc = otherCurrency;
        }
        return (StringUtil.getNonNull(currencyDesc)) + formatNumber(num);
    }

    /**
     * Parse the number string. the acceptable format is: #,##0.00
     * e.g. '2,008.88'
     *
     * @param numStr
     * @return Number (Double, Float...)
     */
    public static Number parseNumber(String numStr) throws Exception {
        return parseNumber(numStr, NUMBER);
    }

    /**
     * Parse the number string for the specified number pattern.
     *
     * @param numStr, pattern
     * @return Number (Double, Float...)
     */
    public static Number parseNumber(String numStr, String pattern) throws ParseException {
        if (numStr == null || "".equals(numStr)) {
            return null;
        }

        if (pattern == null) {
            pattern = NUMBER;
        }

        NumberFormat nf = new DecimalFormat(pattern);
        return nf.parse(numStr);
    }

    //=========================================================
    // date format/parse methods
    //=========================================================

    /**
     * Format the date object with default pattern ('dd/MM/yyyy').
     * e.g. '08/08/2008'
     *
     * @param date
     * @return String
     */
    public static String formatDate(Date date) {
        return formatDateTime(date, DATE);
    }

    /**
     * Format the date object with default pattern ('dd/MM/yyyy hh:mm a').
     * e.g. '08/08/2008 10:00 AM'
     *
     * @param date
     * @return
     */
    public static String formatDateTime(Date date) {
        return formatDateTime(date, DATETIME);
    }

    public static String formatDateOrdinal(Date date) {
        return formatDateTimeOrdinal(date, DATE_ORDINAL);
    }

    public static String formatDateTimeOrdinal(Date date) {
        return formatDateTimeOrdinal(date, DATE_TIME_ORDINAL);
    }

    public static String formatDateTimeOrdinal(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return NumberConverter.getOrdinal(calendar.get(Calendar.DAY_OF_MONTH), NumberConverter.SUP_SCRIPT) + formatDateTime(date, pattern);
    }

    /**
     * Format the date object with specified pattern.
     *
     * @param date
     * @param pattern
     * @return String
     */
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

    public static String formatDateWeek(Date date) {
        if (date == null) {
            return null;
        }
        String[] weekOfDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekDay < 0) {
            weekDay = 0;
        }
        return weekOfDays[weekDay];
    }

    public static Date[] parseDates(String datesStr) throws Exception {
        if (StringUtil.isEmpty(datesStr)) {
            return new Date[0];
        }

        String[] strs = datesStr.split("\\" + Consts.DFT_DELIMITER);
        Date[] dates = null;
        if (strs != null && strs.length > 0) {
            dates = new Date[strs.length];
            for (int i = 0; i < strs.length; i++) {
                dates[i] = parseDate(strs[i]);
            }
        }
        return dates;
    }

    /**
     * Parse the date with default pattern ('dd/MM/yyyy').
     * the acceptable string is: '08/08/2008'
     *
     * @param dateStr
     * @return
     */
    public static Date parseDate(String dateStr) throws Exception {
        return parseDateTime(dateStr, DATE);
    }

    public static Date parseDateTime(String dateStr) throws Exception {
        try {
            return parseDateTime(dateStr, DATETIME);
        } catch (Exception e) {
            return parseDateTime(dateStr, DATETIME2);
        }
    }

    /**
     * Parse the date with specified pattern.
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date parseDateTime(String dateStr, String pattern) throws ParseException {
        if (dateStr == null || "".equals(dateStr.trim())) {
            return null;
        }

        if (pattern == null) {
            pattern = DATETIME;
        }

        DateFormat df = new SimpleDateFormat(pattern, LOCALE);
        return df.parse(dateStr);
    }

    public static Date parseTime12(String time, String timeType) throws Exception {
        if (StringUtil.isEmpty(time) || StringUtil.isEmpty(timeType)) {
            return null;
        }
        try {
            return parseDateTime(time + " " + timeType, TIME);
        } catch (Exception e) {
            return parseDateTime(time + " " + timeType, TIME2);
        }
    }

    public static Date parseTime24(String timeStr) throws Exception {
        try {
            return parseDateTime(timeStr, TIME6);
        } catch (Exception e) {
            return parseDateTime(timeStr, TIME7);
        }
    }

    private Formatter() {
    }
}
