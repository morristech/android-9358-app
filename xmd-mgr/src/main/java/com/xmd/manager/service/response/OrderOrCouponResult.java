package com.xmd.manager.service.response;

import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.beans.Order;

/**
 * Created by linms@xiaomodo.com on 16-5-31.
 */
public class OrderOrCouponResult extends BaseResult {

    public Content respData;

    public class Content {
        public CouponInfo coupon;
        public Order order;
        public String type;
    }
}
