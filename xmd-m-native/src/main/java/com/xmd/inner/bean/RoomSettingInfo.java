package com.xmd.inner.bean;

/**
 * Created by zr on 17-12-5.
 */

public class RoomSettingInfo {
    public String code;    //状态编码	string	free-空闲;using-使用中;clean-清洁;booked-已预定;disabled-禁用
    public int color;    //状态颜色	number	大于0的整数
    public String name;    //状态名称	string
    public int sequenceNo;    //显示顺序号	number
    public String status;    //是否启用	string	Y-启用;N-禁用
}
