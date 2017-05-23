package com.xmd.manager.beans;

import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by Administrator on 2016/12/8.
 */

public class PayOrderDetailBean implements Parcelable, Serializable {
    /**
     * customerName : 心累
     * orderType : paid
     * status : accept
     * statusName : 已接受
     * downPayment : 100
     * technicianId : 748081899301244928
     * innerProvider : null
     * id : 806793547595382784
     * appointTime : 2016-12-08 18:30:00
     * phoneNum : 13137558109
     * orderNo : 116650575984
     * refundStatus : null
     * createdAt : 2016-12-08 17:32:25
     * userId : 743718740591382528
     * techName : 7777
     */

    public String customerName;
    public String orderType;
    public String status;
    public String statusName;
    public int downPayment;
    public String technicianId;
    public Object innerProvider;
    public String id;
    public String appointTime;
    public String phoneNum;
    public String orderNo;
    public Object refundStatus;
    public String createdAt;
    public String userId;
    public String techName;
    public String techNo;
    public boolean isExpire;
    public String serviceItemName;

    protected PayOrderDetailBean(Parcel in) {
        customerName = in.readString();
        orderType = in.readString();
        status = in.readString();
        statusName = in.readString();
        downPayment = in.readInt();
        technicianId = in.readString();
        id = in.readString();
        appointTime = in.readString();
        phoneNum = in.readString();
        orderNo = in.readString();
        createdAt = in.readString();
        userId = in.readString();
        techName = in.readString();
        techNo = in.readString();
        isExpire = in.readByte() != 0;
        serviceItemName = in.readString();
    }

    public static final Creator<PayOrderDetailBean> CREATOR = new Creator<PayOrderDetailBean>() {
        @Override
        public PayOrderDetailBean createFromParcel(Parcel in) {
            return new PayOrderDetailBean(in);
        }

        @Override
        public PayOrderDetailBean[] newArray(int size) {
            return new PayOrderDetailBean[size];
        }
    };

    @BindingAdapter("front_money")
    public static void bindFrontMoney(TextView view, int money) {
        view.setText(String.format(Locale.getDefault(), "预约定金%.2f元", money / 100.0));
    }

    @BindingAdapter("tech_info")
    public static void bindTechInfo(TextView view, PayOrderDetailBean data) {
        view.setText(data.techName + (TextUtils.isEmpty(data.techNo) ? "" : "[" + data.techNo + "]"));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerName);
        dest.writeString(orderType);
        dest.writeString(status);
        dest.writeString(statusName);
        dest.writeInt(downPayment);
        dest.writeString(technicianId);
        dest.writeString(id);
        dest.writeString(appointTime);
        dest.writeString(phoneNum);
        dest.writeString(orderNo);
        dest.writeString(createdAt);
        dest.writeString(userId);
        dest.writeString(techName);
        dest.writeString(techNo);
        dest.writeByte((byte) (isExpire ? 1 : 0));
        dest.writeString(serviceItemName);
    }
}
