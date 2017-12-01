package com.xmd.inner.bean;

/**
 * Created by zr on 17-12-1.
 */

public class SeatInfo {
    public String appointTime;    //预定时间	yyyy-MM-dd HH:mm:ss, 只有是预定状态有效
    public long id;    //座位ID
    public String name;    //座位名称/编号
    public long roomId;    //房间ID
    public String status;    //座位状态	free-空闲;using-使用中;booked-已预定;
    public int type;    //座位类型
    public String userIdentify;    //当前使用手牌或预定客户手机号
}
