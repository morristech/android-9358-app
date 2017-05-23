package com.xmd.manager.service.response;

/**
 * Created by linms@xiaomodo.com on 16-4-27.
 */
public class PaidOrderSwitchResult extends BaseResult {

    public Content respData;

    public class Content {
        public String platformFee;
        public String openStatus;
    }
}
