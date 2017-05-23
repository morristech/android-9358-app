package com.xmd.manager.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sdcm on 15-12-17.
 */
public class DateUtil {

    private static final String FORMAT = "yyyy-MM-dd";

    private static final String FORMAT2 = "yyyyMMdd";

    private static final String FORMAT3 = "yyyy-MM-dd HH:mm:ss";

    public static final Long MONTH = 29 * 24 * 60 * 60 * 1000l;

    public static final long DAY_MILLIS_SECOND = 24 * 3600 * 1000;

    /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<>();
    private static final Object lockObj = new Object();

    public static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern, Locale.getDefault());
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }


    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        return format.format(new Date());
    }

    /**
     * Monday
     *
     * @return
     */
    public static String getMondayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 2);
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        return format.format(cal.getTime());
    }

    public static String getMondayOfWeek(String formatString) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 2);
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(cal.getTime());
    }

    /**
     * @return
     */
    public static String getFirstDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        return format.format(cal.getTime());
    }


    public static String getFirstDayOfMonth(String formatString) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(cal.getTime());
    }

    public static String getFirstDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        return format.format(cal.getTime());
    }

    public static String getFirstDayOfWeek(Date date, String FORMAT) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        String impTimeBegin = sdf.format(cal.getTime());
        return impTimeBegin;
    }

    public static String getLastDayOfWeek(Date date, String FORMAT) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        //String impTimeBegin = sdf.format(cal.getTime());
        cal.add(Calendar.DATE, 6);
        String impTimeEnd = sdf.format(cal.getTime());
        return impTimeEnd;
    }


    /**
     * 获取指定定毫秒数之前的日期
     *
     * @param timeDiff
     * @return
     */
    public static String getDesignatedDate(long timeDiff) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        long nowTime = System.currentTimeMillis();
        long designTime = nowTime - timeDiff;
        return format.format(designTime);
    }

    /**
     * 获取前几天的日期
     */
    public static String getPrefixDate(String count) {
        Calendar cal = Calendar.getInstance();
        int day = 0 - Integer.parseInt(count);
        cal.add(Calendar.DATE, day);   // int amount   代表天数
        Date datNew = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        return format.format(datNew);
    }

    /**
     * 日期转换成字符串
     *
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        return format.format(date);
    }

    public static String dateToStringMinite(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    public static Integer getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static Integer getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static Integer getCurrentQuarter() {

        return getCurrentMonth() / 4 + 1;
    }

    /**
     * 返回指定日期的季的第一天
     *
     * @param
     * @return
     */
    public static Date getFirstDayOfQuarter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getFirstDayOfQuarter(calendar.get(Calendar.YEAR), getQuarterOfYear(date));
    }

    public static String getFirstDayOfQuarterString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return dateToString(getFirstDayOfQuarter(calendar.get(Calendar.YEAR), getQuarterOfYear(date)));
    }

    /**
     * 返回指定年季的季的第一天
     *
     * @param year
     * @param quarter
     * @return
     */
    public static Date getFirstDayOfQuarter(Integer year, Integer quarter) {
        Calendar calendar = Calendar.getInstance();
        Integer month = new Integer(0);
        if (quarter == 1) {
            month = 1 - 1;
        } else if (quarter == 2) {
            month = 4 - 1;
        } else if (quarter == 3) {
            month = 7 - 1;
        } else if (quarter == 4) {
            month = 10 - 1;
        } else {
            month = calendar.get(Calendar.MONTH);
        }
        return getFirstDayOfMonth(year, month);
    }

    /**
     * 返回指定年月的月的第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getFirstDayOfMonth(Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        if (year == null) {
            year = calendar.get(Calendar.YEAR);
        }
        if (month == null) {
            month = calendar.get(Calendar.MONTH);
        }
        calendar.set(year, month, 1);
        return calendar.getTime();
    }

    /**
     * 返回指定日期的季度
     *
     * @param date
     * @return
     */
    public static int getQuarterOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) / 3 + 1;
    }

    /**
     * 字符串转换日期
     *
     * @param str
     * @return
     */
    public static Date stringToDate(String str) {
        //str =  " 2008-07-10 19:20:00 " 格式
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        if (!str.equals("") && str != null) {
            try {
                return format.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 字符串转换日期
     *
     * @param str
     * @return
     */
    public static Date stringToDateMinute(String str) {
        //str =  " 2008-07-10 19:20:00 " 格式
        SimpleDateFormat format;
        if (str.length() == 10) {
            format = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }

        if (Utils.isEmpty(str)) {
            str = "1970-01-01 00:00:00";
        }
        try {
            return format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * 字符串转换日期
     *
     * @param str
     * @return
     */
    public static Date stringToDateSecond(String str) {
        //str =  " 2008-07-10 19:20:00 " 格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (Utils.isEmpty(str)) {
            str = "1970-01-01 00:00:00";
        }
        try {
            return format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    //java中怎样计算两个时间如：“21:57”和“08:20”相差的分钟数、小时数 java计算两个时间差小时 分钟 秒 .
    public void timeSubtract() {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = null;
        Date end = null;
        try {
            begin = dfs.parse("2004-01-02 11:30:24");
            end = dfs.parse("2004-03-26 13:31:40");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒

        long day1 = between / (24 * 3600);
        long hour1 = between % (24 * 3600) / 3600;
        long minute1 = between % 3600 / 60;
        long second1 = between % 60;
        System.out.println("" + day1 + "天" + hour1 + "小时" + minute1 + "分"
                + second1 + "秒");
    }

    /**
     * 毫秒值转换成日期
     */
    public static String longToDate(Long date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return format.format(calendar.getTime());
    }

    /**
     * 日期转化成毫秒值
     */

    public static Long dateToLong(String date) {
        Date mDate = stringToDateMinute(date);
        Long longTime = mDate.getTime();
        return longTime;
    }

    public static Long stringDateToLong(String date) {
        SimpleDateFormat format = null;


        if (Utils.isEmpty(date)) {
            date = "1970-01-01 00:00:00";
        }
        if (date.length() < 12) {
            format = new SimpleDateFormat(FORMAT);
        } else {
            format = new SimpleDateFormat(FORMAT3);
        }
        try {
            Date mDate = format.parse(date);
            return mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 毫秒值转换成日期
     */
    public static String long2Date(Long date, String formatString) {
        String formatStr = "";
        if (Utils.isNotEmpty(formatString)) {
            formatStr = formatString;
        } else {
            formatStr = FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return format.format(calendar.getTime());
    }

    /**
     * 毫秒值转换成日期
     */
    public static String long2Date(Long date) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT3);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return format.format(calendar.getTime());
    }

    public static boolean isCurrentYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        if (date.getTime() > cal.getTime().getTime()) {
            return true;
        } else {
            return false;
        }
    }

    //    public static String getFirstDayOfLastMonth(String str,String formatDay) {
//        String format = "";
//        if(Utils.isNotEmpty(formatDay)){
//            format = formatDay;
//        }else {
//            format = FORMAT;
//        }
//        SimpleDateFormat df = new SimpleDateFormat(format);
//        Date date = null;
//        try {
//            date = df.parse(str);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            date = new Date();
//        }
//        Map<String, String> map = DateUtil.getFirstDayAndLastDayMonth(date);
//        return map.get("first");
//    }
    public static String getLastDayOfLastMonth(String str) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        Map<String, String> map = DateUtil.getFirstdayAndLastdayMonth(date);
        return map.get("last");
    }

    public static Map<String, String> getFirstdayAndLastdayMonth(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.MONTH, -1);
        Date theDate = calendar.getTime();

        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        //上个月最后一天
        calendar.add(Calendar.MONTH, 1);    //加一个月
        calendar.set(Calendar.DATE, 1);        //设置为该月第一天
        calendar.add(Calendar.DATE, -1);    //再减一天即为上个月最后一天
        String day_last = df.format(calendar.getTime());

        Map<String, String> map = new HashMap<String, String>();
        map.put("first", day_first + " 00:00:00");
        map.put("last", day_last + " 23:59:59");
        return map;
    }

    public static String getLastDayOfLastMonth(String dateString, String formatDay) {
        String format = "";
        if (Utils.isNotEmpty(formatDay)) {
            format = formatDay;
        } else {
            format = FORMAT;
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        Map<String, String> map = DateUtil.getFirstDayAndLastDayMonth(date);
        return map.get("last");
    }

    public static Map<String, String> getFirstDayAndLastDayMonth(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.MONTH, -1);
        Date theDate = calendar.getTime();

        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        //上个月最后一天
        calendar.add(Calendar.MONTH, 1);    //加一个月
        calendar.set(Calendar.DATE, 1);        //设置为该月第一天
        calendar.add(Calendar.DATE, -1);    //再减一天即为上个月最后一天
        String day_last = df.format(calendar.getTime());

        Map<String, String> map = new HashMap<String, String>();
        map.put("first", day_first + " 00:00:00");
        map.put("last", day_last + " 23:59:59");
        return map;
    }

    public static String getFirstDayOfMonth(String dateString, String formatString) {
        Date date = null;
        String dayFirst = "";
        String dayFormat = "";
        if (Utils.isNotEmpty(formatString)) {
            dayFormat = formatString;
        } else {
            dayFormat = FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat(dayFormat);
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        dayFirst = format.format(calendar.getTime());
        return dayFirst;
    }

    public static String getLastDayOfMonth(String dateString, String formatString) {
        Date date = null;
        String dayLast = "";
        String dayFormat = "";
        if (Utils.isNotEmpty(formatString)) {
            dayFormat = formatString;
        } else {
            dayFormat = FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat(dayFormat);
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.DATE, -1);
        dayLast = format.format(calendar.getTime());

        return dayLast;
    }

    public static String getFirstDayOfNextMonth(String dateString, String formatString) {
        Date date = null;
        String dayNextMonthFirst = "";
        String dayFormat = "";
        if (Utils.isNotEmpty(formatString)) {
            dayFormat = formatString;
        } else {
            dayFormat = FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat(dayFormat);
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, 1);
        dayNextMonthFirst = format.format(calendar.getTime());
        return dayNextMonthFirst;
    }

    public static String getFirstDayOfLastMonth(String str) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        Map<String, String> map = DateUtil.getFirstdayAndLastdayMonth(date);
        return map.get("first");
    }

    public static String getFirstDayOfLastMonth(String dateString, String formatString) {
        Date date = null;
        String dayNextMonthFirst = "";
        String dayFormat = "";
        if (Utils.isNotEmpty(formatString)) {
            dayFormat = formatString;
        } else {
            dayFormat = FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat(dayFormat);
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DATE, 1);
        dayNextMonthFirst = format.format(calendar.getTime());
        return dayNextMonthFirst;
    }

    public static String getWeekInZh(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "周日";
            case Calendar.MONDAY:
                return "周一";
            case Calendar.TUESDAY:
                return "周二";
            case Calendar.WEDNESDAY:
                return "周三";
            case Calendar.THURSDAY:
                return "周四";
            case Calendar.FRIDAY:
                return "周五";
            case Calendar.SATURDAY:
                return "周六";
        }
        return "错误";
    }

    /**
     * 获取指定定毫秒数之后的日期
     *
     * @param currentTime
     * @return
     */
    public static String getNextDate(long currentTime) {
        String FORMAT = "MM月dd日";
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        long nowTime = currentTime;
        long nextDate = nowTime + DAY_MILLIS_SECOND;
        return format.format(nextDate);
    }

    /**
     * 获取指定定毫秒数之前的日期
     *
     * @param currentTime
     * @return
     */

    public static String getLastDate(long currentTime) {
        String FORMAT = "MM月dd日";
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        long nowTime = currentTime;
        long nextDate = nowTime - DAY_MILLIS_SECOND;
        return format.format(nextDate);
    }

    /**
     * 获取当前日期
     *
     * @param currentTime
     * @return
     */
    public static String getCurrentDate(long currentTime) {
        String FORMAT = "MM月dd日";
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        long nowTime = currentTime;
        return format.format(nowTime);
    }

    /**
     * 获取当前日期
     *
     * @param currentTime
     * @return
     */
    public static String getCurrentDate(long currentTime, String dateFormat) {
        String FORMAT = dateFormat;
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        long nowTime = currentTime;
        return format.format(nowTime);
    }

    /**
     * 获取当前年月日期
     *
     * @param currentTime
     * @return
     */
    public static String getDate(long currentTime) {
        String FORMAT = "yyyy-MM-dd";
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        long nowTime = currentTime;
        return format.format(nowTime);
    }

    public static Long DateToLong(String data) {
        SimpleDateFormat format;
        format = new SimpleDateFormat("MM月dd日");
        Date date = null;
        try {
            date = format.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (data == null) {
            return System.currentTimeMillis();
        }
        return date.getTime();
    }

    /**
     * 根据毫秒值获取当前季度
     *
     * @param currentMillis
     * @return
     */

    public static int getSeason(Long currentMillis) {

        int season = 0;
        Date date = stringToDateMinute(longToDate(currentMillis));

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                season = 1;
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                season = 2;
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                season = 3;
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                season = 4;
                break;
            default:
                break;
        }
        return season;
    }

}
