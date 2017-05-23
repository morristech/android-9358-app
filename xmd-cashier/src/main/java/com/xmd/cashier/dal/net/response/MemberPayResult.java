package com.xmd.cashier.dal.net.response;

/**
 * Created by heyangya on 16-9-19.
 */

public class MemberPayResult extends BaseResult {

    public PayResult respData;

    public static class PayResult {
        public int payMoney;
        public int creditAmount;
        public String phone;
        public String tradeId;
        public String tradeNo;
    }
}
