package com.xmd.technician.common;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/23.
 */
public class RelativeDateFormatUtil {
    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";


    public static String format(Long time) {
        long mDate = new Date().getTime() - time;
        if (mDate < 1L * ONE_MINUTE) {
            long seconds = toSeconds(mDate);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (mDate < 45L * ONE_MINUTE) {
            long minutes = toMinutes(mDate);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (mDate < 24L * ONE_HOUR) {
            long hours = toHours(mDate);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (mDate < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (mDate < 30L * ONE_DAY) {
            long days = toDays(mDate);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (mDate < 12L * 4L * ONE_WEEK) {
            long months = toMonths(mDate);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(mDate);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }

    public static String formatModify(Long time) {
        long mDate = new Date().getTime() - time;
        if (mDate < 1L * ONE_MINUTE) {
            long seconds = toSeconds(mDate);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (mDate < 45L * ONE_MINUTE) {
            long minutes = toMinutes(mDate);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (mDate < 24L * ONE_HOUR) {
            long hours = toHours(mDate);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (mDate < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (mDate < 30L * ONE_DAY) {
            long days = toDays(mDate);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        } else {
            // 大于30天
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(new Date(time));
        }

        /*
        if (mDate < 12L * 4L * ONE_WEEK) {
            long months = toMonths(mDate);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(mDate);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }*/
    }


    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }

}
