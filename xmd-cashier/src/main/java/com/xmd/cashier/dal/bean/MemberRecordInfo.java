package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-7-15.
 * 会所会员账户记录
 */

public class MemberRecordInfo {
    public String id;   //订单ID
    public String tradeNo;  //交易流水号
    public String tradeType;    //交易类型:pay-扣除金额;income-添加金额
    public String platform;     //offline-PC后台;online-线上充值;cashier-POS机
    public String payChannel;   //account-会员;wx-微信;ali-支付宝;union-银联;cash-现金；other-其他;
    public String payChannelName;   //支付方式名称

    public int accountAmount;   //账户余额
    public int amount;      //交易变动金额

    public String businessCategory; //业务类型
    public String businessCategoryName; //业务类型名称
    public String businessNo;   //订单充值活动编号

    public String userId;
    public String name;     //会员昵称
    public String telephone;    //会员手机号
    public String cardNo;   //会员卡号
    public long memberId;   //会员ID
    public int memberTypeId;    //会员等级
    public String memberTypeName;   //会员等级名称
    public String avatarUrl;    //用户头像

    public String operatorName; //操作人
    public String createTime;   //创建时间
    public String description;  //订单描述

    public int discount;     //折扣
    public int discountAmount;  //优惠金额
    public int orderAmount; //原订单金额

    public String techId;
    public String techName;
    public String techNo;

    public Long packageId;
    public String activityName;
}
