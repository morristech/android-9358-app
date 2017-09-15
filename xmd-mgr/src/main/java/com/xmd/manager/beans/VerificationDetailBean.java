package com.xmd.manager.beans;

/**
 * Created by Administrator on 2017/1/16.
 */

public class VerificationDetailBean {

    /**
     * id : 516
     * userId : 648764903829540864
     * userName : mjflx
     * avatar : null
     * telephone : 17727979912
     * businessType : coupon
     * businessId : 702703270044377088
     * verifyCode : 176163999427
     * operatorId : 601634063966539776
     * operatorName : 刘德华
     * verifyTime : 2016-02-25 11:54:49
     * amount : 10000
     * originalAmount : 10000
     * paidType : free
     * status : Y
     * sourceType : registered
     * description : 100元现金券
     * businessTypeName : 优惠券
     * avatarUrl : http://wx.qlogo.cn/mmopen/KZUTxZLF6c0EWiaSetyz1pQ2Jk7k4HWqibKIKat2yVf1d6ry25HFdCUottj7tS8D7nSQ6Z770vbDosGYBLnXhDZl1fGTrjVhDZ/0
     * sourceTypeName : 注册有礼券
     */

    public String id;
    public String userId;
    public String userName;
    public Object avatar;
    public String telephone;
    public String businessType;
    public String businessId;
    public String verifyCode;
    public String operatorId;
    public String operatorName;
    public String verifyTime;
    public int amount;
    public int originalAmount;
    public String paidType;
    public String status;
    public String sourceType;
    public String description;
    public String businessTypeName;
    public String avatarUrl;
    public String sourceTypeName;
    public String currentMonth;
    public String currentMonthTotal;

    public VerificationDetailBean(String currentMonth, String currentMonthTotal) {
        this.currentMonth = currentMonth;
        this.currentMonthTotal = currentMonthTotal;
    }
}
