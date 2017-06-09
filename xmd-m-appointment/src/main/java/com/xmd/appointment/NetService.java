package com.xmd.appointment;

import com.xmd.app.net.BaseBean;
import com.xmd.appointment.beans.AppointmentSettingResult;
import com.xmd.appointment.beans.ServiceListResult;
import com.xmd.appointment.beans.TechnicianListResult;

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
     * @param itemId   项目ID	string	非必填
     * @param sortId   排序ID	string	非必填，0-按星级；1-按评论数排序,
     * @param status   技师状态	string	非必填,free-只查空闲
     * @param techName 技师名称	string	非必填
     */
    @GET("/spa-manager/api/v2/tech/order/technician/list")
    Observable<TechnicianListResult> getTechnicianList(@Query("page") int page,
                                                       @Query("pageSize") int pageSize,
                                                       @Query("itemId") String itemId,
                                                       @Query("sortId") String sortId,
                                                       @Query("status") String status,
                                                       @Query("techName") String techName);


    /**
     * 获取项目列表
     */
    @GET("/spa-manager/api/v2/tech/order/serviceItem/list")
    Observable<ServiceListResult> getServiceList();

    /**
     * 获取更多预约信息
     *
     * @param techId 被预约技师ID	string	非必填
     * @param userId 预约用户ID	string	必填
     */
    @GET("/spa-manager/api/v2/tech/order/edit")
    Observable<AppointmentSettingResult> getAppointmentExt(@Query("techId") String techId,
                                                           @Query("userId") String userId);

    /**
     * 生成预约
     *
     * @param customerName    顾客名称	string	必填
     * @param customerPhone   顾客手机	string	必填
     * @param dateId          预约指定的天	number	必填, 从0开始 ， 0代表当天， 1代表明天，以处类推
     * @param time            预约时间	string	必填, 格式HH:mm
     * @param techId          被预约技师ID	string	非必填
     * @param userId          预约用户ID	string	非必填
     * @param serviceId       预约项目ID	string	非必填
     * @param serviceDuration 预约服务时长	number	单位为分
     */
    @POST("/spa-manager/api/v2/tech/order/save")
    @FormUrlEncoded
    Observable<BaseBean> submitAppointment(@Field("customerName") String customerName,
                                           @Field("phoneNum") String customerPhone,
                                           @Field("appointTime") Long time,
                                           @Field("techId") String techId,
                                           @Field("userId") String userId,
                                           @Field("itemId") String serviceId,
                                           @Field("serviceDuration") Integer serviceDuration);
}
