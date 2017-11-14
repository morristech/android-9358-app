package com.xmd.cashier.dal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zr on 17-11-2.
 * 内网订单对象
 */

public class InnerOrderInfo implements Serializable{
    public int amount;    //账单价格,单位为分
    public String batchNo;
    public String businessNo;    //帐单编号
    public long id;    //账单ID

    public List<InnerOrderItemInfo> itemList;    //账单项列表

    public String payNo;    //支付流水号
    public long roomId;    //房间ID
    public String roomName;    //房间名称
    public String roomTypeName;    //房间类型名称
    public long seatId;    //账单座位ID
    public String startTime;    //开始时间
    public String closeTime;
    public int status;    //账单状态
    public String userIdentify;    //用户手牌标识

    public boolean selected;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof InnerOrderInfo)) {
            return false;
        }
        InnerOrderInfo other = (InnerOrderInfo) obj;
        return id == other.id;
    }
}
