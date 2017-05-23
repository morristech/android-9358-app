package com.xmd.manager.service.response;

import java.io.Serializable;

/**
 * Created by lhj on 2016/9/14.
 */
public class MainPageData implements Serializable {

    public String id;
    public String userCount;
    public String totalWifiCount;
    public String couponGetCount;
    public String totalUv;
    public String totalUserCount;
    public String wifiCount;
    public String acceptCount;
    public String totalCouponGetCount;
    public String uv;
    public String submitCount;
    public String completeCount;

    public MainPageData(String id, String userCount, String totalWifiCount, String couponGetCount, String totalUv, String totalUserCount,
                        String wifiCount, String acceptCount, String totalCouponGetCount, String uv, String submitCount, String completeCount
    ) {
        this.id = id;
        this.userCount = userCount;
        this.totalWifiCount = totalWifiCount;
        this.couponGetCount = couponGetCount;
        this.totalUv = totalUv;
        this.totalUserCount = totalUserCount;
        this.wifiCount = wifiCount;
        this.acceptCount = acceptCount;
        this.totalCouponGetCount = totalCouponGetCount;
        this.uv = uv;
        this.submitCount = submitCount;
        this.completeCount = completeCount;
    }

}
