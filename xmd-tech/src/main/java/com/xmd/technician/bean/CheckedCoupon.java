package com.xmd.technician.bean;

import android.os.Parcelable;

/**
 * Created by Lhj on 2016/8/4.
 */
public class CheckedCoupon extends CouponInfo implements Parcelable {
    public int position;

    public CheckedCoupon(String userTypeName, int actValue, String couponPeriod, String actId, String couponType, int position) {
        this.useTypeName = userTypeName;
        this.actValue = actValue;
        this.couponPeriod = couponPeriod;
        this.actId = actId;
        this.couponType = couponType;
        this.position = position;
    }
}
