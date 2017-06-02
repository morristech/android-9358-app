package com.xmd.appointment;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.TextView;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.appointment.beans.AppointmentSetting;
import com.xmd.appointment.beans.ServiceItem;
import com.xmd.appointment.beans.Technician;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by heyangya on 17-5-23.
 * 预约数据
 */

public class AppointmentData implements Serializable {
    private Date time; //到店时间
    private int duration; //持续时间，分钟
    private String customerName;//客户名称
    private String customerPhone;//客户电话
    private Integer fontMoney; //订金

    private Technician technician; //技师信息
    private ServiceItem serviceItem; //服务信息
    private AppointmentSetting appointmentSetting; //额外信息

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
        if (data.getServiceItem() == null || data.getServiceItem().getPrice() == null) {
            view.setText("待定");
        } else {
            view.setText(data.getServiceItem().getPrice() + "元");
        }
    }

    public ServiceItem getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(ServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
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

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
    }

    public AppointmentSetting getAppointmentSetting() {
        return appointmentSetting;
    }

    public void setAppointmentSetting(AppointmentSetting appointmentSetting) {
        this.appointmentSetting = appointmentSetting;
    }

    @BindingAdapter("appointmentTime")
    public static void bindAppointmentTime(TextView view, Date date) {
        if (date != null) {
            view.setText(DateUtils.getSdf("yyyy-MM-dd HH:mm").format(date));
        } else {
            view.setText("--:--");
        }
    }
}
