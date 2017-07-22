package com.xmd.technician.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 16-4-14.
 */
public class CouponInfo implements Parcelable {

    /**
     * actId : 887162723530448896
     * clubId : 601679316694081536
     * clubName : null
     * actTitle : 二娃热
     * actSubTitle : 
     * actDescription : 
     * actContent : <ul><li>使用时，请出示手机号码或者优惠码。</li><li>每张券仅限一人使用，仅能使用一张。</li><li>使用此券，不可享受本店其他优惠。</li><li>提供免费WiFi。</li><li>提供免费停车位。</li><li>欢迎提前预约。</li></ul>
     * actValue : 550
     * actLogo : 
     * backgroupImage : 
     * actLogoCompress : null
     * actLogoUrl : 
     * actTotal : 2000
     * userGetCount : 5
     * actType : coupon
     * actTypeName : 优惠券
     * startDate : null
     * endDate : null
     * actStatus : online
     * actStatusName : 在线
     * couponSellTotal : 10
     * couponUseTotal : 6
     * couponType : discount
     * consumeMoney : 0
     * modifyDate : 2017-07-18 12:10:50
     * createDate : 2017-07-18 12:10:50
     * orderNo : 0
     * getFlag : null
     * itemId : 
     * serverItem : null
     * time : null
     * useDateNote : 
     * baseCommission : 0
     * commission : 0
     * qrCode : null
     * qrCodeUrl : null
     * redpackUseDetailUrl : null
     * clubLogoUrl : null
     * useStartDate : null
     * useEndDate : null
     * startTime : 0
     * endTime : 23
     * shareUrl : null
     * userGetCounts : null
     * useType : discount
     * isIndex : N
     * periodType : fixed_time
     * longAfterReceive : 0
     * periodDay : 
     * couponTypeName : 折扣券
     * useDay : 
     * platformFee : null
     * incomeType : club
     * operatorId : 601634063966539776
     * operatorName : 刘德华
     * consumeMoneyDescription : 5.5折
     * techCommission : 0
     * actPeriod : 不限
     * couponPeriod : 领取后立即生效，至长期有效！
     * useTimePeriod :  0:00 - 23:00
     * useTypeName : 折扣券
     * backgroupImageUrl : null
     * techBaseCommission : 550
     */

