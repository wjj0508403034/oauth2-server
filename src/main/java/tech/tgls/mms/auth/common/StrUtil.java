package tech.tgls.mms.auth.common;


import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shenyuyang
 * @date 16/9/24
 */
public class StrUtil {


    /**
     * 检查字符串是否是 null 或者空白字符。多个空格在这里也返回true。
     *
     * @param target
     * @return
     */
    public static Boolean isNullOrEmpty(String target) {
        if (target != null) {
            return target.trim().length() == 0;
        }
        return true;
    }


    /**
     * 检查是否包含有效字符(空格等空白字符不算)
     *
     * @param target
     * @return
     */
    public static Boolean hasText(String target) {
        return !isNullOrEmpty(target);
    }


    /**
     * 比较两个字符串是否相等
     *
     * @param s1
     * @param s2
     * @return
     */
    public static Boolean equals(String s1, String s2) {

        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        if (s2.length() != s1.length()) return false;
        return s1.equals(s2);
    }


    /**
     * 比较两个字符串是否相等(不区分大小写)
     *
     * @param s1
     * @param s2
     * @return
     */
    public static Boolean equalsIgnoreCase(String s1, String s2) {

        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;

        if (s2.length() != s1.length()) return false;

        return s1.equalsIgnoreCase(s2);
    }


    /**
     * 将对象转为字符串，如果对象为 null，则转为空字符串(string.Empty)
     *
     * @param str
     * @return
     */
    public static String convertNotNull(Object str) {
        if (str == null) return "";
        return str.toString();
    }


    /**
     * 将 endString 附加到 srcString末尾，如果 srcString 末尾已包含 endString，则不再附加。
     *
     * @param srcString
     * @param endString
     * @return
     */
    public static String append(String srcString, String endString) {
        if (StrUtil.isNullOrEmpty(srcString)) return endString;
        if (StrUtil.isNullOrEmpty(endString)) return srcString;
        if (srcString.endsWith(endString)) return srcString;
        return srcString + endString;
    }

    /**
     * 从字符串中截取指定长度的一段，结果末尾没有省略号
     *
     * @param str
     * @param length
     * @return
     */
    public static String cutString(String str, int length) {
        if (str == null) return null;
        if (str.length() > length) return substring(str, 0, length);
        return str;
    }


    /**
     * 从字符串中截取指定长度的一段，如果源字符串被截取了，则结果末尾出现省略号...
     *
     * @param str    源字符串
     * @param length 需要截取的长度
     * @return
     */
    public static String cutStringDot(Object str, int length) {
        return cutStringDot(convertNotNull(str), length);
    }


    /**
     * 从字符串中截取指定长度的一段，如果源字符串被截取了，则结果末尾出现省略号...
     *
     * @param str    源字符串
     * @param length 需要截取的长度
     * @return
     */
    public static String cutStringDot(String str, int length) {
        if (str == null) return null;
        if (str.length() > length) return format("{0}...", substring(str, 0, length));
        return str;
    }


    /**
     * 对双引号进行编码
     *
     * @param src
     * @return
     */
    public static String encodeQuote(String src) {
        return src.replace("\"", "&quot;");
    }


    /**
     * 让 html 在 textarea 中正常显示。替换尖括号和字符&amp;lt;与&amp;gt;
     *
     * @param html
     * @return
     */
    public static String encodeTextarea(String html) {
        if (html == null) return null;
        return html.replace("&lt;", "&amp;lt;").replace("&gt;", "&amp;gt;").replace("<", "&lt;").replace(">", "&gt;");
    }


    /**
     * 获取所有整数 int 的字符串
     *
     * @param arrIds
     * @return
     */
    public static String getIds(int[] arrIds) {
        if (arrIds == null || arrIds.length == 0) return "";
        String ids = "";

        for (int x : arrIds) {
            ids += x + ",";
        }

        return trimEnd(ids);
    }

    public static String getIds(long[] arrIds) {
        if (arrIds == null || arrIds.length == 0) return "";
        String ids = "";
        for (long x : arrIds) {
            ids += x + ",";
        }
        return trimEnd(ids);
    }

    public static String getIds(List<Object> idList) {

        if (idList == null || idList.size() == 0) return "";

        String ids = "";

        for (Object x : idList) {
            ids += x + ",";
        }

        return trimEnd(ids);
    }


    /**
     * 用斜杠/拼接两个字符串
     *
     * @param strA
     * @param strB
     * @return
     */
    public static String join(String strA, String strB) {
        return join(strA, strB, "/");
    }


    /**
     * 根据指定的分隔符拼接两个字符串
     *
     * @param strA
     * @param strB
     * @param separator
     * @return
     */
    public static String join(String strA, String strB, String separator) {
        return (append(strA, separator) + trimStart(strB, separator));
    }


