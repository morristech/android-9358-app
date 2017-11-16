package com.xmd.manager.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 17-11-8.
 */

public class CouponRecordBean implements Parcelable {

    /**
     * title : 手机礼品券
     * subTitle : 手机礼品
     * businessNo : AG170906001
     * couponType : gift
     * actStatus : online
     * canUseSum : 1
     * useStartTime : 2017-09-07 20:54
     * amount : 0
     * oriAmount : 0
     * couponNo : 227264206428
     * userId : 845113907323342848
     * userHeadImage : 160856
     * userName : 1
     * userPhoneNum : 13265684444
     * getTime : 2017-09-07 20:54:34
     * useEndTime : null
     * techName : sdadm
     * techNo : 7ryhxn
     * verifyTime : null
     * verifyOperator : null
     * status : canUse
     * consumeMoneyDescription : 手机礼品
     * couponTypeName : 礼品券
     * statusName : 可用
     */

    public String title;
    public String subTitle;
    public String businessNo;
    public String couponType;
    public String actStatus;
    public int canUseSum;
    public String useStartTime;
    public int amount;
    public int oriAmount;
    public String couponNo;
    public String userId;
    public String userHeadImage;
    public String userName;
    public String userPhoneNum;
    public String getTime;
    public String useEndTime;
    public String techName;
    public String techNo;
    public String verifyTime;
    public String verifyOperator;
    public String status;
    public String consumeMoneyDescription;
    public String couponTypeName;
    public String statusName;
    public boolean isSearch;
    public int isSelected;//0:未被选中，1：被选中
    public String isUsable; //"Y":可用，"N":已下线

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.subTitle);
        dest.writeString(this.businessNo);
        dest.writeString(this.couponType);
        dest.writeString(this.actStatus);
        dest.writeInt(this.canUseSum);
        dest.writeString(this.useStartTime);
        dest.writeInt(this.amount);
        dest.writeInt(this.oriAmount);
        dest.writeString(this.couponNo);
        dest.writeString(this.userId);
        dest.writeString(this.userHeadImage);
        dest.writeString(this.userName);
        dest.writeString(this.userPhoneNum);
        dest.writeString(this.getTime);
        dest.writeString(this.useEndTime);
        dest.writeString(this.techName);
        dest.writeString(this.techNo);
        dest.writeString(this.verifyTime);
        dest.writeString(this.verifyOperator);
        dest.writeString(this.status);
        dest.writeString(this.consumeMoneyDescription);
        dest.writeString(this.couponTypeName);
        dest.writeString(this.statusName);
        dest.writeByte(this.isSearch ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isSelected);
        dest.writeString(this.isUsable);
    }

    protected CouponRecordBean(Parcel in) {
        this.title = in.readString();
        this.subTitle = in.readString();
        this.businessNo = in.readString();
        this.couponType = in.readString();
        this.actStatus = in.readString();
        this.canUseSum = in.readInt();
        this.useStartTime = in.readString();
        this.amount = in.readInt();
        this.oriAmount = in.readInt();
        this.couponNo = in.readString();
        this.userId = in.readString();
        this.userHeadImage = in.readString();
        this.userName = in.readString();
        this.userPhoneNum = in.readString();
        this.getTime = in.readString();
        this.useEndTime = in.readString();
        this.techName = in.readString();
        this.techNo = in.readString();
        this.verifyTime = in.readString();
        this.verifyOperator = in.readString();
        this.status = in.readString();
        this.consumeMoneyDescription = in.readString();
        this.couponTypeName = in.readString();
        this.statusName = in.readString();
        this.isSearch = in.readByte() != 0;
        this.isSelected = in.readInt();
        this.isUsable = in.readString();
    }

    public static final Creator<CouponRecordBean> CREATOR = new Creator<CouponRecordBean>() {
        @Override
        public CouponRecordBean createFromParcel(Parcel source) {
            return new CouponRecordBean(source);
        }

        @Override
        public CouponRecordBean[] newArray(int size) {
            return new CouponRecordBean[size];
        }
    };
}
