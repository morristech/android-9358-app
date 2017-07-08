package com.xmd.cashier.dal.net.response;

import com.xmd.m.network.BaseBean;

/**
 * Created by heyangya on 16-9-19.
 */

public class MemberPayResult extends BaseBean<MemberPayResult.PayResult> {

    public class PayResult {
        public int payMoney;
        public int creditAmount;
        public String phone;
        public String tradeId;
        public String tradeNo;
    }
}
