package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-9-29.
 */

public class OfflineStatisticInfo {
    public long aliMember;    //【子】会员充值-支付宝	number
    public long cashMember;    //【子】会员充值-现金	number
    public long cashPos;    //pos现金	number
    public long otherMember;    //【子】会员充值-其他	number
    public long recordCount; //记录数;	number
    public long totalAmount;    //详细列表的总值	number
    public long totalDiscount;//	优惠减免	number
    public long totalMember;    //会员充值订单金额	number
    public long totalPos;    //pos现金支付订单金额	number
    public long totalRecharge;//	充值收入	number
    public long totalRefund;    //退款金额	number
    public long totalRefundOther;//	【子】退款其他	number
    public long totalSubtract;    //【子】错充扣除	number
    public long unionMember;    //【子】会员充值-银联	number
    public long wxMember;    //【子】会员充值-微信	number
}
