package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-7-19.
 * 处理充值流程
 */

public class MemberRechargeProcess {
    public MemberRechargeProcess() {
        memberInfo = new MemberInfo();
        techInfo = new TechInfo();
    }

    // 会员ID
    private long memberId;
    // 会员实例
    private MemberInfo memberInfo;
    // 充值订单ID
    private String orderId;
    // 充值套餐ID
    private String packageId;
    // 充值套餐名称
    private String packageName;
    // 充值套餐金额
    private int packageAmount;
    // 充值指定金额
    private int amount;

    private String rechargeAmountType;

    // 充值描述
    private String description;

    // 充值支付方式:区分POS支付或者扫码支付
    private int rechargePayType;

    // 扫码支付方式:区分微信/支付宝  getPayChannel
    private String scanPayType;
    // 充值订单二维码URL
    private String payUrl;

    // POS支付方式:区分现金=1/银行卡=4  getPayTypeString
    private int posPayType;

    // 营销人员ID
    private TechInfo techInfo;

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public MemberInfo getMemberInfo() {
        return memberInfo;
    }

    public void setMemberInfo(MemberInfo memberInfo) {
        this.memberInfo = memberInfo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(int packageAmount) {
        this.packageAmount = packageAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRechargePayType() {
        return rechargePayType;
    }

    public void setRechargePayType(int rechargePayType) {
        this.rechargePayType = rechargePayType;
    }

    public String getScanPayType() {
        return scanPayType;
    }

    public void setScanPayType(String scanPayType) {
        this.scanPayType = scanPayType;
    }

    public int getPosPayType() {
        return posPayType;
    }

    public void setPosPayType(int posPayType) {
        this.posPayType = posPayType;
    }

    public TechInfo getTechInfo() {
        return techInfo;
    }

    public void setTechInfo(TechInfo techInfo) {
        this.techInfo = techInfo;
    }

    public String getRechargeAmountType() {
        return rechargeAmountType;
    }

    public void setRechargeAmountType(String rechargeAmountType) {
        this.rechargeAmountType = rechargeAmountType;
    }
}
