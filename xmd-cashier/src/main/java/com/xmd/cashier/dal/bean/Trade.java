package com.xmd.cashier.dal.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-9-18.
 */

public class Trade {
    public static final int DISCOUNT_TYPE_NONE = 0;
    public static final int DISCOUNT_TYPE_COUPON = 2;
    public static final int DISCOUNT_TYPE_USER = 1;

    //订单信息
    public String tradeNo; //订单号
    public int tradeStatus;//订单状态
    public String tradeTime;//交易时间

    private int originMoney;//原始消费金额
    private int discountType;//0:系统计算 1：用户手动输入
    private int userDiscountMoney;//用户手动输入的抵扣金额
    private int couponDiscountMoney; //抵扣金额=优惠券+预付费订单+朋友请客

    private int willDiscountMoney;

    /**
     * 各类优惠券，预付费订单，请客
     **/
    private List<VerificationItem> couponList;//当前优惠券列表
    private int verificationCount;//将要核销的优惠券数量
    private int verificationMoney;//将要核销的金额
    private int verificationSuccessfulMoney;//核销成功的金额
    private int verificationNoUseTreatMoney;//核销成功后，没有使用的请客金额

    public int currentCashier; //当前收银方式，会员支付 OR POS收银程序 OR 小摩豆微信在线买单

    /**
     * 会员支付
     **/
    public MemberInfo memberInfo;//会员支付信息
    public int memberNeedPayMoney;//会员需要支付的金额
    public String memberPayError; //会员支持错误提示
    public int memberPayResult; //会员支付结果
    public int memberPaidDiscountMoney;//会员实际折扣金额
    private int memberPaidMoney; //会员实际支付的金额
    public int memberPoints; //交易后会员获得的积分
    public String memberCanDiscount;// Y打折，N不打折
    public String memberPayCertificate;//会员支付凭证

    /**
     * 收银台支付
     **/
    private int posPayType;
    private String posTradeNo; //收银台订单号
    public String posPayTypeString; //收银台支付方式
    public int posMoney; //收银台支付金额
    public int posPayResult; //收银台支付结果
    public Object posPayReturn;//收银台支付返回
    public int posPoints;//收银台收银获得积分
    public String posPointsPhone;//收银台积分需要送到的手机号

    /**
     * 二维码
     **/
    public byte[] qrCodeBytes;

    public Trade() {
        couponList = new ArrayList<>();
        discountType = DISCOUNT_TYPE_COUPON;
    }

    /**
     * 扫码在线买单
     */
    private String onlinePayId;
    private int onlinePayPaidMoney;
    private String onlinePayChannel;

    // 在线扫码买单
    public void setOnlinePayId(String payId) {
        this.onlinePayId = payId;
    }

    public String getOnlinePayId() {
        return this.onlinePayId;
    }

    public void setOnlinePayPaidMoney(int onlineMoney) {
        this.onlinePayPaidMoney = onlineMoney;
    }

    public int getOnlinePayPaidMoney() {
        return this.onlinePayPaidMoney;
    }

    public void setOnlinePayChannel(String channel) {
        this.onlinePayChannel = channel;
    }

    public String getOnlinePayChannel() {
        return this.onlinePayChannel;
    }

    //设置原始消费金额
    public void setOriginMoney(int originMoney) {
        this.originMoney = originMoney;
    }

    //设置会员已支付金额
    public void setMemberPaidMoney(int memberMoney) {
        this.memberPaidMoney = memberMoney;
        memberPaidDiscountMoney = memberNeedPayMoney - memberPaidMoney;//计算折扣金额
    }

    public int getMemberPaidMoney() {
        return memberPaidMoney;
    }

    public int getMemberPaidDiscountMoney() {
        return memberPaidDiscountMoney;
    }

    /**
     * 当前需要支付的金额
     **/
    public int getNeedPayMoney() {
        int needPayMoney = originMoney - memberPaidMoney - memberPaidDiscountMoney - willDiscountMoney;
        if (needPayMoney < 0) {
            needPayMoney = 0;
        }
        return needPayMoney;
    }

    public int getOriginMoney() {
        return originMoney;
    }

    //将要减免的金额
    public int getWillDiscountMoney() {
        return willDiscountMoney;
    }

    public void setWillDiscountMoney(int willDiscountMoney) {
        this.willDiscountMoney = willDiscountMoney;
    }

    //实际减免金额
    public int getReallyDiscountMoney() {
        switch (discountType) {
            case DISCOUNT_TYPE_COUPON:
                return couponDiscountMoney;
            case DISCOUNT_TYPE_USER:
                return userDiscountMoney;
        }
        return 0;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public int getUserDiscountMoney() {
        return userDiscountMoney;
    }

    public void setUserDiscountMoney(int userDiscountMoney) {
        this.userDiscountMoney = userDiscountMoney;
    }

    public void setCouponDiscountMoney(int couponDiscountMoney) {
        this.couponDiscountMoney = couponDiscountMoney;
    }

    public int getCouponDiscountMoney() {
        return couponDiscountMoney;
    }

    public List<VerificationItem> getCouponList() {
        return couponList;
    }

    public void cleanCouponList() {
        couponList.clear();
    }

    public int getVerificationCount() {
        return verificationCount;
    }

    public void setVerificationCount(int verificationCount) {
        this.verificationCount = verificationCount;
    }

    public int getVerificationMoney() {
        return verificationMoney;
    }

    public void setVerificationMoney(int verificationMoney) {
        this.verificationMoney = verificationMoney;
    }

    public int getVerificationSuccessfulMoney() {
        return verificationSuccessfulMoney;
    }

    public void setVerificationSuccessfulMoney(int verificationSuccessfulMoney) {
        this.verificationSuccessfulMoney = verificationSuccessfulMoney;
    }

    public synchronized String getPosTradeNo() {
        if (posTradeNo == null) {
            newCashierTradeNo();
        }
        return posTradeNo;
    }

    public int getPosMoney() {
        return posMoney;
    }

    public String getPosPayTypeString() {
        return posPayTypeString;
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

    public int getVerificationNoUseTreatMoney() {
        return verificationNoUseTreatMoney;
    }

    public void setVerificationNoUseTreatMoney(int verificationNoUseTreatMoney) {
        this.verificationNoUseTreatMoney = verificationNoUseTreatMoney;
    }
}
