package com.xmd.cashier.dal.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by heyangya on 16-9-8.
 */

public class VerificationItem implements Parcelable {
    //是否已选择
    public boolean selected;
    //核销结果
    public boolean success;
    public int errorCode;
    public String errorMsg;

    public String code;
    //券信息
    public String type;
    //以下信息根据type类型设置
    public CouponInfo couponInfo;//优惠券
    public OrderInfo order; //订单
    public TreatInfo treatInfo;//请客

    public VerificationItem() {
    }

    protected VerificationItem(Parcel in) {
        selected = in.readByte() != 0;
        success = in.readByte() != 0;
        errorCode = in.readInt();
        errorMsg = in.readString();
        code = in.readString();
        type = in.readString();
        couponInfo = in.readParcelable(CouponInfo.class.getClassLoader());
        order = in.readParcelable(OrderInfo.class.getClassLoader());
        treatInfo = in.readParcelable(TreatInfo.class.getClassLoader());
    }

    public static final Creator<VerificationItem> CREATOR = new Creator<VerificationItem>() {
        @Override
        public VerificationItem createFromParcel(Parcel in) {
            return new VerificationItem(in);
        }

        @Override
        public VerificationItem[] newArray(int size) {
            return new VerificationItem[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof VerificationItem)) {
            return false;
        }
        VerificationItem that = (VerificationItem) o;
        return type.equals(that.type)
                && ((couponInfo == null && that.couponInfo == null) || (couponInfo != null && couponInfo.equals(that.couponInfo)))
                && ((order == null && that.order == null) || (order != null && order.equals(that.order)));
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (byte b : type.getBytes()) {
            hashCode += b;
        }
        if (couponInfo != null) {
            hashCode += couponInfo.hashCode();
        }
        if (order != null) {
            hashCode += order.hashCode();
        }
        return hashCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeInt(errorCode);
        dest.writeString(errorMsg);
        dest.writeString(code);
        dest.writeString(type);
        dest.writeParcelable(couponInfo, flags);
        dest.writeParcelable(order, flags);
        dest.writeParcelable(treatInfo, flags);
    }
}
