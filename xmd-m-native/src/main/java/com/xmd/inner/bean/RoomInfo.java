package com.xmd.inner.bean;

import java.util.List;

/**
 * Created by zr on 17-12-1.
 */

public class RoomInfo {
    public long id;    //房间ID
    public String name;    //房间名称
    public long roomTypeId;    //房间类型ID
    public String roomTypeName;    //房间类型名称
    public int seatCount;    //房间座位数
    public int useCount;    //房间座位使用数
    public List<SeatInfo> seatList;    //房间座位信息列表
    public String status;    //房间状态	free-空闲;using-使用中;clean-清洁;booked-已预定;disabled-禁用
}
