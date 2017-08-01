package com.xmd.app;

import com.xmd.app.beans.UserCredit;
import com.xmd.app.user.User;
import com.xmd.m.network.BaseBean;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by mo on 17-7-21.
 * 一些公用的网络接口
 */

public interface CommonNetService {
    @POST("/spa-manager/api/v2/app/heartbeat")
    @FormUrlEncoded
    Observable<BaseBean> reportAlive(@Field("token") String token,
                                     @Field("deviceId") String deviceId);

    /**
     * 获取用户基本信息和联系权限
     */
    @GET("/spa-manager/api/v2/chat/userInfo/{id}")
    Observable<BaseBean<User>> getUserBaseInfo(@Path("id") String id,
                                               @Query("idType") String idType,
                                               @Query("getPermission") boolean getPermission);

    /**
     * 获取用户积分
     */
    @GET("/spa-manager/api/v2/credit/user/account")
    Observable<BaseBean<List<UserCredit>>> getUserCredit(@Query("clubId") String clubId,
                                                         @Query("page") int page,
                                                         @Query("pageSize") int pageSize);
}
