package com.xmd.appointment.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heyangya on 17-5-31.
 * 预约额外数据
 */

public class AppointmentExt implements Serializable {
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


    public static class TimeInfo {
        private String day; //日期标题名称
        private List<TimeSection> time;//时间列表
    }

    private static class TimeSection {
        private Integer day; //	第几天	从0开始， 当天为0， 第二天为1..., 依此类推
        private String status; //	时间状态
        private Integer time; //	时间
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

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getTimeStr() {
            return timeStr;
        }

        public void setTimeStr(String timeStr) {
            this.timeStr = timeStr;
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
        return "AppointmentExt{" +
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
}
