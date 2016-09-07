package com.xmd.technician.bean;

import com.google.gson.annotations.SerializedName;
import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Administrator on 2016/9/7.
 */
public class UserSwitchesResult extends BaseResult {

    /**
     * paidOrder : {"switch":"on","platformFee":"10%"}
     * paidCoupon : {"couponPlatformFee":"","couponSwitch":"off"}
     * account : {"switch":"off"}
     * credit : {"gameTimeoutSeconds":60,"systemSwitch":"on","exchangeRatio":1,"exchangeLimitation":20,"diceGameSwitch":"on","clubSwitch":"on"}
     */

    public RespDataBean respData;

    public static class RespDataBean {

        public PaidOrderBean paidOrder;
        public PaidCouponBean paidCoupon;
        public AccountBean account;
        public CreditBean credit;

       
        public static class PaidOrderBean {
            @SerializedName("switch")
            public String switchX;
            public String platformFee;
        }

        public static class PaidCouponBean {
            public String couponPlatformFee;
            public String couponSwitch;
        }

        public static class AccountBean {
            @SerializedName("switch")
            public String switchX;
        }

        public static class CreditBean {
            public int gameTimeoutSeconds;
            public String systemSwitch;
            public int exchangeRatio;
            public int exchangeLimitation;
            public String diceGameSwitch;
            public String clubSwitch;
        }
    }
}
