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
    public String useTypeName;
    public String actTitle;
    public String actValue;
    public String couponPeriod;

    public UserGetCouponResult(){

    }

    public UserGetCouponResult(String content, String actId, String techCode, String couponType, String limitTime, String useTypeName,
                               String actTitle, String actValue, String couponPeriod) {
        this.content = content;
        this.actId = actId;
        this.techCode = techCode;
        this.couponType = couponType;
        this.limitTime = limitTime;
        this.useTypeName = useTypeName;
        this.actTitle = actTitle;
        this.actValue = actValue;
        this.couponPeriod = couponPeriod;
    }

    public static class RespDataBean {
        /**
         * userActId : 818657996652421120
         */

        public String userActId;
    }
}
