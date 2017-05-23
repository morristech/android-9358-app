package com.xmd.manager.service.response;

import com.xmd.manager.beans.DefaultVerificationBean;

/**
 * Created by lhj on 2016/12/7.
 */

public class DefaultVerificationDetailResult extends BaseResult {

    /**
     * respData : {"needAmount":true,"title":"支付","code":"136440827742","type":"pay_for_other","info":null}
     */

    public DefaultVerificationBean respData;

    public DefaultVerificationDetailResult(String error) {
        this.msg = error;
    }


}
