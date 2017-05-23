package com.xmd.manager.service.response;


import com.hyphenate.chat.EMMessage;

/**
 * Created by Administrator on 2017/1/9.
 */

public class UserGetCouponResult extends BaseResult {
    /**
     * respData : {"userActId":"818657996652421120"}
     */

    public RespDataBean respData;
    public String actId;
    public String content;
    public EMMessage mMessage;

    public static class RespDataBean {
        /**
         * userActId : 818657996652421120
         */

        public String userActId;
    }

    public UserGetCouponResult(RespDataBean respDate, EMMessage emMessage) {
        this.statusCode = 200;
        this.respData = respDate;
        this.mMessage = emMessage;
    }

    public UserGetCouponResult(EMMessage msg) {
        this.mMessage = msg;
    }

}
