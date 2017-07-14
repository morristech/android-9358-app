package com.shidou.commonlibrary.util;

import com.shidou.commonlibrary.helper.TimeProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by SD_ZR on 16-4-11.
 * 用来处理时间的工具类
 */
public class DateUtils {
    public static final long DAY_TIME_MS = 24 * 3600 * 1000L;

    private static final String WEEK_ZH = "日一二三四五六";

    public static final String DF_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String DF_JUST_DAY = "yyyy-MM-dd";
    public static final String DF_DEFAULT_ZH = "yyyy年MM月dd日 HH:mm:ss";
    public static final String DF_JUST_DAY_WITHOUT_LINE = "yyMMdd";
    public static final String DF_JUST_TIME = "HH:mm:ss";
    public static final String DF_WITH_CN_ALL = "yyyy年MM月dd日 HH:mm:ss";
    public static final String DF_JUST_YEAR_MONTH_ZH = "yyyy年MM月";
    public static final String DF_JUST_YEAR_MONTH = "yyyy-MM";
    public static final String DF_JUST_DATE_TIME = "MM-dd HH:mm";


    private static final Object lockObj = new Object();

    /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<>();

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
     * 和当前时间比较，是否为同一天
     *
     * @param date
     * @return
     */
    public static boolean isSameDayWithNow(Date date) {
        if (date != null) {
            Date now = new Date(TimeProvider.currentTimeMillis());
            return getSdf(DF_JUST_DAY).format(now).equals(getSdf(DF_JUST_DAY).format(date));
        }
        return false;
    }

    /*************
     * 字符串日期到Date,失败返回 null
     ************************/
    public static Date doString2Date(String dateString, String df) {
        try {
            return getSdf(df).parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date doString2Date(String dateString) {
        return doString2Date(dateString, DF_DEFAULT);
    }

    /**************
     * 字符串日期到long,失败返回0
     **********************************/
    public static long doString2Long(String dateString, String df) {
        try {
            Date date = getSdf(df).parse(dateString);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException ignore) {

        }
        return 0;
    }

    public static long doString2Long(String dateString) {
        return doString2Long(dateString, DF_DEFAULT);
    }

    /********************************************
     * 日期到字符串, 日期到Long直接使用Date.getTime()
     ********************************************/

    public static String doDate2String(Date date, String df) {
        return getSdf(df).format(date);
    }

    public static String doDate2String(Date date) {
        return doDate2String(date, DF_DEFAULT);
    }

    /***********************************************
     * Long 到字符串, Long到Date直接使用new Date(Long)
     *************************************************/
    public static String doLong2String(long date, String df) {
        return doDate2String(new Date(date), df);
    }

    public static String doLong2String(long date) {
        return doDate2String(new Date(date));
    }


    /**********************************************
     * 字符串，一种格式到另外一种,失败返回null
     *****************************************/
    public static String doString2String(String dateString, String oldFormat, String newFormat) {
        Date date = doString2Date(dateString, oldFormat);
        if (date != null) {
            return doDate2String(date, newFormat);
        }
        return null;
    }

    public static String doString2StringZH(String date) {
        return doString2String(date, DF_DEFAULT, "yyyy年MM月dd日 HH:mm:ss");
    }

    public static Long doDate2Long(String date) {
        Date mDate = stringToDateMinute(date);
        Long longTime = mDate.getTime();
        return longTime;
    }

    public static Date stringToDateMinute(String str) {
        //str =  " 2008-07-10 19:20:00 " 格式
        SimpleDateFormat format;
        if (str.length() == 10) {
            format = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }

        if (str == null || str.length() == 0) {
            str = "1970-01-01 00:00:00";
        }
        try {
            return format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }


    public static String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());
    }

    /**
     * 数字周转到为中文的周
     *
     * @param week 1-7
     * @return 日一二三四五六
     */
    public static String weekNumberToZh(int week) {
        return WEEK_ZH.charAt(week - 1) + "";
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat(DF_JUST_DAY);
        return format.format(new Date());
    }

    public static int dateToInt(String str) {
        if (!str.isEmpty() && str.length() == 10) {
            String year = str.substring(0, 4);
            String mounth = str.substring(5, 7);
            String day = str.substring(8, 10);
            String newStr = year + mounth + day;
            int result = Integer.parseInt(newStr);
            return result;
        }
        return -1;
    }
}
