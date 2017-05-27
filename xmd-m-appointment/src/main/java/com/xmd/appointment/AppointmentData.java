package com.xmd.appointment;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.TextView;

import com.xmd.appointment.beans.Technician;

import java.io.Serializable;

/**
 * Created by heyangya on 17-5-23.
 * 预约数据
 */

public class AppointmentData implements Serializable {
    private String serviceName; //预约项目
    private Integer servicePrice; //项目价格
    private String time; //到店时间
    private int duration; //持续时间，分钟
    private String customerName;//客户名称
    private String customerPhone;//客户电话
    private Integer fontMoney; //订金

    private Technician technician;

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

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
    }

    @BindingAdapter("techName")
    public static void setTechName(TextView view, AppointmentData data) {
        if (data.getTechnician() == null) {
            view.setText("技师待定");
            return;
        }
        if (TextUtils.isEmpty(data.getTechnician().getSerialNo())) {
            view.setText(data.getTechnician().getName());
        } else {
            view.setText(data.getTechnician().getName() + "[" + data.getTechnician().getSerialNo() + "]");
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
