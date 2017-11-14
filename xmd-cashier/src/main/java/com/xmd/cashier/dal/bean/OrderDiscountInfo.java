package com.xmd.cashier.dal.bean;

import java.io.Serializable;

/**
 * Created by zr on 17-11-9.
 */

public class OrderDiscountInfo implements Serializable {
    public int amount;    //抵扣金额
    public String bizName;    //抵扣名称
    public long id;    //抵扣项ID
    public String status;    //核销状态
    public String type;    //优惠项类型:paid_order-预约;coupon-用券;member-会员
    public String verifyCode;    //核销码

    public OrderDiscountCheckInfo checkInfo;
}
