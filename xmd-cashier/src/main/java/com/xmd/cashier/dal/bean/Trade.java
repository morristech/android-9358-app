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
        verifiedList = new ArrayList<>();
        discountType = AppConstants.DISCOUNT_TYPE_COUPON;
    }

    public int currentCashier;  //当前收银方式标记
    public String currentCashierName;   //当前收银方式名称
    public String currentCashierType;
    public String currentCashierMark;   //当前收银方式备注
    public boolean isClient;    //用来控制打印是否要打印客户联小票

    //订单信息
    public String tradeNo;  //订单号
    public int tradeStatus; //订单状态
    public String tradeTime;    //交易时间

    private int originMoney;    //原始消费金额  |内网
    private int discountType;   //0:无; 1:用户手动输入; 2:核销;
    private int userDiscountMoney;  //用户手动输入的抵扣金额
    private int couponDiscountMoney;    //核销抵扣金额=各种券+预付费订单+朋友请客
    private int willDiscountMoney;  //预计优惠金额    |内网
    private int willPayMoney;   //应该支付的金额

    /**
     * 体验券|点钟券|折扣券|现金券|付费预约|请客
     **/
    private List<VerificationItem> couponList;//当前核销列表
    private int verificationCount;  //将要核销的优惠券数量
    private int verificationMoney;  //将要核销的金额
    private int verificationSuccessfulMoney;//核销成功的金额
    private int verificationNoUseTreatMoney;//核销成功后，没有使用的请客金额

    /**
     * 会员支付
     **/
    public String memberId;
    public String memberPayMethod;  // 支付方式：接口 或者 二维码memberToken
    public MemberInfo memberInfo;   // 会员支付时的会员信息
    public String memberToken;  //会员二维码token
    public MemberRecordInfo memberRecordInfo;   //会员支付信息
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
    public String posPayTypeChannel;
    public int posMoney; //收银台支付金额
    public int posPayResult; //收银台支付结果
    public Object posPayReturn;//收银台支付返回
    public int posPoints;//收银台收银获得积分

    /**
     * 内网
     */
    public String batchNo;      //批次号
    public String payOrderId;   //订单编号
    public String payUrl;
    private List<OrderDiscountInfo> verifiedList;
    private int willReductionMoney;
    private int alreadyDiscountMoney;

    public void setAlreadyDiscountMoney(int money) {
        alreadyDiscountMoney = money;
    }

    public int getAlreadyDiscountMoney() {
        return alreadyDiscountMoney;
    }

    public List<OrderDiscountInfo> getVerifiedList() {
        return verifiedList;
    }

    public void setVerifiedList(List<OrderDiscountInfo> list) {
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

    // 需要支付的金额
    public int getNeedPayMoney() {
        int needPayMoney = originMoney - willDiscountMoney;
        if (needPayMoney < 0) {
            needPayMoney = 0;
        }
        return needPayMoney;
    }

    // 初始消费金额
    public int getOriginMoney() {
        return originMoney;
    }

    public void setOriginMoney(int originMoney) {
        this.originMoney = originMoney;
    }

    // 预计减免的金额
    public int getWillDiscountMoney() {
        return willDiscountMoney;
    }

    public void setWillDiscountMoney(int willDiscountMoney) {
        this.willDiscountMoney = willDiscountMoney;
    }

    // 减免类型
    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    // 手动减免金额
    public int getUserDiscountMoney() {
        return userDiscountMoney;
    }

    public void setUserDiscountMoney(int userDiscountMoney) {
        this.userDiscountMoney = userDiscountMoney;
    }

    // 核销减免金额
    public void setCouponDiscountMoney(int couponDiscountMoney) {
        this.couponDiscountMoney = couponDiscountMoney;
    }

    public int getCouponDiscountMoney() {
        return couponDiscountMoney;
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

    // 预计核销金额
    public int getVerificationMoney() {
        return verificationMoney;
    }

    public void setVerificationMoney(int verificationMoney) {
        this.verificationMoney = verificationMoney;
    }

    // 核销成功金额
    public int getVerificationSuccessfulMoney() {
        return verificationSuccessfulMoney;
    }

    public void setVerificationSuccessfulMoney(int verificationSuccessfulMoney) {
        this.verificationSuccessfulMoney = verificationSuccessfulMoney;
    }

    // 请客未使用金额
    public int getVerificationNoUseTreatMoney() {
        return verificationNoUseTreatMoney;
    }

    public void setVerificationNoUseTreatMoney(int verificationNoUseTreatMoney) {
        this.verificationNoUseTreatMoney = verificationNoUseTreatMoney;
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

    public String getPosPayTypeChannel() {
        return posPayTypeChannel;
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
