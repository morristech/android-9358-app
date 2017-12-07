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
    public static final String PROJECT_LIST_AVAILABLE = BASE_URL + "/v2/project/list/available"; //可用项目
    public static final String TECHNICIAN_LIST = BASE_URL + "/v2/manager/club/select/technician/list"; //技师列表
    public static final String NATIVE_ORDER_ITEM_BELL_LIST = BASE_URL + "/v2/manager/native/order/item/bell/list";
    public static final String ORDER_BATCH_SAVE = BASE_URL + "/v2/manager/native/order/batch-save"; // 座位开单
    public static final String USER_IDENTIFY_HAVE = BASE_URL + "/v2/manager/user/identify/have"; //是否必须手牌号
    public static final String USER_IDENTIFY_LIST = BASE_URL + "/v2/manager/user/identify/list";//手牌列表
    public static final String NATIVE_ORDER_ITEM_UPDATE = BASE_URL + "/v2/manager/native/order/item/update"; //更新消费信息

}
