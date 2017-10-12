package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-9-29.
 */

public class OnlineStatisticInfo {
    public long fastPay; // 在线买单收入 number
    public long internalCommission;  // 优惠减免 number
    public long itemCard;    //项目次卡 number
    public long itemPackage; // 混合套餐 number
    public long mall;    // 商城收入 number
    public long marketing;   // 营销活动 number
    public long paidCoupon;  // 点钟券 number
    public long paidOrder;   // 付费预约 number
    public long paidServiceItem; // 限时抢 number
    public long qrClub;  //水牌买单 number
    public long qrPos;   // POS买单 number
    public long qrTech;  // 扫工牌买单 number
    public long recharge;    // 会员充值 number
    public long totalAli;    // 支付宝 number
    public long totalAmount; // 交易总金额 number
    public long totalDiscount;   // 用券抵扣 number
    public long totalManagerAmount;  // 会所部分 number
    public long totalManagerGetAmount;   // 实际结算金额 number
    public long totalRefund; // 退款 number
    public long totalSettleAmount;   // 应结算金额 number 光大或代付！
    public long totalSettleFee;  // 手续费 number
    public long totalUnion;  // 银联 number
    public long totalWx; // 微信 number
}
