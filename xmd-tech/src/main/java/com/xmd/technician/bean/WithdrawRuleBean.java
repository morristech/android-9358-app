package com.xmd.technician.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 18-1-10.
 */

public class WithdrawRuleBean implements Parcelable{

    /**
     * dayLimitAmount : 1000
     * noServiceChargeAmount : 30000
     * serviceChargeRate : 0.006
     */

    public int dayLimitAmount;
    public int noServiceChargeAmount;
    public double serviceChargeRate;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.dayLimitAmount);
        dest.writeInt(this.noServiceChargeAmount);
        dest.writeDouble(this.serviceChargeRate);
    }

    public WithdrawRuleBean() {
    }

    protected WithdrawRuleBean(Parcel in) {
        this.dayLimitAmount = in.readInt();
        this.noServiceChargeAmount = in.readInt();
        this.serviceChargeRate = in.readDouble();
    }

    public static final Creator<WithdrawRuleBean> CREATOR = new Creator<WithdrawRuleBean>() {
        @Override
        public WithdrawRuleBean createFromParcel(Parcel source) {
            return new WithdrawRuleBean(source);
        }

        @Override
        public WithdrawRuleBean[] newArray(int size) {
            return new WithdrawRuleBean[size];
        }
    };
}