    /**
     * 从 srcString 的末尾剔除掉 trimString
     *
     * @param srcString
     * @param trimString
     * @return
     */
    public static String trimEnd(String srcString, String trimString) {
        if (isNullOrEmpty(trimString)) return srcString;
        if (srcString.endsWith(trimString) == false) return srcString;
        if (srcString.equals(trimString)) return "";
        return substring(srcString, 0, srcString.length() - trimString.length());
    }


    /**
     * 从 srcString 的开头剔除掉 trimString
     *
     * @param srcString
     * @param trimString
     * @return
     */
    public static String trimStart(String srcString, String trimString) {
        if (srcString == null) return null;
        if (trimString == null) return srcString;
        if (StrUtil.isNullOrEmpty(srcString)) return "";
        if (srcString.startsWith(trimString) == false) return srcString;
        return substring(srcString, trimString.length());
    }


    /**
     * 过滤掉 sql 语句中的单引号，并返回指定长度的结果
     *
     * @param rawSql
     * @param number
     * @return
     */
    public static String sqlClean(String rawSql, int number) {
        if (isNullOrEmpty(rawSql)) return rawSql;
        return cutString(rawSql, number).replace("'", "''");
    }

    /**
     * 得到字符串的 CamelCase 格式
     *
     * @param str
     * @return
     */
    public static String getCamelCase(String str) {
        if (isNullOrEmpty(str)) return str;
        return String.valueOf(str.charAt(0)).toLowerCase() + substring(str, 1);
    }

    public static String getTextFromHtml(String html, int length) {

        String txt = getTextFromHtml(html);
        return cutString(txt, length);
    }

    public static String getTextFromHtml(String html) {

        return Jsoup.clean(html, new Whitelist());
    }


    //-------------------- 以下是csharp 兼容方法 Format/Substring --------------------------------------


    /**
     * 将对象转换成字符串。如果是null返回empty; 如果是Date/DateTime, 则默认格式 yyyy-MM-dd HH:mm
     *
     * @param x
     * @return
     */
    public static String toString(Object x) {

        if (x == null) return "";

        if (x instanceof DateTime) {
            Date obj = ((DateTime) x).toDate();
            return Convert.timeToString(obj);
        } else if (x instanceof Date) {
            return Convert.timeToString((Date) x);
        }
        return x.toString();
    }

    /**
     * 将时间转换成字符串
     *
     * @param dateTime
     * @param format
     * @return
     */
    public static String toString(Date dateTime, String format) {
        return Convert.timeToString(dateTime, format);
    }


    /**
     * 删掉最后一个字符
     *
     * @param str
     * @return
     */
    public static String trimEnd(String str) {
        if (str == null) return null;
        if (str.length() == 0) return "";
        return str.substring(0, str.length() - 1);
    }


    /**
     * 提供 csharp 的兼容性方法 Format
     *
     * @param str
     * @param arg0
     * @return
     */
    public static String format(String str, Object arg0) {
        String x0 = (arg0 == null ? "" : arg0.toString());
        return str.replace("{0}", x0);
    }

    public static String format(String str, Object arg0, Object arg1) {
        String x0 = arg0 == null ? "" : arg0.toString();
        String x1 = arg1 == null ? "" : arg1.toString();
        return str.replace("{0}", x0).replace("{1}", x1);
    }

    public static String format(String str, Object arg0, Object arg1, Object arg2) {
        String x0 = arg0 == null ? "" : arg0.toString();
        String x1 = arg1 == null ? "" : arg1.toString();
        String x2 = arg2 == null ? "" : arg2.toString();
        return str.replace("{0}", x0).replace("{1}", x1).replace("{2}", x2);
    }

    public static String substring(String str, int startIndex) {
        if (str == null) return null;
        return str.substring(startIndex);
    }

    // 提供 csharp 的兼容性方法
    public static String substring(String str, int startIndex, int length) {
        if (str == null) return null;
        int endIndex = startIndex + length;
        return str.substring(startIndex, endIndex);
    }

    /**
     * 判断是否是手机号码
     * @param num
     * @return
     */
    public static boolean isMobileNum(String num) {
        String REGEX_MOBILE = "^\\d{11}$";
        Pattern pattern = Pattern.compile(REGEX_MOBILE,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(num);
        return matcher.matches();
    }

    /**
     * 隐藏手机号码中间四位
     * @param num
     * @return
     */
    public static  String maskedMobile(String num){
        if(isMobileNum(num)){
            return num.substring(0,3)+"****"+num.substring(7,11);
        }else {
            return null;
        }
    }

    public static String strEveryFourToAddSpaces(String str){
        if (isNullOrEmpty(str)) return  str;
        StringBuilder sb=new StringBuilder(str);
        int length=str.length()/4+str.length();
        for(int i=0;i<length;i++){
            if(i%5==0){
                sb.insert(i," ");
            }
        }
        return  sb.toString();
    }
}

