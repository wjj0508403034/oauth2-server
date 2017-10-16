package tech.tgls.mms.auth.common;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author shenyuyang
 * @date 16/9/24
 */
public class Convert {

    public static Boolean isInt(String str) {

        boolean isValidInteger = false;
        try {
            Integer.parseInt(str);
            isValidInteger = true;
        } catch (NumberFormatException ex) {
            // s is not an integer
        }

        return isValidInteger;
    }

    /**
     * 是否数字, 包括小数
     * 或者 NumberUtils.isNumber or StringUtils.isNumeric from Apache Commons Lang
     * 或者 android.text.TextUtils.isDigitsOnly(CharSequence str)
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
        char localeMinusSign = currentLocaleSymbols.getMinusSign();

        if (!Character.isDigit(str.charAt(0)) && str.charAt(0) != localeMinusSign) return false;

        boolean isDecimalSeparatorFound = false;
        char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();

        for (char c : str.substring(1).toCharArray()) {
            if (!Character.isDigit(c)) {
                if (c == localeDecimalSeparator && !isDecimalSeparatorFound) {
                    isDecimalSeparatorFound = true;
                    continue;
                }
                return false;
            }
        }
        return true;
    }


    /**
     * 判断字符串是否是"true"或"false"(不区分大小写)
     *
     * @param str
     * @return
     */
    public static Boolean isBool(String str) {
        if (str == null) return false;
        if (StrUtil.equalsIgnoreCase(str, "true") || StrUtil.equalsIgnoreCase(str, "false")) return true;

        return false;
    }


    public static String toString(Object obj) {
        return StrUtil.toString(obj);
    }

    public static boolean toBool(Object obj) {
        return Boolean.parseBoolean(toString(obj));
    }

    public static int toInt(Object obj) {
        try {
            return Integer.parseInt(toString(obj));
        } catch (Exception ex) {
            return 0;
        }
    }

    public static long toLong(Object obj) {
        try {
            return Long.parseLong(toString(obj));
        } catch (Exception ex) {
            return 0;
        }
    }

    public static short toShort(Object obj) {
        return Short.parseShort(toString(obj));
    }

    public static float toFloat(Object obj) {
        return Float.parseFloat(toString(obj));
    }

    public static double toDouble(Object obj) {
        return Double.parseDouble(toString(obj));
    }


    public static String timeToString(Date dateTime) {
        return timeToString(dateTime, "yyyy-MM-dd HH:mm");
    }

    public static String timeToString(Date dateTime, String format) {
        return new SimpleDateFormat(format).format(dateTime); // yyyy-MM-dd HH:mm:ss
    }

    public static DateTime toTime(String str, String format) {
        return DateTimeFormat.forPattern(format).parseDateTime(str);
    }

    public static DateTime toTime(int year, int month, int day, int hour, int minute) {
        return new DateTime(year, month, day, hour, minute);
    }

    public static DateTime toTime(int year, int month, int day) {
        return toTime(year, month, day, 0, 0);
    }


}

