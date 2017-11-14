package com.xmd.cashier.dal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zr on 17-11-2.
 */

public class InnerRecordInfo implements Serializable {
    public String id;    //买单ID string
    public String modifyTime;    //修改时间 string
    public String operatorName;    //操作人    string
    public String batchNo;    //结账批次号   string
    public String createTime;    //发起买单时间   string
    public String description;    //买单描述    string
    public int originalAmount;    //买单原价    number
    public int payAmount;    //实付金额 number
    public String payChannel;    //支付方式 string
    public String payChannelName;
    public String payId;    //支付流水号 string
    public String qrType;    //支付二维码类型
    public String status;    //买单状态 string
    public String transactionId;    //第三方流水号
    public String userId;    //用户ID string
    public String userName;    //用户昵称   string
    public String telephone;    //用户手机号 string

    public List<InnerOrderInfo> details;

    public List<OrderDiscountInfo> orderDiscountList;
}
