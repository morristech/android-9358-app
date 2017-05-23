package com.xmd.manager.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by linms@xiaomodo.com on 16-5-30.
 */
public class CouponSelectResult implements Parcelable {
    public List<CouponInfo> mCouponList;

    public CouponSelectResult(List<CouponInfo> mCouponList) {
        this.mCouponList = mCouponList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mCouponList);
    }

    protected CouponSelectResult(Parcel in) {
        this.mCouponList = in.createTypedArrayList(CouponInfo.CREATOR);
    }

    public static final Creator<CouponSelectResult> CREATOR = new Creator<CouponSelectResult>() {
        @Override
        public CouponSelectResult createFromParcel(Parcel source) {
            return new CouponSelectResult(source);
        }

        @Override
        public CouponSelectResult[] newArray(int size) {
            return new CouponSelectResult[size];
        }
    };
}
