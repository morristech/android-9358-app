package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-4-24.
 */

public class SettleRecordInfo {
    public String createTime;   //创建时间
    public String operatorId;
    public String operatorName;
    public int incomeTotalMoney;    //总金额
    public int monthDataCount;  //该月所有记录数
    public int orderCount;  //订单记录数
    public long settleRecordId; //结算记录ID
}
