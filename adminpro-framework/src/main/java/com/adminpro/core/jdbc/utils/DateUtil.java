package com.adminpro.core.jdbc.utils;

import com.adminpro.core.jdbc.Consts;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class DateUtil {
    private DateUtil() {
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static Date getToday() throws Exception {
        return Formatter.parseDate(Formatter.formatDate(new Date()));
    }

    public static boolean isSaturday(Date date) {
        if (date == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) == 7;
    }

    public static boolean isSunday(Date date) {
        if (date == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    public static boolean isFutureDate(Date date) throws Exception {
        if (date == null) {
            return false;
        }
        return Formatter.parseDate(Formatter.formatDate(date)).compareTo(getToday()) > 0;
    }

    public static boolean isFutureDateTime(Date date) {
        if (date == null) {
            return false;
        }
        return date.compareTo(new Date()) > 0;
    }

    public static boolean isPastDate(Date date) throws Exception {
        if (date == null) {
            return false;
        }
        return Formatter.parseDate(Formatter.formatDate(date)).compareTo(getToday()) < 0;
    }

    public static boolean isFromToday(Date date) throws Exception {
        if (date == null) {
            return false;
        }
        return Formatter.parseDate(Formatter.formatDate(date)).compareTo(getToday()) >= 0;
    }

    public static boolean isToToday(Date date) throws Exception {
        if (date == null) {
            return false;
        }
        return Formatter.parseDate(Formatter.formatDate(date)).compareTo(getToday()) <= 0;
    }

    public static boolean isToday(Date date) throws Exception {
        if (date == null) {
            return false;
        }
        return Formatter.parseDate(Formatter.formatDate(date)).compareTo(getToday()) == 0;
    }

    public static boolean isAM() throws Exception {
        return isAM(new Date());
    }

    public static boolean isAM(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.AM_PM) == 0 ? true : false;
    }

    public static boolean isMsAM() throws Exception {
        return isMsAM(new Date());
    }

    public static boolean isMsAM(Date date) throws Exception {
        Date dateAM = DateUtil.defaultTime(date, Consts.DFT_MS_AM);
        return !date.after(dateAM);
    }

    public static Date getMsAM() throws Exception {
        return DateUtil.defaultTime(new Date(), Consts.DFT_MS_AM);
    }

    public static Date getMsPM() throws Exception {
        return DateUtil.defaultTime(new Date(), Consts.DFT_MS_PM);
    }

    public static int getCurrentMonth() throws Exception {
        return getMonth(new Date());
    }

    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    public static int getCurrentYear() throws Exception {
        return getYear(new Date());
    }

    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static Date defaultTime(Date date) throws Exception {
        return defaultTime(date, Consts.DFT_TIME + " " + Consts.TT_AM);
    }

    public static Date defaultTime(Date date, String defaulTime) throws Exception {
        String dateStr = Formatter.formatDate(date);
        dateStr += " " + defaulTime;
        return Formatter.parseDateTime(dateStr);
    }

    public static Date getFirstDateOfCurrentMonth() {
        return getFirstDateOfMonth(new Date());
    }

    public static Date getFirstDateOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getLastDateOfCurrentMonth() {
        return getLastDateOfMonth(new Date());
    }

    public static Date getLastDateOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getNextDay(Date date) {
        Calendar nextDate = Calendar.getInstance();
        nextDate.setTime(date);
        nextDate.add(Calendar.DAY_OF_YEAR, 1);

        return nextDate.getTime();
    }

    public static Date getPreviousDay(Date date) {
        Calendar nextDate = Calendar.getInstance();
        nextDate.setTime(date);
        nextDate.add(Calendar.DAY_OF_YEAR, -1);

        return nextDate.getTime();
    }

    public static Date getPreviousWeek(Date date) {
        Calendar nextDate = Calendar.getInstance();
        nextDate.setTime(date);
        nextDate.add(Calendar.DAY_OF_YEAR, -7);

        return nextDate.getTime();
    }

    public static Date getLatestDate(Date[] dates) {
        if (dates == null) {
            return null;
        }

        Date latestDate = null;
        for (Date date : dates) {
            if (latestDate == null || latestDate.getTime() < date.getTime()) {
                latestDate = date;
            }
        }
        return latestDate;
    }

    public static Date getEarliestDate(Date[] dates) {
        if (dates == null) {
            return null;
        }

        Date earliestDate = null;
        for (Date date : dates) {
            if (earliestDate == null || earliestDate.getTime() > date.getTime()) {
                earliestDate = date;
            }
        }
        return earliestDate;
    }

    public static String getAge(Date birthDate) throws Exception {
        int age = calculateAge(birthDate);
        return age < 0 ? "Unknown" : String.valueOf(age);
    }

    public static String getAgeDesc(Date birthDate) throws Exception {
        if (birthDate == null) {
            return "Unknown (Age: Unknown)";
        }

        return Formatter.formatDate(birthDate) + " (Age: " + getAge(birthDate) + ")";
    }

    public static int calculateAge(Date dateOfBirth) throws Exception {
        return calculateAge(dateOfBirth, new Date());
    }

    public static int calculateAge(Date dateOfBirth, Date endDate) {
        if (dateOfBirth == null || endDate == null) {
            return -1;
        }

        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        int age = end.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (end.get(Calendar.MONTH) < dob.get(Calendar.MONTH)
                || (end.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
                && end.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    public static String calculateAgeDetail(Date dateOfBirth) throws Exception {
        return calculateAgeDetail(dateOfBirth, new Date());
    }

    public static String calculateAgeDetail(Date dateOfBirth, Date endDate) throws Exception {
        if (dateOfBirth == null || endDate == null) {
            return "Unknown";
        }

        int[] durations = getDateDuration(dateOfBirth, endDate, true);
        return getDateDurationDesc(durations, true);
    }

    public static boolean isSameDay(Date sourceDate, Date targetDate) {
        if (sourceDate == null || targetDate == null) {
            return false;
        }

        String sourceDateStr = Formatter.formatDate(sourceDate);
        String targetDateStr = Formatter.formatDate(targetDate);

        return sourceDateStr.equals(targetDateStr);
    }

    public static String getDateDurationDesc(Date start) throws Exception {
        return getDateDurationDesc(start, new Date());
    }

    public static String getDateDurationDesc(Date start, Date end) throws Exception {
        return getDateDurationDesc(getDateDuration(start, end));
    }

    public static String getDateDurationDesc(int[] duration) throws Exception {
        return getDateDurationDesc(duration, false);
    }

    public static String getDateDurationDesc(int[] duration, boolean isForAge) {
        String desc = "";
        if (duration[0] > 0) {
            desc += duration[0] + (duration[0] > 1 ? " years" : " year");
        }
        if (duration[1] > 0) {
            desc += (StringUtils.isEmpty(desc) ? "" : " and ") + duration[1] + (duration[1] > 1 ? " months" : " month");
        }
        if (isForAge) {
            if (duration[0] <= 0 && duration[2] > 0) {
                desc += (StringUtils.isEmpty(desc) ? "" : " and ") + duration[2] + (duration[2] > 1 ? " days" : " day");
            }
        } else {
            if (duration[2] >= 0) {
                desc += (StringUtils.isEmpty(desc) ? "" : " and ") + (duration[2] + 1) + (duration[2] > 0 ? " days" : " day");
            }
        }
        return desc;
    }

    public static int[] getDateDuration(Date start) throws Exception {
        return getDateDuration(start, new Date());
    }

    public static int[] getDateDuration(Date start, Date end) throws Exception {
        return getDateDuration(start, end, false);
    }

    public static int[] getDateDuration(Date start, Date end, boolean isForAge) throws Exception {
        return getDateDuration(start, end, isForAge, false);
    }

    public static int[] getDateDuration(Date start, Date end, boolean isForAge, boolean compareTime) throws Exception {
        int[] duration = new int[]{0, 0, 0, 0, 0};
        if (start == null || end == null) {
            return duration;
        }

        if (!compareTime) {
            start = Formatter.parseDate(Formatter.formatDate(start));
            end = Formatter.parseDate(Formatter.formatDate(end));
        }
        if (start.compareTo(end) > 0) {
            return duration;
        }

        GregorianCalendar startCal = new GregorianCalendar();
        startCal.setTime(start);
        GregorianCalendar endCal = new GregorianCalendar();
        endCal.setTime(end);

        duration[0] = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
        duration[1] = endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
        duration[2] = endCal.get(Calendar.DAY_OF_MONTH) - startCal.get(Calendar.DAY_OF_MONTH) + (isForAge ? 0 : 0);

        int i = 0;
        if (compareTime) {
            duration[3] = endCal.get(Calendar.HOUR_OF_DAY) - startCal.get(Calendar.HOUR_OF_DAY);
            duration[4] = endCal.get(Calendar.MINUTE) - startCal.get(Calendar.MINUTE);
            if (duration[4] < 0) {
                duration[3]--;
                duration[4] += 60;

            }
            if (duration[3] < 0) {
                i--;
                duration[2]--;
                duration[3] += 24;
            }
        }

        if (duration[2] < 0) {
            duration[1]--;
            duration[2] = startCal.getActualMaximum(Calendar.DAY_OF_MONTH) - startCal.get(Calendar.DAY_OF_MONTH) + endCal.get(Calendar.DAY_OF_MONTH) + (isForAge ? i : 0);
        }
        if (duration[1] < 0) {
            duration[0]--;
            duration[1] += 12;
        }
        return duration;
    }

    public static int[] getTimeDuration(Date start, Date end) {
        int[] interval = new int[]{0, 0, 0};
        if (start == null || end == null) {
            return interval;
        }

        long between = (end.getTime() - start.getTime()) / 1000;

        interval[0] = (int) (between / (24 * 3600));
        interval[1] = (int) (between % (24 * 3600) / 3600);
        interval[2] = (int) (between % 3600 / 60);

        return interval;
    }

    public static Date getFinancialYearStart() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getToday());
        int month = cal.get(Calendar.MONTH);
        if (month < Calendar.APRIL) {
            cal.add(Calendar.YEAR, -1);
        }
        cal.set(Calendar.MONTH, Calendar.APRIL);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date getFinancialYearEnd() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getToday());
        int month = cal.get(Calendar.MONTH);
        if (month >= Calendar.APRIL) {
            cal.add(Calendar.YEAR, 1);
        }
        cal.set(Calendar.MONTH, Calendar.APRIL);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    /**
     * Note: First day of this week is Sunday.
     *
     * @return
     * @throws Exception
     */
    public static Date getMondayOfWeek(int week) throws Exception {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DATE, (-dayOfWeek + 1) + (week * 7));
        return Formatter.parseDate(Formatter.formatDate(calendar.getTime()));
    }

    /**
     * Note: First day of this week is Sunday.
     *
     * @return
     * @throws Exception
     */
    public static Date getTuesdayOfWeek(int week) throws Exception {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DATE, (-dayOfWeek + 2) + (week * 7));
        return Formatter.parseDate(Formatter.formatDate(calendar.getTime()));
    }

    /**
     * Note: First day of this week is Sunday.
     *
     * @return
     * @throws Exception
     */
    public static Date getWednesdayOfWeek(int week) throws Exception {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DATE, (-dayOfWeek + 3) + (week * 7));
        return Formatter.parseDate(Formatter.formatDate(calendar.getTime()));
    }

    /**
     * Note: First day of this week is Sunday.
     *
     * @return
     * @throws Exception
     */
    public static Date getThursdayOfWeek(int week) throws Exception {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DATE, (-dayOfWeek + 4) + (week * 7));
        return Formatter.parseDate(Formatter.formatDate(calendar.getTime()));
    }

    /**
     * Note: First day of this week is Sunday.
     *
     * @return
     * @throws Exception
     */
    public static Date getFridayOfWeek(int week) throws Exception {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DATE, (-dayOfWeek + 5) + (week * 7));
        return Formatter.parseDate(Formatter.formatDate(calendar.getTime()));
    }

    /**
     * Note: First day of this week is Sunday. Last day is Saturday
     *
     * @return
     * @throws Exception
     */
    public static Date getLastDateTimeOfWeek(int week) throws Exception {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DATE, (-dayOfWeek + 6) + (week * 7));
        return Formatter.parseDate(Formatter.formatDate(calendar.getTime()));
    }

    /**
     * @param date
     * @param pattern
     * @return
     */
    public static Date getDateFromString(String date, String pattern) {
        Date dt = new Date();
        try {
            if (date != null) {
                SimpleDateFormat df2 = new SimpleDateFormat(pattern);
                dt = df2.parse(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dt;

    }

    /**
     * @param sourceDate
     * @return
     * @throws Exception
     */
    public static Date removeTime(Date sourceDate) {
        if (sourceDate == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(sourceDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
