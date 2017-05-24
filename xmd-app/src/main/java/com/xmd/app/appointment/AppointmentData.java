package com.xmd.app.appointment;

import java.io.Serializable;

/**
 * Created by heyangya on 17-5-23.
 */

public class AppointmentData implements Serializable {
    private String serviceItem; //预约项目
    private Integer servicePrice; //项目价格
    private String time; //到店时间
    private int duration; //持续时间，分钟
    private String customerName;//客户名称
    private String customerPhone;//客户电话
    private Integer fontMoney; //订金

    private String token; //订单生成者token

    public String getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(String serviceItem) {
        this.serviceItem = serviceItem;
    }

    public Integer getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(Integer servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Integer getFontMoney() {
        return fontMoney;
    }

    public void setFontMoney(Integer fontMoney) {
        this.fontMoney = fontMoney;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AppointmentData{" +
                "serviceItem='" + serviceItem + '\'' +
                ", servicePrice=" + servicePrice +
                ", time='" + time + '\'' +
                ", duration=" + duration +
                ", customerName='" + customerName + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", fontMoney=" + fontMoney +
                ", token='" + token + '\'' +
                '}';
    }
}
