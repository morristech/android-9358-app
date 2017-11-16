package com.xmd.manager.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 17-11-9.
 */

public class CouponBean implements Parcelable {
    /**
     * actId : 833975150268391424
     * clubId : 601679316694081536
     * actTitle : 3元点钟券
     * actSubTitle : null
     * couponType : paid
     * amount : 3
     * oriAmount : 3
     * status : 1
     * subStatus : 2
     * startTime : null
     * endTime : null
     * useEndTime : null
     * beforePeriod : 0
     * period : null
     * shareUrl : http://t.cn/RjbnZoN
     * businessNo : AP160324424
     * commissionAmount : 400
     * consumeMoneyDescription : 3元抵3元
     * couponTypeName : 点钟券
     * statusName : 进行中
     * couponPeriod : 客人购买后立即生效，长期有效！
     */

    public String actId;
    public String clubId;
    public String actTitle;
    public String actSubTitle;
    public String couponType;
    public String amount;
    public int oriAmount;
    public int status;
    public int subStatus;
    public String startTime;
    public String endTime;
    public String useEndTime;
    public String beforePeriod;
    public String period;
    public String shareUrl;
    public String businessNo;
    public int commissionAmount;
    public String consumeMoneyDescription;
    public String couponTypeName;
    public String statusName;
    public String couponPeriod;
    public String online; //'Y'表示在线，‘N'表示不在线
    public int isSelected;//0:未被选中，1：被选中
    public String isUsable; //"Y":可用，"N":已下线

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.actId);
        dest.writeString(this.clubId);
        dest.writeString(this.actTitle);
        dest.writeString(this.actSubTitle);
        dest.writeString(this.couponType);
        dest.writeString(this.amount);
        dest.writeInt(this.oriAmount);
        dest.writeInt(this.status);
        dest.writeInt(this.subStatus);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.useEndTime);
        dest.writeString(this.beforePeriod);
        dest.writeString(this.period);
        dest.writeString(this.shareUrl);
        dest.writeString(this.businessNo);
        dest.writeInt(this.commissionAmount);
        dest.writeString(this.consumeMoneyDescription);
        dest.writeString(this.couponTypeName);
        dest.writeString(this.statusName);
        dest.writeString(this.couponPeriod);
        dest.writeString(this.online);
        dest.writeInt(this.isSelected);
        dest.writeString(this.isUsable);
    }

    public CouponBean() {
    }

    protected CouponBean(Parcel in) {
        this.actId = in.readString();
        this.clubId = in.readString();
        this.actTitle = in.readString();
        this.actSubTitle = in.readString();
        this.couponType = in.readString();
        this.amount = in.readString();
        this.oriAmount = in.readInt();
        this.status = in.readInt();
        this.subStatus = in.readInt();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.useEndTime = in.readString();
        this.beforePeriod = in.readString();
        this.period = in.readString();
        this.shareUrl = in.readString();
        this.businessNo = in.readString();
        this.commissionAmount = in.readInt();
        this.consumeMoneyDescription = in.readString();
        this.couponTypeName = in.readString();
        this.statusName = in.readString();
        this.couponPeriod = in.readString();
        this.online = in.readString();
        this.isSelected = in.readInt();
        this.isUsable = in.readString();
    }

    public static final Creator<CouponBean> CREATOR = new Creator<CouponBean>() {
        @Override
        public CouponBean createFromParcel(Parcel source) {
            return new CouponBean(source);
        }

        @Override
        public CouponBean[] newArray(int size) {
            return new CouponBean[size];
        }
    };
}
