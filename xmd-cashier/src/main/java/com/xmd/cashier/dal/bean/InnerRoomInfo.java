package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-11-2.
 * 房间
 */

public class InnerRoomInfo {
    public long id;    //房间ID
    public String name;    //房间名称
    public long roomTypeId;    //房间类型ID
    public String roomTypeName;    //房间类型名称
    public int seatCount;    //座位数

    public boolean selected;
}
