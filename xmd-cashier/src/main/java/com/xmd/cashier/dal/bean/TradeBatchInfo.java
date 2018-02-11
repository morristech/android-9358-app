package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-11-8.
 */

public class TradeBatchInfo {
    public String batchNo;    //批量结账单号
    public int discountAmount;    //本次新增的优惠抵扣	单位为分
    public String payNo;             //部分支付订单No
    public int oriAmount;    //订单原价	单位为分
    public int payAmount;    //实际应付价	单位为分
    public String payOrderId;    //支付订单ID	买单ID
    public String payUrl;    //支付URL	支付URL
    public String status;    //支付订单状态	N-生成待支付,Y-已支付完成
}
