package com.xmd.technician.bean;

import com.hyphenate.chat.EMMessage;
import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Lhj on 2017/1/12.
 */

public class UserGetCouponResult extends BaseResult{
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

    public UserGetCouponResult(EMMessage msg) {
        this.mMessage = msg;
    }
}
