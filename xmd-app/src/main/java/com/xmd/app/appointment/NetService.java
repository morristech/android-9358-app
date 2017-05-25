package com.xmd.app.appointment;

import com.xmd.app.appointment.beans.TechnicianListResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by heyangya on 17-5-25.
 * 网络接口
 */

public interface NetService {
    /**
     * @param itemId   项目ID	string	非必填
     * @param sortId   排序ID	string	非必填，0-按星级；1-按评论数排序,
     * @param status   技师状态	string	非必填,free-只查空闲
     * @param techName 技师名称	string	非必填
     */
    @GET("/spa-manager/api/v2/tech/order/technician/list")
    Observable<TechnicianListResult> getTechnicianList(@Query("itemId") String itemId,
                                                       @Query("sortId") String sortId,
                                                       @Query("status") String status,
                                                       @Query("techName") String techName);
}
