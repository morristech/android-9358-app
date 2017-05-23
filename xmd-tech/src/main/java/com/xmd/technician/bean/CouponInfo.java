package com.xmd.technician.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 16-4-14.
 */
public class CouponInfo implements Parcelable{
    public String actId;
    public String actTitle;
    public String actContent;
    public String couponPeriod;
    public String couponType;
    public String useTypeName;
    public int actValue;
    public int baseCommission;
    public int commission;
    public int sysCommission;
    public float techBaseCommission;
    public float techCommission;
    public String consumeMoneyDescription;
    public String time;
    public String useTimePeriod;
    public String useType;
    public String consumeMoney;
    public String couponTypeName;
    public String shareUrl;
    public int selectedStatus; //1可被选中且未被选中，2，可被选中且已被选中

    @Override
    public String toString() {
        return actTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.actId);
        dest.writeString(this.actTitle);
        dest.writeString(this.actContent);
        dest.writeString(this.couponPeriod);
        dest.writeString(this.couponType);
        dest.writeString(this.useTypeName);
        dest.writeInt(this.actValue);
        dest.writeInt(this.baseCommission);
        dest.writeInt(this.commission);
        dest.writeInt(this.sysCommission);
        dest.writeFloat(this.techBaseCommission);
        dest.writeFloat(this.techCommission);
        dest.writeString(this.consumeMoneyDescription);
        dest.writeString(this.time);
        dest.writeString(this.useTimePeriod);
        dest.writeString(this.useType);
        dest.writeString(this.consumeMoney);
        dest.writeString(this.couponTypeName);
        dest.writeString(this.shareUrl);
        dest.writeInt(this.selectedStatus);
    }

    public CouponInfo() {
    }

    protected CouponInfo(Parcel in) {
        this.actId = in.readString();
        this.actTitle = in.readString();
        this.actContent = in.readString();
        this.couponPeriod = in.readString();
        this.couponType = in.readString();
        this.useTypeName = in.readString();
        this.actValue = in.readInt();
        this.baseCommission = in.readInt();
        this.commission = in.readInt();
        this.sysCommission = in.readInt();
        this.techBaseCommission = in.readFloat();
        this.techCommission = in.readFloat();
        this.consumeMoneyDescription = in.readString();
        this.time = in.readString();
        this.useTimePeriod = in.readString();
        this.useType = in.readString();
        this.consumeMoney = in.readString();
        this.couponTypeName = in.readString();
        this.shareUrl = in.readString();
        this.selectedStatus = in.readInt();
    }

    public static final Creator<CouponInfo> CREATOR = new Creator<CouponInfo>() {
        @Override
        public CouponInfo createFromParcel(Parcel source) {
            return new CouponInfo(source);
        }

        @Override
        public CouponInfo[] newArray(int size) {
            return new CouponInfo[size];
        }
    };
}
