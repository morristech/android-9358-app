package com.xmd.cashier.dal.bean;

import java.io.Serializable;

/**
 * Created by zr on 17-4-24.
 */

public class SettleRecordInfo implements Serializable {
    public String operatorName;
    public String operatorId;
    public String createTime;   //创建时间
    public long amount;
    public long fastPay;
    public long discount;
    public long recharge;
    public long id;
    public int monthCount;
}
