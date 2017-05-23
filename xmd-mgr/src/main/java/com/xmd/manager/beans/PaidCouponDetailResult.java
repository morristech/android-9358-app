package com.xmd.manager.beans;

import com.xmd.manager.service.response.BaseResult;

/**
 * Created by Administrator on 2016/6/28.
 */
public class PaidCouponDetailResult extends BaseResult {

    public PaidCoupondetail respData;
    public String type;

    public static class PaidCoupondetail {
        public double clubAmount;
        public int expireCount;
        public int paidCount;
        public float techCommission;
        public int useCount;

        public int share;

    }
}
