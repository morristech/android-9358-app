package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-11-4.
 * 查询结果
 */

public class InnerSearchInfo {
    public String id;    //信息ID:room时为房间ID,order时为订单ID,tech/floof_staff时为员工ID
    public String name;    //信息名称	对应类型的名称
    public String type;    //信息类型:room-房间;order-用户手牌的订单;tech-技师;floor_staff-楼面
}
