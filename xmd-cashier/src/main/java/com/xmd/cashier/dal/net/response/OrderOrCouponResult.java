package com.xmd.cashier.dal.net.response;


import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.OrderInfo;

/**
 * Created by linms@xiaomodo.com on 16-5-31.
 */
public class OrderOrCouponResult extends BaseResult {

    public Content respData;

    public class Content {
        public CouponInfo userAct;
        public OrderInfo order;
        public String type;
    }
}
