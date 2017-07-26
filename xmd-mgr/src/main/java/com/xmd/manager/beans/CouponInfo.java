package com.xmd.manager.beans;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sdcm on 15-10-27.
 */
public class CouponInfo implements Parcelable, Cloneable {

    public boolean isDelivery = false;

    /**
     * Only Used by User Coupon
     */
    public String userId;

    /**
     * Only Used by User Coupon
     */
    public String suaId;

    public String couponNo;
    public String actId;
    public String actTitle;
    public String actSubTitle;
    public String couponType;
    public String actStatus;
    public String actStatusName;
    public String clubId;
    public String actContent;
    public int actValue;
    public String actType;
    //yyyy-MM-dd
    public String startDate;
    //yyyy-MM-dd
    public String endDate;

    public String useStartDate;
    public String useEndDate;

    //yyyy-MM-dd HH:mm:ss
    public String modifyDate;
    public String itemId;
    //yyyy-MM-dd~yyyy-MM-dd HH:mm:ss
    public String startTime;
    public String endTime;
    //HH:mm:ss
    public String useDateNote;
    public String useType;
    public String actDescription;

    public String commission;

    public String actPeriod;
    public String couponPeriod;
    public String useTimePeriod;
    public String consumeMoneyDescription;
    public String useTypeName;
    public String couponTypeName;
    public int isSelected; //1,可被选中且未被选中，2，可被选中且已被选中，3，不可被选中

    public CouponInfo(String actTitle, String actId) {
        this.actTitle = actTitle;
        this.actId = actId;

    }


    public CouponInfo(String actTitle, String useTypeName, String consumeMoneyDescription,
                      String actPeriod, String couponPeriod, String useTimePeriod, String actStatusName) {
        this.actTitle = actTitle;
        this.useTypeName = useTypeName;
        this.consumeMoneyDescription = consumeMoneyDescription;
        this.actPeriod = actPeriod;
        this.couponPeriod = couponPeriod;
        this.useTimePeriod = useTimePeriod;
        this.actStatusName = actStatusName;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        CouponInfo couponInfo = null;
        try {
            couponInfo = (CouponInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return couponInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isDelivery ? (byte) 1 : (byte) 0);
        dest.writeString(this.userId);
        dest.writeString(this.suaId);
        dest.writeString(this.couponNo);
        dest.writeString(this.actId);
        dest.writeString(this.actTitle);
        dest.writeString(this.couponType);
        dest.writeString(this.actStatus);
        dest.writeString(this.actStatusName);
        dest.writeString(this.clubId);
        dest.writeString(this.actContent);
        dest.writeInt(this.actValue);
        dest.writeString(this.actType);
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeString(this.useStartDate);
        dest.writeString(this.useEndDate);
        dest.writeString(this.modifyDate);
        dest.writeString(this.itemId);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.useDateNote);
        dest.writeString(this.useType);
        dest.writeString(this.actDescription);
        dest.writeString(this.commission);
        dest.writeString(this.actPeriod);
        dest.writeString(this.couponPeriod);
        dest.writeString(this.useTimePeriod);
        dest.writeString(this.consumeMoneyDescription);
        dest.writeString(this.useTypeName);
        dest.writeString(this.couponTypeName);
        dest.writeInt(this.isSelected);
    }

    protected CouponInfo(Parcel in) {
        this.isDelivery = in.readByte() != 0;
        this.userId = in.readString();
        this.suaId = in.readString();
        this.couponNo = in.readString();
        this.actId = in.readString();
        this.actTitle = in.readString();
        this.couponType = in.readString();
        this.actStatus = in.readString();
        this.actStatusName = in.readString();
        this.clubId = in.readString();
        this.actContent = in.readString();
        this.actValue = in.readInt();
        this.actType = in.readString();
        this.startDate = in.readString();
        this.endDate = in.readString();
        this.useStartDate = in.readString();
        this.useEndDate = in.readString();
        this.modifyDate = in.readString();
        this.itemId = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.useDateNote = in.readString();
        this.useType = in.readString();
        this.actDescription = in.readString();
        this.commission = in.readString();
        this.actPeriod = in.readString();
        this.couponPeriod = in.readString();
        this.useTimePeriod = in.readString();
        this.consumeMoneyDescription = in.readString();
        this.useTypeName = in.readString();
        this.couponTypeName = in.readString();
        this.isSelected = in.readInt();
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
