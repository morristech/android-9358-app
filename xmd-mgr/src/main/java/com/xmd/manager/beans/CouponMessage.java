package com.xmd.manager.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28.
 */
public class CouponMessage implements Serializable {
    public String couponName;
    public String actId;

    public CouponMessage(String couponName, String actId) {
        this.couponName = couponName;
        this.actId = actId;

    }
}
