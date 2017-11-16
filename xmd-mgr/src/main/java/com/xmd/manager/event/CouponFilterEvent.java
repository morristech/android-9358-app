package com.xmd.manager.event;

/**
 * Created by Lhj on 17-11-13.
 */

public class CouponFilterEvent {

    public String filterType;

    public CouponFilterEvent(String filterType) {
        this.filterType = filterType;
    }
}
