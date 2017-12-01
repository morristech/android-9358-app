package com.xmd.inner.bean;

import java.util.List;

/**
 * Created by zr on 17-12-1.
 * 内网订单
 */

public class OrderInfo {
    public int amount;    //账单价格,单位为分
    public String batchNo;
    public String businessNo;    //帐单编号
    public long id;    //账单ID

    public List<ConsumeInfo> itemList;    //账单项列表

    public String payNo;    //支付流水号
    public long roomId;    //房间ID
    public String roomName;    //房间名称
    public String roomTypeName;    //房间类型名称
    public long seatId;    //账单座位ID
    public String seatName;
    public String startTime;    //开始时间
    public String closeTime;
    public int status;    //账单状态
    public String userIdentify;    //用户手牌标识
}

