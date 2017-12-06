package com.xmd.inner.httprequest;

/**
 * Created by Lhj on 17-11-22.
 * 用于存放请求链接及请求Key
 */

public class RequestConstant {
    public static final String BASE_URL = "/spa-manager/api";

    public static final String SAVE_BOOK_ROOM_SEAT_URL = BASE_URL + "/v2/manager/native/order/booked/save";    //预定
    public static final String CANCEL_BOOK_ROOM_SEAT_URL = BASE_URL + "/v2/manager/native/order/booked/cancel";//取消预定
    public static final String ENABLE_ROOM_STATUS = BASE_URL + "/v2/manager/native/room/status/enable";        //房间启动和禁用
    public static final String GET_ROOM_SEAT_INFO_LIST = BASE_URL + "/v2/manager/native/room/list";            //获取房间信息列表
    public static final String GET_ORDER_INFO_BY_ROOM = BASE_URL + "/v2/manager/native/order/room/detail";   //获取房间开单座位和开单详情
    public static final String GET_ROOM_STATUS_SETTING = BASE_URL + "/v2/manager/native/room/setting/status/info";     //获取房间状态设置
    public static final String GET_ROOM_STATUS_STATISTICS = BASE_URL + "/v2/manager/native/room/status/stat";   //房间状态统计

}
