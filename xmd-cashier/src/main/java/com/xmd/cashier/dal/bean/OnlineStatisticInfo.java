package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-9-29.
 */

public class OnlineStatisticInfo {
    public int fastPay; // 在线买单收入 number
    public int internalCommission;  // 优惠减免 number
    public int itemCard;    //项目次卡 number
    public int itemPackage; // 混合套餐 number
    public int mall;    // 商城收入 number
    public int marketing;   // 营销活动 number
    public int paidCoupon;  // 点钟券 number
    public int paidOrder;   // 付费预约 number
    public int paidServiceItem; // 限时抢 number
    public int qrClub;  //水牌买单 number
    public int qrPos;   // POS买单 number
    public int qrTech;  // 扫工牌买单 number
    public int recharge;    // 会员充值 number
    public int totalAli;    // 支付宝 number
    public int totalAmount; // 交易总金额 number
    public int totalDiscount;   // 用券抵扣 number
    public int totalManagerAmount;  // 会所部分 number
    public int totalManagerGetAmount;   // 实际结算金额 number
    public int totalRefund; // 退款 number
    public int totalSettleAmount;   // 应结算金额 number 光大或代付！
    public int totalSettleFee;  // 手续费 number
    public int totalUnion;  // 银联 number
    public int totalWx; // 微信 number
}