    public String actId;
    public String clubId;
    public String clubName;
    public String actTitle;
    public String actSubTitle;
    public String actDescription;
    public String actContent;
    public int actValue;
    public String actLogo;
    public String backgroupImage;
    public String actLogoCompress;
    public String actLogoUrl;
    public int actTotal;
    public int userGetCount;
    public String actType;
    public String actTypeName;
    public String startDate;
    public String endDate;
    public String actStatus;
    public String actStatusName;
    public int couponSellTotal;
    public int couponUseTotal;
    public String couponType;
    public String consumeMoney;
    public String modifyDate;
    public String createDate;
    public int orderNo;
    public String getFlag;
    public String itemId;
    public String serverItem;
    public String time;
    public String useDateNote;
    public int baseCommission;
    public int commission;
    public String qrCode;
    public String qrCodeUrl;
    public String redpackUseDetailUrl;
    public String clubLogoUrl;
    public String useStartDate;
    public String useEndDate;
    public String startTime;
    public String endTime;
    public String shareUrl;
    public String userGetCounts;
    public String useType;
    public String isIndex;
    public String periodType;
    public String longAfterReceive;
    public String periodDay;
    public String couponTypeName;
    public String useDay;
    public String platformFee;
    public String incomeType;
    public String operatorId;
    public String operatorName;
    public String consumeMoneyDescription;
    public int techCommission;
    public String actPeriod;
    public String couponPeriod;
    public String useTimePeriod;
    public String useTypeName;
    public String backgroupImageUrl;
    public int techBaseCommission;
    public int selectedStatus; //1可被选中且未被选中，2，可被选中且已被选中

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.actId);
        dest.writeString(this.clubId);
        dest.writeString(this.clubName);
        dest.writeString(this.actTitle);
        dest.writeString(this.actSubTitle);
        dest.writeString(this.actDescription);
        dest.writeString(this.actContent);
        dest.writeInt(this.actValue);
        dest.writeString(this.actLogo);
        dest.writeString(this.backgroupImage);
        dest.writeString(this.actLogoCompress);
        dest.writeString(this.actLogoUrl);
        dest.writeInt(this.actTotal);
        dest.writeInt(this.userGetCount);
        dest.writeString(this.actType);
        dest.writeString(this.actTypeName);
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeString(this.actStatus);
        dest.writeString(this.actStatusName);
        dest.writeInt(this.couponSellTotal);
        dest.writeInt(this.couponUseTotal);
        dest.writeString(this.couponType);
        dest.writeString(this.consumeMoney);
        dest.writeString(this.modifyDate);
        dest.writeString(this.createDate);
        dest.writeInt(this.orderNo);
        dest.writeString(this.getFlag);
        dest.writeString(this.itemId);
        dest.writeString(this.serverItem);
        dest.writeString(this.time);
        dest.writeString(this.useDateNote);
        dest.writeInt(this.baseCommission);
        dest.writeInt(this.commission);
        dest.writeString(this.qrCode);
        dest.writeString(this.qrCodeUrl);
        dest.writeString(this.redpackUseDetailUrl);
        dest.writeString(this.clubLogoUrl);
        dest.writeString(this.useStartDate);
        dest.writeString(this.useEndDate);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.shareUrl);
        dest.writeString(this.userGetCounts);
        dest.writeString(this.useType);
        dest.writeString(this.isIndex);
        dest.writeString(this.periodType);
        dest.writeString(this.longAfterReceive);
        dest.writeString(this.periodDay);
        dest.writeString(this.couponTypeName);
        dest.writeString(this.useDay);
        dest.writeString(this.platformFee);
        dest.writeString(this.incomeType);
        dest.writeString(this.operatorId);
        dest.writeString(this.operatorName);
        dest.writeString(this.consumeMoneyDescription);
        dest.writeInt(this.techCommission);
        dest.writeString(this.actPeriod);
        dest.writeString(this.couponPeriod);
        dest.writeString(this.useTimePeriod);
        dest.writeString(this.useTypeName);
        dest.writeString(this.backgroupImageUrl);
        dest.writeInt(this.techBaseCommission);
        dest.writeInt(this.selectedStatus);
    }

    public CouponInfo() {
    }

    protected CouponInfo(Parcel in) {
        this.actId = in.readString();
        this.clubId = in.readString();
        this.clubName = in.readString();
        this.actTitle = in.readString();
        this.actSubTitle = in.readString();
        this.actDescription = in.readString();
        this.actContent = in.readString();
        this.actValue = in.readInt();
        this.actLogo = in.readString();
        this.backgroupImage = in.readString();
        this.actLogoCompress = in.readString();
        this.actLogoUrl = in.readString();
        this.actTotal = in.readInt();
        this.userGetCount = in.readInt();
        this.actType = in.readString();
        this.actTypeName = in.readString();
        this.startDate = in.readString();
        this.endDate = in.readString();
        this.actStatus = in.readString();
        this.actStatusName = in.readString();
        this.couponSellTotal = in.readInt();
        this.couponUseTotal = in.readInt();
        this.couponType = in.readString();
        this.consumeMoney = in.readString();
        this.modifyDate = in.readString();
        this.createDate = in.readString();
        this.orderNo = in.readInt();
        this.getFlag = in.readString();
        this.itemId = in.readString();
        this.serverItem = in.readString();
        this.time = in.readString();
        this.useDateNote = in.readString();
        this.baseCommission = in.readInt();
        this.commission = in.readInt();
        this.qrCode = in.readString();
        this.qrCodeUrl = in.readString();
        this.redpackUseDetailUrl = in.readString();
        this.clubLogoUrl = in.readString();
        this.useStartDate = in.readString();
        this.useEndDate = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.shareUrl = in.readString();
        this.userGetCounts = in.readString();
        this.useType = in.readString();
        this.isIndex = in.readString();
        this.periodType = in.readString();
        this.longAfterReceive = in.readString();
        this.periodDay = in.readString();
        this.couponTypeName = in.readString();
        this.useDay = in.readString();
        this.platformFee = in.readString();
        this.incomeType = in.readString();
        this.operatorId = in.readString();
        this.operatorName = in.readString();
        this.consumeMoneyDescription = in.readString();
        this.techCommission = in.readInt();
        this.actPeriod = in.readString();
        this.couponPeriod = in.readString();
        this.useTimePeriod = in.readString();
        this.useTypeName = in.readString();
        this.backgroupImageUrl = in.readString();
        this.techBaseCommission = in.readInt();
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
