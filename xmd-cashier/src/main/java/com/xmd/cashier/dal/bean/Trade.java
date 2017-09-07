package com.xmd.cashier.dal.bean;

import com.xmd.cashier.common.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-9-18.
 */

public class Trade {
    public Trade() {
        couponList = new ArrayList<>();
        discountType = AppConstants.DISCOUNT_TYPE_COUPON;
    }

    public int currentCashier; //当前收银方式:会员支付 OR POS收银程序 OR 小摩豆微信在线买单;为error时表示不需要支付金额
    public boolean isClient;    //用来控制打印是否要打印客户联小票

    //订单信息
    public String tradeNo; //订单号
    public int tradeStatus;//订单状态
    public String tradeTime;//交易时间

    private int originMoney;//原始消费金额
    private int discountType;//0:系统计算 1：用户手动输入
    private int userDiscountMoney;//用户手动输入的抵扣金额
    private int couponDiscountMoney; //抵扣金额=优惠券+预付费订单+朋友请客
    private int willDiscountMoney;//将会优惠金额

    /**
     * 各类优惠券，预付费订单，请客
     **/
    private List<VerificationItem> couponList;//当前优惠券列表
    private int verificationCount;  //将要核销的优惠券数量
    private int verificationMoney;  //将要核销的金额
    private int verificationSuccessfulMoney;//核销成功的金额
    private int verificationNoUseTreatMoney;//核销成功后，没有使用的请客金额

    /**
     * 会员支付
     **/
    public String memberPayMethod;  // 支付方式：接口 或者 二维码memberToken
    public MemberInfo memberInfo;// 会员支付时的会员信息
    public String memberToken;  //会员二维码token
    public MemberRecordInfo memberRecordInfo;//会员支付信息
    public String memberTempPhone;  //临时手机号

    /**
     * 扫码在线买单
     */
    public OnlinePayInfo onlinePayInfo;

    /**
     * 收银台支付
     **/
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

    //设置原始消费金额
    public void setOriginMoney(int originMoney) {
        this.originMoney = originMoney;
    }

    /**
     * 当前需要支付的金额
     **/
    public int getNeedPayMoney() {
        int needPayMoney = originMoney - willDiscountMoney;
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
            case AppConstants.DISCOUNT_TYPE_COUPON:
                return couponDiscountMoney;
            case AppConstants.DISCOUNT_TYPE_USER:
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
