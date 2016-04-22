package com.xmd.technician.model;

/**
 * Created by sdcm on 16-4-14.
 */
public class CouponInfo {
    public String actId;
    public String actTitle;
    public String couponPeriod;
    public String couponType;
    public int actValue;

    @Override
    public String toString() {
        return actTitle;
    }
}
