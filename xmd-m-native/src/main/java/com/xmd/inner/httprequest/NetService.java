package com.xmd.inner.httprequest;

import com.xmd.inner.httprequest.response.RoomOrderInfoResult;
import com.xmd.inner.httprequest.response.RoomSeatListResult;
import com.xmd.inner.httprequest.response.RoomSettingResult;
import com.xmd.inner.httprequest.response.RoomStatisticResult;
import com.xmd.m.network.BaseBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Lhj on 17-12-01.
 * 用于存放请求
 */

public interface NetService {
    // 房间座位
    @FormUrlEncoded
    @POST(RequestConstant.GET_ROOM_SEAT_INFO_LIST)
    Observable<RoomSeatListResult> getRoomSeatInfoList(@Field("status") String status,
                                                       @Field("roomName") String roomName);

    // 房间订单
    @GET(RequestConstant.GET_ORDER_INFO_BY_ROOM)
    Observable<RoomOrderInfoResult> getOrderInfoByRoom(@Query("roomId") String roomId);

    // 房间启用和禁用
    @FormUrlEncoded
    @POST(RequestConstant.ENABLE_ROOM_STATUS)
    Observable<BaseBean> setRoomStatus(@Field("roomId") String roomId,
                                       @Field("status") String status);

    // 房间预定
    @FormUrlEncoded
    @POST(RequestConstant.SAVE_BOOK_ROOM_SEAT_URL)
    Observable<BaseBean> saveRoomSeatBook(@Field("roomId") String roomId,
                                          @Field("seatId") String seatId,
                                          @Field("telephone") String telephone,
                                          @Field("appointTime") String appointTime);

    // 房间取消预定
    @FormUrlEncoded
    @POST(RequestConstant.CANCEL_BOOK_ROOM_SEAT_URL)
    Observable<BaseBean> cancelRoomSeatBook(@Field("seatId") String seatId);

    // 获取房间统计信息
    @GET(RequestConstant.GET_ROOM_STATUS_STATISTICS)
    Observable<RoomStatisticResult> getRoomStatistics();


    // 获取房间设置信息
    @GET(RequestConstant.GET_ROOM_STATUS_SETTING)
    Observable<RoomSettingResult> getRoomSetting();
}
