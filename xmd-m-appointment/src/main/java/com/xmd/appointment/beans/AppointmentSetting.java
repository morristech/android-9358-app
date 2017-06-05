package com.xmd.appointment.beans;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.shidou.commonlibrary.util.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by heyangya on 17-5-31.
 * 预约配置信息
 */

public class AppointmentSetting implements Serializable {
    private String appointDescription;//	预约描述
    private String appointType;//	预约类型
    private Integer downPayment;//	预约定金
    private Long nowTime;//	当前时间
    private Boolean onlyTechItem;//	是否只显示技师的项目	boolean	如果为true， 只显示techItemIds对应的项目，否则显示会所全部项目

    private List<String> techItemIds;//	技师可预约项目ID
    private List<TimeInfo> timeList;//预约时间表

    private String phoneNum;//	预约人手机号
    private String userId;//	预约人用户ID
    private String userName;//	预约人用户名


    public static class TimeInfo implements Serializable {
        private String day; //日期标题名称
        private List<TimeSection> time;//时间列表

        private SortedMap<Integer, TimeSection> hourMap;
        private SortedMap<Integer, List<TimeSection>> minuteMap;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public List<TimeSection> getTime() {
            return time;
        }

        public void setTime(List<TimeSection> time) {
            this.time = time;
        }

        //将时间拆分为小时和分钟
        private void parse() {
            hourMap = new TreeMap<>();
            minuteMap = new TreeMap<>();
            for (TimeSection section : time) {
                if (!"Y".equals(section.getStatus())) {
                    continue;
                }
                Integer hour = section.getHour();
                if (hour == null) {
                    continue;
                }
                Integer minute = section.getMinute();
                if (minute == null) {
                    continue;
                }
                if (!hourMap.containsKey(hour)) {
                    hourMap.put(hour, section);
                }
                List<TimeSection> minuteList = minuteMap.get(hour);
                if (minuteList == null) {
                    minuteList = new ArrayList<>();
                    minuteMap.put(hour, minuteList);
                }
                minuteList.add(section);
            }
        }

        public List<TimeSection> getValidHourList() {
            if (hourMap == null) {
                parse();
            }
            return new ArrayList<>(hourMap.values());
        }

        public List<TimeSection> getValidMinuteList(Integer hour) {
            if (minuteMap == null) {
                parse();
            }
            return minuteMap.get(hour);
        }
    }

    public static class TimeSection implements Serializable {
        private Integer day; //	第几天	从0开始， 当天为0， 第二天为1..., 依此类推
        private String status; //	时间状态
        private Long time; //	时间
        private String timeStr; //	时间串

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public String getTimeStr() {
            return timeStr;
        }

        public void setTimeStr(String timeStr) {
            this.timeStr = timeStr;
        }

        public Integer getHour() {
            try {
                return Integer.parseInt(timeStr.split(":")[0]);
            } catch (Exception e) {
                return null;
            }
        }

        public Integer getMinute() {
            try {
                return Integer.parseInt(timeStr.split(":")[1]);
            } catch (Exception e) {
                return null;
            }
        }

        public Long getMillisTime() {
            Integer h = getHour();
            Integer m = getMinute();
            Long t = 0L;
            if (h != null) {
                t += h * 3600 * 1000;
            }
            if (m != null) {
                t += m * 60 * 1000;
            }
            return t;
        }

        @Override
        public String toString() {
            return "TimeInfo{" +
                    "day=" + day +
                    ", status='" + status + '\'' +
                    ", time=" + time +
                    ", timeStr='" + timeStr + '\'' +
                    '}';
        }
    }

    public String getAppointDescription() {
        return appointDescription;
    }

    public void setAppointDescription(String appointDescription) {
        this.appointDescription = appointDescription;
    }

    public String getAppointType() {
        return appointType;
    }

    public void setAppointType(String appointType) {
        this.appointType = appointType;
    }

    public Integer getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(Integer downPayment) {
        this.downPayment = downPayment;
    }

    public Long getNowTime() {
        return nowTime;
    }

    public void setNowTime(Long nowTime) {
        this.nowTime = nowTime;
    }

    public Boolean getOnlyTechItem() {
        return onlyTechItem;
    }

    public void setOnlyTechItem(Boolean onlyTechItem) {
        this.onlyTechItem = onlyTechItem;
    }

    public List<String> getTechItemIds() {
        return techItemIds;
    }

    public void setTechItemIds(List<String> techItemIds) {
        this.techItemIds = techItemIds;
    }

    public List<TimeInfo> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<TimeInfo> timeList) {
        this.timeList = timeList;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "AppointmentSetting{" +
                "appointDescription='" + appointDescription + '\'' +
                ", appointType='" + appointType + '\'' +
                ", downPayment=" + downPayment +
                ", nowTime=" + nowTime +
                ", onlyTechItem=" + onlyTechItem +
                ", techItemIds=" + techItemIds +
                ", timeList=" + timeList +
                ", phoneNum='" + phoneNum + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public static long getDayMillis(TimeInfo timeInfo) {
        Calendar calendar = Calendar.getInstance();
        List<TimeSection> hourInfoList = timeInfo.getTime();
        if (hourInfoList != null && hourInfoList.size() > 0) {
            calendar.setTimeInMillis(System.currentTimeMillis() + hourInfoList.get(0).day * DateUtils.DAY_TIME_MS);
        } else {
            if ("今天".equals(timeInfo.day)) {
                calendar.setTimeInMillis(System.currentTimeMillis());
            } else if ("明天".equals(timeInfo.day)) {
                calendar.setTimeInMillis(System.currentTimeMillis() + DateUtils.DAY_TIME_MS);
            } else if ("后天".equals(timeInfo.day)) {
                calendar.setTimeInMillis(System.currentTimeMillis() + DateUtils.DAY_TIME_MS * 2);
            }
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    @BindingAdapter("dayInfo")
    public static void bindDayInfo(TextView view, TimeInfo timeInfo) {
        if (timeInfo != null) {
            long dayTime = getDayMillis(timeInfo);
            if (dayTime > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dayTime);
                view.setText(DateUtils.getSdf("MM月dd日 周").format(dayTime) + DateUtils.weekNumberToZh(calendar.get(Calendar.DAY_OF_WEEK)));
            } else {
                view.setText(timeInfo.day);
            }
        } else {
            view.setText("");
        }
    }

    @BindingAdapter("hourInfo")
    public static void bindHourInfo(TextView view, TimeSection section) {
        if (section != null) {
            view.setText(section.getHour() + "时");
        } else {
            view.setText("");
        }
    }

    @BindingAdapter("minuteInfo")
    public static void bindMinuteInfo(TextView view, TimeSection section) {
        if (section != null) {
            view.setText(section.getMinute() + "分");
        } else {
            view.setText("");
        }
    }
}
