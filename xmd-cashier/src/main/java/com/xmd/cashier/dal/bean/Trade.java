package com.xmd.cashier.dal.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-9-18.
 */

public class Trade {
    public Trade() {
        couponList = new ArrayList<>();
        verifiedList = new ArrayList<>();
    }

    public int tradeStatus; //交易结果
    public String tradeStatusError; //交易失败描述

    public String currentChannelName;   //当前收银方式名称
    public String currentChannelType;   //当前收银方式标记
    public String currentChannelMark;   //当前收银方式备注

    public String memberId;     //会员ID
    public MemberInfo memberInfo;   //会员信息

    public String tradeNo;  //POS交易订单号
    public String batchNo;  //批量结账单号
    public String payOrderId;   //支付订单ID
    public String payNo;    //当次支付号
    public String payUrl;   //当次支付URL

    private List<VerificationItem> couponList;  //当前核销列表
    private List<TradeDiscountInfo> verifiedList;   //已核销列表

    private int originMoney;    //原始消费金额

    private int willReductionMoney;     // 直接减免的金额

    private int willDiscountMoney;      // 此次核销的金额
    private int alreadyDiscountMoney;   // 已经核销的金额
    private int verificationCount;      // 将要核销的优惠券数量

    private int alreadyCutMoney;        // 优惠减免的金额 = 已经核销的金额 + 此次核销的金额 + 直接减免的金额

    private int alreadyPayMoney;        // 已经支付的金额

    private int willPayMoney;       // 此次支付的金额

    private int needPayMoney;       // 需要支付的金额

    private String posTradeNo;      //收银台订单号
    public String posPayCashierNo;  //POS支付返回的CashierNo

    public TradeRecordInfo innerRecordInfo;

    public int getAlreadyCutMoney() {
        return alreadyCutMoney;
    }

    public void setAlreadyCutMoney(int money) {
        alreadyCutMoney = money;
    }

    public void setAlreadyPayMoney(int money) {
        alreadyPayMoney = money;
    }

    public int getAlreadyPayMoney() {
        return alreadyPayMoney;
    }

    public void setAlreadyDiscountMoney(int money) {
        alreadyDiscountMoney = money;
    }

    public int getAlreadyDiscountMoney() {
        return alreadyDiscountMoney;
    }

    public List<TradeDiscountInfo> getVerifiedList() {
        return verifiedList;
    }

    public void setVerifiedList(List<TradeDiscountInfo> list) {
        verifiedList = list;
    }

    public int getWillReductionMoney() {
        return willReductionMoney;
    }

    public void setWillReductionMoney(int money) {
        willReductionMoney = money;
    }

    public int getWillPayMoney() {
        return willPayMoney;
    }

    public void setWillPayMoney(int money) {
        willPayMoney = money;
    }

    public int getNeedPayMoney() {
        return needPayMoney;
    }

    public void setNeedPayMoney(int money) {
        needPayMoney = money;
    }

    public int getOriginMoney() {
        return originMoney;
    }

    public void setOriginMoney(int originMoney) {
        this.originMoney = originMoney;
    }

    public int getWillDiscountMoney() {
        return willDiscountMoney;
    }

    public void setWillDiscountMoney(int willDiscountMoney) {
        this.willDiscountMoney = willDiscountMoney;
    }

    // 核销列表
    public List<VerificationItem> getCouponList() {
        return couponList;
    }

    public void cleanCouponList() {
        couponList.clear();
    }

    // 预计核销数目
    public int getVerificationCount() {
        return verificationCount;
    }

    public void setVerificationCount(int verificationCount) {
        this.verificationCount = verificationCount;
    }

    public synchronized String getPosTradeNo() {
        if (posTradeNo == null) {
            newCashierTradeNo();
        }
        return posTradeNo;
    }

    //生成新的订单号给收银APP使用
    public void newCashierTradeNo() {
        int seed = 0;
        if (posTradeNo != null) {
            seed = Integer.parseInt(posTradeNo.subSequence(posTradeNo.length() - 4, posTradeNo.length()).toString());
            seed++;
        }
        posTradeNo = String.format("%s%04d", tradeNo, seed);
    }
}
