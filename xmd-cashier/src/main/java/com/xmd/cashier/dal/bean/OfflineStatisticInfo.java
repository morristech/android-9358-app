package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-9-29.
 */

public class OfflineStatisticInfo {
    public int aliMember;    //【子】会员充值-支付宝	number
    public int cashMember;    //【子】会员充值-现金	number
    public int cashPos;    //pos现金	number
    public int otherMember;    //【子】会员充值-其他	number
    public int recordCount; //记录数;	number
    public int totalAmount;    //详细列表的总值	number
    public int totalDiscount;//	优惠减免	number
    public int totalMember;    //会员充值订单金额	number
    public int totalPos;    //pos现金支付订单金额	number
    public int totalRecharge;//	充值收入	number
    public int totalRefund;    //退款金额	number
    public int totalRefundOther;//	【子】退款其他	number
    public int totalSubtract;    //【子】错充扣除	number
    public int unionMember;    //【子】会员充值-银联	number
    public int wxMember;    //【子】会员充值-微信	number
}
