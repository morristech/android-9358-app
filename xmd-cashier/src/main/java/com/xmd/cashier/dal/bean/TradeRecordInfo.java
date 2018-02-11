package com.xmd.cashier.dal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zr on 17-11-2.
 */

public class TradeRecordInfo implements Serializable {
    public String userAvatarUrl;    //用户头像

    public String techId;    //技师ID
    public String techName;  //技师名称
    public String techNo;    //技师编号
    public String otherTechNames;    //合并技师

    public String platform;     //会员支付平台

    public String id;    //买单ID string
    public String modifyTime;    //修改时间 string
    public String operatorName;    //操作人    string
    public String batchNo;    //结账批次号   string
    public String createTime;    //发起买单时间   string
    public String description;    //买单描述    string
    public int originalAmount;    //买单原价    number
    public int payAmount;    //实付金额 number
    public int paidAmount;  //已付金额
    public String payChannel;    //支付方式 string
    public String payChannelName;
    public String payId;    //支付流水号 string
    public String qrType;    //支付二维码类型
    public String status;    //买单状态 string
    public String transactionId;    //第三方流水号
    public String userId;    //用户ID string
    public String userName;    //用户昵称   string
    public String telephone;    //用户手机号 string

    public List<InnerOrderInfo> details;    // 内网详情

    public List<TradeDiscountInfo> orderDiscountList;   // 折扣列表

    public List<PayRecordInfo> payRecordList;   // 支付列表

    public PayCouponInfo payCouponInfo;
    public boolean isDetail;
    public int tempNo;   //用来标识是此次列表中的第几个元素
    public String tempErrMsg;
}
