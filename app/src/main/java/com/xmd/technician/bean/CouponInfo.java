package com.xmd.technician.bean;

/**
 * Created by sdcm on 16-4-14.
 */
public class CouponInfo {
    public String actId;
    public String actTitle;
    public String actContent;
    public String couponPeriod;
    public String couponType;
    public String useTypeName;
    public int actValue;
    public int baseCommission;
    public int commission;
    public String consumeMoneyDescription;
    public String time;
    public String useType;

    @Override
    public String toString() {
        return actTitle;
    }
}
