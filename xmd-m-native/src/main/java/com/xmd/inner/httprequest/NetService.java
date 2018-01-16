package com.xmd.inner.httprequest;


import com.xmd.inner.bean.NativeCreateBill;
import com.xmd.inner.bean.NativeUpdateBill;
import com.xmd.inner.httprequest.response.CreateSeatOrderResult;
import com.xmd.inner.httprequest.response.HaveIdentifyResult;
import com.xmd.inner.httprequest.response.OrderItemUpdateResult;
import com.xmd.inner.httprequest.response.OrderTimeListResult;
import com.xmd.inner.httprequest.response.ProjectListAvailableResult;
import com.xmd.inner.httprequest.response.RoomOrderInfoResult;
import com.xmd.inner.httprequest.response.RoomSeatListResult;
import com.xmd.inner.httprequest.response.RoomStatisticResult;
import com.xmd.inner.httprequest.response.TechnicianListResult;
import com.xmd.inner.httprequest.response.UserIdentifyListResult;
import com.xmd.m.network.BaseBean;

import java.util.List;

import retrofit2.http.Body;
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

    /**
     * 获取可用项目列表
     */
    @GET(RequestConstant.PROJECT_LIST_AVAILABLE)
    Observable<ProjectListAvailableResult> getAvailableProjectList();

    /**
     * 获取所有技师列表
     */
    @GET(RequestConstant.TECHNICIAN_LIST)
    Observable<TechnicianListResult> getAllTechnicianList(@Query("role") String role,
                                                          @Query("status") String status);

    /**
     * 获取闲技师列表
     */
    @GET(RequestConstant.TECHNICIAN_LIST)
    Observable<TechnicianListResult> getFreeTechnicianList(@Query("role") String role);

    /**
     * 获取上钟类别
     */
    @GET(RequestConstant.NATIVE_ORDER_ITEM_BELL_LIST)
    Observable<OrderTimeListResult> getOrderItemList();

    /**
     * 创建订单
     */
    @POST(RequestConstant.ORDER_BATCH_SAVE)
    Observable<CreateSeatOrderResult> createSeatOrder(@Body List<NativeCreateBill> data);

    /**
     * 判断手牌是否为必须
     */
    @GET(RequestConstant.USER_IDENTIFY_HAVE)
    Observable<HaveIdentifyResult> userIdentifyHave();

    /**
     * 获取手牌列表
     */
    @GET(RequestConstant.USER_IDENTIFY_LIST)
    Observable<UserIdentifyListResult> getUserIdentifyList(@Query("status") String status);

    /**
     * 更新订单信息
     */
    @POST(RequestConstant.NATIVE_ORDER_ITEM_UPDATE)
    Observable<OrderItemUpdateResult> updateOrderItem(@Body NativeUpdateBill data);

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

    // 房间座位
    @FormUrlEncoded
    @POST(RequestConstant.GET_ROOM_SEAT_INFO_LIST)
    Observable<RoomSeatListResult> getRoomSeatInfoList(@Field("status") String status,
                                                       @Field("roomName") String roomName);

    // 房间取消预定
    @FormUrlEncoded
    @POST(RequestConstant.CANCEL_BOOK_ROOM_SEAT_URL)
    Observable<BaseBean> cancelRoomSeatBook(@Field("seatId") String seatId);

    // 获取房间统计信息
    @GET(RequestConstant.GET_ROOM_STATUS_STATISTICS)
    Observable<RoomStatisticResult> getRoomStatistics();
}
