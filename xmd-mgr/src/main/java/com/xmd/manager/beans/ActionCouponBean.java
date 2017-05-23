package com.xmd.manager.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lhj on 2016/12/9.
 */

public class ActionCouponBean implements Serializable {


    /**
     * startDate : 2017-03-06
     * suaId : 839006227231940608
     * actId : 839006105114800128
     * couponNo : 177284868620
     * useType : paid_service_item
     * couponType : service_item
     * endUseDate : 2017-03-25 23:59:59
     * useTypeName : 抢购项目
     * endDate : 2017-03-23
     * couponPeriod : 2017-03-06 00:00:00 至 2017-03-25 23:59:59
     * consumeAmount : 1000
     * actContent : <ul><li>oih;</li></ul>
     * getDate : 2017-03-07 14:54:07
     * actStatus : online
     * itemNames : ["大放送大放送大放送大放送大"]
     * couponTypeName : 项目抵用券
     * useTimePeriod : 周三，周日 00:00 - 03:00
     * actTitle : 大放送大放送大放送大放送大
     * actSubTitle : 大放送大放送大放送大放送大
     * actStatusName : 在线
     * creditAmount : 1
     * actAmount : 1000
     * paidType : credits
     * consumeMoneyDescription : 1积分
     */

    public String startDate;
    public String suaId;
    public String actId;
    public String couponNo;
    public String useType;
    public String couponType;
    public String endUseDate;
    public String useTypeName;
    public String endDate;
    public String couponPeriod;
    public int consumeAmount;
    public String actContent;
    public String getDate;
    public String actStatus;
    public String couponTypeName;
    public String useTimePeriod;
    public String actTitle;
    public String actSubTitle;
    public String actStatusName;
    public int creditAmount;
    public int actAmount;
    public String paidType;
    public String consumeMoneyDescription;
    public List<String> itemNames;
}
