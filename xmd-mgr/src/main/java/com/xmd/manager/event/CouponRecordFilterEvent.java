package com.xmd.manager.event;

/**
 * Created by Lhj on 17-11-15.
 */

public class CouponRecordFilterEvent {
    public String filterStartTime;
    public String filterEndTime;
    public String couponId;
    public String couponTitle;
    public String couponStatus;
    public String timeFilter;

    public CouponRecordFilterEvent(String filterStartTime,String filterEndTime,String couponId,String couponTitle,String couponStatus,String timeFilter){
        this.filterStartTime = filterStartTime;
        this.filterEndTime = filterEndTime;
        this.couponId = couponId;
        this.couponTitle = couponTitle;
        this.couponStatus = couponStatus;
        this.timeFilter = timeFilter;
    }
}
