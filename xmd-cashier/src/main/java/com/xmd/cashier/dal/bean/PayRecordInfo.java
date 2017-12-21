package com.xmd.cashier.dal.bean;

import java.io.Serializable;

/**
 * Created by zr on 17-12-19.
 */

public class PayRecordInfo implements Serializable {
    public int amount;    //支付金额	单位为分
    public String payChannel;    //支付渠道	account-会员;wx-微信;ali-支付宝;union-银联;cash-现金;以custom_开头的自定义支付方式
    public String payChannelName;    //支付渠道名称
    public String operatorName;
    public String payNo;    //支付流水号
    public String payTime;    //支付时间
    public String platform;    //支付平台
    public String tradeNo;    //第三方流水号
}
