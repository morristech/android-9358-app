package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Lhj on 2017/1/12.
 */

public class UserGetCouponResult extends BaseResult {
    /**
     * respData : {"userActId":"818657996652421120"}
     */

    public RespDataBean respData;
    public String content;
    public String actId;
    public String techCode;
    public String couponType;
    public String limitTime;

    public UserGetCouponResult(String content, String actId, String techCode, String couponType, String limitTime) {
        this.content = content;
        this.actId = actId;
        this.techCode = techCode;
        this.couponType = couponType;
        this.limitTime = limitTime;
    }

    public static class RespDataBean {
        /**
         * userActId : 818657996652421120
         */

        public String userActId;
    }
}
