package com.xmd.cashier.dal.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by heyangya on 16-9-7.
 */

/**
 * 请客授权信息
 */
public class TreatInfo implements Parcelable {
    public int amount;//授权金额,以分为单位
    public String authorizeCode;//授权码

    public int useMoney; //实际使用金额，需要计算

    public String userName;
    public String userPhone;

    public String memberName;
    public String memberCardNo;
    public String memberPhone;
    public int memberDiscount;
    public String memberTypeName;

    public TreatInfo() {
    }

    public void setExtraMemberInfo(CommonVerifyInfo.ExtraMemberInfo info) {
        this.memberName = info.memberName;
        this.memberPhone = info.memberPhone;
        this.memberCardNo = info.cardNo;
        this.memberDiscount = info.discount;
        this.memberTypeName = info.memberTypeName;
    }

    protected TreatInfo(Parcel in) {
        amount = in.readInt();
        authorizeCode = in.readString();
        useMoney = in.readInt();
        userName = in.readString();
        userPhone = in.readString();

        memberName = in.readString();
        memberCardNo = in.readString();
        memberPhone = in.readString();
        memberDiscount = in.readInt();
        memberTypeName = in.readString();
    }

    public static final Creator<TreatInfo> CREATOR = new Creator<TreatInfo>() {
        @Override
        public TreatInfo createFromParcel(Parcel in) {
            return new TreatInfo(in);
        }

        @Override
        public TreatInfo[] newArray(int size) {
            return new TreatInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(amount);
        dest.writeString(authorizeCode);
        dest.writeInt(useMoney);
        dest.writeString(userName);
        dest.writeString(userPhone);

        dest.writeString(memberName);
        dest.writeString(memberPhone);
        dest.writeString(memberCardNo);
        dest.writeInt(memberDiscount);
        dest.writeString(memberTypeName);
    }
}
