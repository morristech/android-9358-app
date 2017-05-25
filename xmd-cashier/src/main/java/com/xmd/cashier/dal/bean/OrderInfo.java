package com.xmd.cashier.dal.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sdcm on 15-11-23.
 */
public class OrderInfo implements Parcelable {
    // common filed
    public String id;       //订单ID
    public String techName; //技师名称
    public String techNo;
    public String orderNo;  //订单号
    public int downPayment; //订金	单位为分

    // old filed
    // ------------------------begin-----------------------
    public String cardNo;
    public String serviceItemName;
    // -------------------------end------------------------

    // new filed
    // ------------------------begin-----------------------
    public String appointTime;  //预约时间	yyyy-MM-dd HH:mm:ss
    public String createdAt;    //创建时间	yyyy-MM-dd HH:mm:ss
    public String customerName; //用户名
    public String innerProvider;//内网商		非空代表是内网订单
    public boolean isExpire;    //是否可过期
    public String orderType;    //订单类型	paid
    public String phoneNum;     //用户电话
    public String refundStatus; //退款状态	空为未退款;refund-退款中;refunded-已退款
    public String status;       //订单状态	submit-待接受;cancel-取消;complete-完成;accept-已接受;reject-拒绝;failure-失效;overtime-超时
    public String statusName;   //订单状态名称
    public String technicianId; //技师ID
    public String userId;       //用户ID
    // -------------------------end------------------------

    public String serviceItemId;
    public String description;
    public String expire;

    public OrderInfo() {

    }

    protected OrderInfo(Parcel in) {
        id = in.readString();
        techName = in.readString();
        techNo = in.readString();
        orderNo = in.readString();
        downPayment = in.readInt();
        cardNo = in.readString();
        serviceItemName = in.readString();
        appointTime = in.readString();
        createdAt = in.readString();
        customerName = in.readString();
        innerProvider = in.readString();
        isExpire = in.readByte() != 0;
        orderType = in.readString();
        phoneNum = in.readString();
        refundStatus = in.readString();
        status = in.readString();
        statusName = in.readString();
        technicianId = in.readString();
        userId = in.readString();
        serviceItemId = in.readString();
        description = in.readString();
        expire = in.readString();
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel in) {
            return new OrderInfo(in);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        // TODO
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof OrderInfo)) {
            return false;
        }
        OrderInfo that = (OrderInfo) o;
        return id.equals(that.id) && orderNo.equals(that.orderNo);
    }

    @Override
    public int hashCode() {
        // TODO
        return Integer.valueOf(orderNo.substring(orderNo.length() - 8, orderNo.length()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(techName);
        dest.writeString(techNo);
        dest.writeString(orderNo);
        dest.writeInt(downPayment);
        dest.writeString(cardNo);
        dest.writeString(serviceItemName);
        dest.writeString(appointTime);
        dest.writeString(createdAt);
        dest.writeString(customerName);
        dest.writeString(innerProvider);
        dest.writeByte((byte) (isExpire ? 1 : 0));
        dest.writeString(orderType);
        dest.writeString(phoneNum);
        dest.writeString(refundStatus);
        dest.writeString(status);
        dest.writeString(statusName);
        dest.writeString(technicianId);
        dest.writeString(userId);
        dest.writeString(serviceItemId);
        dest.writeString(description);
        dest.writeString(expire);
    }
}
