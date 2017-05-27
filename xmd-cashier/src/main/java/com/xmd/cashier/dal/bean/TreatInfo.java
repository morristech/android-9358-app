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
    public String telephone;    // 手机号
    public String createDate;   // 创建时间

    public TreatInfo() {
    }

    protected TreatInfo(Parcel in) {
        amount = in.readInt();
        authorizeCode = in.readString();
        useMoney = in.readInt();
        telephone = in.readString();
        createDate = in.readString();
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
        dest.writeString(telephone);
        dest.writeString(createDate);
    }
}
