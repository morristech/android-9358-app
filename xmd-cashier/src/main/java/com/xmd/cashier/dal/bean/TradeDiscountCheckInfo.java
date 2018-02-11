package com.xmd.cashier.dal.bean;

import java.io.Serializable;

/**
 * Created by zr on 17-11-9.
 */

public class TradeDiscountCheckInfo implements Serializable {
    public int amount;    //抵扣金额	单位为分
    public String cardNo;
    public String consumeDescription;    //具体指会员消费的等级，礼品券的礼品名称；优惠券的优惠内容;
    public String sourceName;    //活动来源
    public String telephone;    //手机号
    public String title;    //标题
    public String type;    //类型:cash_coupon-现金券;discount_coupon-折扣券;coupon-体验券;gift_coupon-礼品券;service_item_coupon-项目券;consume-会员消费;paid_order-付费预约
    public String typeName;    //类型名称
    public String userName;    //会会名称
    public String verifyCode;    //核销码
}
