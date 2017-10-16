package tech.tgls.mms.auth.common;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 日期工具类
 */
public class DateUtil {

    public static final String FORMAT_DATE_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE_YMD = "yyyy-MM-dd";
    public static final String FORMAT_DATE_YMDHM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_DATE_YMD_HH00 = "yyyy-MM-dd HH:00";
    public static final String FORMAT_DATE_NYR = "yyyy年MM月dd日";

    /**
     * 将Date类型转换为字符串
     *
     * @param date 日期类型
     * @return 日期字符串
     */
    public static String format(Date date) {
        return format(date, FORMAT_DATE_YMDHMS);
    }

    /**
     * 将Date类型转换为字符串
     *
     * @param date    日期类型
     * @param pattern 字符串格式
     * @return 日期字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return "null";
        }
        if (pattern == null || pattern.equals("") || pattern.equals("null")) {
            pattern = FORMAT_DATE_YMDHMS;
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 将字符串时间格式化
     *
     * @param date
     * @param format
     * @param pattern
     * @return
     */
    public static String strToStr(String date, String format, String pattern) {
        Date strToDate = strToDate(date, format);
        return dateToString(strToDate, pattern);
    }

    /**
     * 将字符串转换为Date类型
     *
     * @param date 字符串类型
     * @return 日期类型
     */
    public static Date format(String date) {
        return format(date, null);
    }

    /**
     * 将字符串转换为Date类型
     *
     * @param date    字符串类型
     * @param pattern 格式
     * @return 日期类型
     */
    public static Date format(String date, String pattern) {
        if (pattern == null || pattern.equals("") || pattern.equals("null")) {
            pattern = FORMAT_DATE_YMDHMS;
        }
        if (date == null || date.equals("") || date.equals("null")) {
            return new Date();
        }
        Date d = null;
        try {
            d = new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException pe) {
        }
        return d;
    }

    /**
     * 得到年月日时分秒字符串
     *
     * @return 字符串
     */
    public static String getDateYmdHms() {
        Date date = new Date();
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = formatter1.format(date);
        return now;
    }

    /**
     *  获取年月
     * @return
     */
    public static String getDateYearMonth() {
        Date date = new Date();
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMM");
        String now = formatter1.format(date);
        return now;
    }
    /**
     * 获取年
     * @param date
     * @return
     */
    public static String getYear(Date date) {
        SimpleDateFormat formatter1 = new SimpleDateFormat("y");
        String year = formatter1.format(date);
        return year;
    }
    /**
     * 获取月份
     * @param date
     * @return
     */
    public static String getMonth(Date date) {
        SimpleDateFormat formatter1 = new SimpleDateFormat("M");
        String now = formatter1.format(date);
        return now;
    }

    public static Date getNextDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, 1);
        date = c.getTime();
        return date;
    }

    public static Date getNextHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, 1);
        date = c.getTime();
        return date;
    }

    public static Date getNextMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, 1);
        date = c.getTime();
        return date;
    }

    public static Date getPreviousMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, -1);
        date = c.getTime();
        return date;
    }

    public static Date getPreviousWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, -7);
        date = c.getTime();
        return date;
    }

    public static Date getPreviousDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1);
        date = c.getTime();
        return date;
    }

    //获得当前月的最后一天
    public static Date getLastDayOfMonth() {
        String dateFormat = FORMAT_DATE_YMD;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);//设为当前月的1号
        lastDate.add(Calendar.MONTH, 1);//加一个月，变为下月的1号
        lastDate.add(Calendar.DATE, -1);//减去一天，变为当月最后一天

        return strToDate(sdf.format(lastDate.getTime()), dateFormat);
    }

    //获取当月第一天
    public static Date getFirstDayOfMonth() {
        String dateFormat = FORMAT_DATE_YMD;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);//设为当前月的1号
        return strToDate(sdf.format(lastDate.getTime()), dateFormat);
    }

    //获取当天时间
    public static Date getNowTime(String dateformat) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);//可以方便地修改日期格式
        return strToDate(dateFormat.format(now), dateformat);

    }

    public static Date getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static String getYesterdayStr() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat(FORMAT_DATE_YMD).format(cal.getTime());
        return yesterday;
    }
    public  static String getYesterdayLongStr(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat(FORMAT_DATE_YMDHMS).format(cal.getTime());
        return yesterday;
    }

    public static String getYesterdayStrNYR() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat(FORMAT_DATE_NYR).format(cal.getTime());
        return yesterday;
    }

    /**
     * 获取前天的日期
     * @return
     */
    public static String getDayBeforeYesterdayStr() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        String day = new SimpleDateFormat(FORMAT_DATE_YMD).format(cal.getTime());
        return day;
    }

    /**
     * 获取前天的日期
     * @return
     */
    public static Date getDayBeforeYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        return cal.getTime();
    }

    public static Date computeDate(Date date, int diffDay) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, diffDay);
        date = c.getTime();
        return date;
    }

    public static String dateToString(Date d) {
        return dateToString(d, FORMAT_DATE_YMD);
    }

    public static String dateToString(Date d, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(d);
    }

    public static Date strToDate(String dateStr) {
        return strToDate(dateStr, "MM/dd/yyyy");
    }

    public static Date strToDate(String dateStr, String format) {
        if (StrUtil.isNullOrEmpty(dateStr)) {
            return null;
        }
        SimpleDateFormat dateFmt = new SimpleDateFormat(format);

        dateFmt.setLenient(false);
        ParsePosition pos = new ParsePosition(0);
        return dateFmt.parse(dateStr, pos);
    }

    public static boolean compareDate(Date date1, Date date2){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date1).equals(simpleDateFormat.format(date2));
    }

    public static boolean compareInDays(int days) {
        Date lastDayOfMonth = getLastDayOfMonth();
        Date begin = computeDate(lastDayOfMonth, days);
        Date nowTime = getNowTime(FORMAT_DATE_YMD);
        return nowTime.getTime() > begin.getTime() && nowTime.getTime() <= lastDayOfMonth.getTime();
    }

    public static int getDiscrepantDays(Date begin, Date end) {
        int days = (int) ((begin.getTime() - end.getTime()) / (1000*3600*24));
        return Math.abs(days);
    }
    public static int getDiscrepantMin(Date begin, Date end) {
        int min = (int) ((begin.getTime() - end.getTime()) / (1000*60));
        return Math.abs(min);
    }
    public static Date getMinTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
        return cal.getTime();
    }

    public static Date getMaxTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
        return cal.getTime();
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        LocalDate date1 = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(date1, date2);
    }

    public static int dayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static String dayOfWeekOfStr(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE", Locale.CHINA);
        return dateFormat.format(c.getTime());
    }

    /**
     * 获取当天的开始时间
     * @return
     */
    public static Date getStartTime() {
        Calendar currentDate = new GregorianCalendar();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        return (Date) currentDate.getTime().clone();
    }

    /**
     * 获取当天的结束时间
     * @return
     */
    public static Date getEndTime() {
        Calendar currentDate = new GregorianCalendar();
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        return ((Date) currentDate.getTime().clone());
    }

    /*
    获取距离当天结束还剩的秒数
     */
    public static long getRemainSec() {
        long current=System.currentTimeMillis();//当前时间毫秒数
        long zero=current/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
        long twelve=zero+24*60*60*1000-1;//今天23点59分59秒的毫秒数
        return Math.abs((twelve - current)/1000);
    }
}