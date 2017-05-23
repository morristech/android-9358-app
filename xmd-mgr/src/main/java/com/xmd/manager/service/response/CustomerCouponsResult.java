package com.xmd.manager.service.response;

import com.xmd.manager.beans.BeanFactory;
import com.xmd.manager.beans.CouponInfo;

/**
 * Created by linms@xiaomodo.com on 16-5-25.
 */
public class CustomerCouponsResult extends BaseListResult<CouponInfo> {

    public CustomerCouponsResult() {
        this.statusCode = 200;
        this.respData = BeanFactory.getCustomerCoupons();
    }
}
