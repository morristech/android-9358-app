package com.xmd.technician.clubinvite;

import com.xmd.m.network.BaseBean;
import com.xmd.technician.clubinvite.beans.ClubInvite;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by mo on 17-8-11.
 * 会所邀请
 */

public interface NetService {
    /**
     * 查询邀请列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GET("/spa-manager/api/v2/tech/club/position/invite/list")
    Observable<BaseBean<List<ClubInvite>>> listInvite(@Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 接受或者拒绝邀请
     *
     * @param inviteId
     * @param operate
     * @return
     */
    @POST("/spa-manager/api/v2/tech/club/position/invite")
    @FormUrlEncoded
    Observable<BaseBean<ClubInvite>> acceptOrRefuseInvite(@Field("inviteId") Long inviteId, @Field("operate") String operate);
}
