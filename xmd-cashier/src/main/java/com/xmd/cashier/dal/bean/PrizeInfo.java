package com.xmd.cashier.dal.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zr on 16-12-9.
 * 奖品
 */

public class PrizeInfo implements Parcelable {
    // 大转盘活动ID
    public int activityId;
    // 大转盘活动名称
    public String activityName;
    // 活动说明
    public String description;
    // 过期时间
    public String expireTime;

    // 奖品ID
    public int id;
    // 奖品名称
    public String prizeName;
    // 奖品类型:1=实物商品
    public int prizeType;

    // 用户手机号
    public String telephone;
    // 用户名称
    public String userName;
    // 奖品兑换码
    public String verifyCode;

    protected PrizeInfo(Parcel in) {
        activityId = in.readInt();
        activityName = in.readString();
        description = in.readString();
        expireTime = in.readString();
        id = in.readInt();
        prizeName = in.readString();
        prizeType = in.readInt();
        telephone = in.readString();
        userName = in.readString();
        verifyCode = in.readString();
    }

    public static final Creator<PrizeInfo> CREATOR = new Creator<PrizeInfo>() {
        @Override
        public PrizeInfo createFromParcel(Parcel in) {
            return new PrizeInfo(in);
        }

        @Override
        public PrizeInfo[] newArray(int size) {
            return new PrizeInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(activityId);
        dest.writeString(activityName);
        dest.writeString(description);
        dest.writeString(expireTime);
        dest.writeInt(id);
        dest.writeString(prizeName);
        dest.writeInt(prizeType);
        dest.writeString(telephone);
        dest.writeString(userName);
        dest.writeString(verifyCode);
    }
}
