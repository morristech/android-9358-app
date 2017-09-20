package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-5-2.
 * 核销记录
 */

public class VerifyRecordInfo {
    public int amount;          //核销金额:如果paidType='credits'代表积分值;否则为金额,单位为分;对于优惠券代表抵扣金额,如果为负数表示抵扣值未知
    public int originalAmount;  //原始价格

    public String paidType;     //支付类型:amount-金额支付;credits-积分支付;free-免费赠送
    public String platformName; //核销平台名称
    public String sourceType;   //核销单来源类型
    public String sourceTypeName;   //核销单来源类型名称

    public String userId;   //用户ID
    public String avatar;   //用户头像ID
    public String avatarUrl;    //用户头像URL
    public String telephone;    //用户手机号
    public String userName;     //用户名称

    public String verifyCode;   //核销码
    public long id;     //核销记录ID
    public String operatorId;   //核销操作人ID
    public String operatorName; //核销操作人
    public String businessId;   //核销单ID
    public String businessType; //核销单类型
    public String businessTypeName; //核销单类型名称
    public String description;  //核销详情
    public String verifyTime;   //核销时间:yyyy-MM-dd HH:mm:ss

    public int currentCount;

    // 用于打印
    public String consumeMoneyDescription;  // 券使用说明
    public String techDescription;  //预约技师
    public String serviceItemName;  //预约项目
    public String memberCardNo; //会员卡号
    public String memberPhone;  //会员请客授权手机号
    public String memberTypeName;   //会员等级描述
    public String memberDiscountDesc;   //会员折扣信息描述
    public String giftCouponName;   //礼品券名称
    public String prizeActivityName; //奖品活动名称
}
