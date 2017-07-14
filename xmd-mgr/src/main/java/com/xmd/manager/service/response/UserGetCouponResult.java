package com.xmd.manager.service.response;


import com.xmd.manager.beans.CouponInfo;

/**
 * Created by Administrator on 2017/1/9.
 */

public class UserGetCouponResult extends BaseResult {
    /**
     * respData : {"userActId":"818657996652421120"}
     */

    public CouponInfo couponInfo;
    public RespDataBean respData;

    public static class RespDataBean {
        /**
         * userActId : 818657996652421120
         */

        public String userActId;
    }

    public UserGetCouponResult(CouponInfo couponInfo, RespDataBean respDate) {
        this.couponInfo = couponInfo;
        this.respData = respDate;
    }

}
