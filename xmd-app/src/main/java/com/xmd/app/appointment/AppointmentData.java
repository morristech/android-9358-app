package com.xmd.app.appointment;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by heyangya on 17-5-23.
 */

public class AppointmentData implements Serializable {
    private String serviceName; //预约项目
    private Integer servicePrice; //项目价格
    private String time; //到店时间
    private int duration; //持续时间，分钟
    private String customerName;//客户名称
    private String customerPhone;//客户电话
    private Integer fontMoney; //订金

    private String techName;
    private String techAvatar;
    private String techNo;

    private String token; //订单生成者token

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public String getTechAvatar() {
        return techAvatar;
    }

    public void setTechAvatar(String techAvatar) {
        this.techAvatar = techAvatar;
    }

    public String getTechNo() {
        return techNo;
    }

    public void setTechNo(String techNo) {
        this.techNo = techNo;
    }

    @BindingAdapter("techName")
    public static void setTechName(TextView view, AppointmentData data) {
        if (TextUtils.isEmpty(data.getTechName())) {
            view.setText("技师待定");
            return;
        }
        if (TextUtils.isEmpty(data.getTechNo())) {
            view.setText(data.getTechName());
        } else {
            view.setText(data.getTechName() + "[" + data.getTechNo() + "]");
        }
    }

    @BindingAdapter("servicePrice")
    public static void setServicePrice(TextView view, AppointmentData data) {
        if (data.getServicePrice() == null) {
            view.setText("待定");
        } else {
            view.setText(data.getServicePrice() + "元");
        }
    }
}
