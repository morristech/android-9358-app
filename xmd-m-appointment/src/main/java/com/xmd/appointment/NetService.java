package com.xmd.appointment;

import com.xmd.app.constants.HttpRequestConstant;
import com.xmd.appointment.beans.AppointmentSettingResult;
import com.xmd.appointment.beans.ServiceListResult;
import com.xmd.appointment.beans.TechnicianListResult;
import com.xmd.m.network.BaseBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by heyangya on 17-5-25.
 * 网络接口
 */

public interface NetService {
    /**
     * 获取技师列表
     *
     * @param itemId   项目ID	string	非必填
     * @param sortId   排序ID	string	非必填，0-按星级；1-按评论数排序,
     * @param status   技师状态	string	非必填,free-只查空闲
     * @param techName 技师名称	string	非必填
     */
    @GET(HttpRequestConstant.URL_ORDER_TECHNICIAN_LIST)
    Observable<TechnicianListResult> getTechnicianList(@Query("page") int page,
                                                       @Query("pageSize") int pageSize,
                                                       @Query("itemId") String itemId,
                                                       @Query("sortId") String sortId,
                                                       @Query("status") String status,
                                                       @Query("techName") String techName);


    /**
     * 获取项目列表
     */
    @GET(HttpRequestConstant.URL_ORDER_SERVICE_ITEM_LIST)
    Observable<ServiceListResult> getServiceList();

    /**
     * 获取更多预约信息
     *
     * @param techId 被预约技师ID	string	非必填
     * @param userId 预约用户ID	string	必填
     */
    @GET(HttpRequestConstant.URL_TECH_ORDER_EDIT)
    Observable<AppointmentSettingResult> getAppointmentExt(@Query("techId") String techId,
                                                           @Query("userId") String userId);

    /**
     * 生成或者查询预约
     * @param orderId 订单ID
     * @param customerName 客户名 必填
     * @param customerPhone 客户手机 必填
     * @param time 预约时间 utc_ms 必填
     * @param userId 预约用户ID
     * @param cardNo
     * @param techId 预约技师ID
     * @param serviceId 项目ID
     * @param serviceDuration 项目时长
     * @return 订单ID
     */
    @POST(HttpRequestConstant.URL_TECH_ORDER_SAVE)
    @FormUrlEncoded
    Observable<BaseBean> submitAppointment(
            @Field("orderId") String orderId,
            @Field("customerName") String customerName,
            @Field("phoneNum") String customerPhone,
            @Field("appointTime") Long time,
            @Field("userId") String userId,
            @Field("cardNo") String cardNo,
            @Field("techId") String techId,
            @Field("itemId") String serviceId,
            @Field("serviceDuration") String serviceDuration);
}
