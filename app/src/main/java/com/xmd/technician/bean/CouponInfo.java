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
    public int sysCommission;
    public float techBaseCommission;
    public float techCommission;
    public String consumeMoneyDescription;
    public String time;
    public String useTimePeriod;
    public String useType;
    public String consumeMoney;
    public String couponTypeName;
    public String shareUrl;
    public int selectedStatus; //1可被选中且未被选中，2，可被选中且已被选中

    @Override
    public String toString() {
        return actTitle;
    }
}
