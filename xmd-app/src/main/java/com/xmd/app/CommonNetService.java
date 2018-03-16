package com.xmd.app;

import com.xmd.app.beans.UserCredit;
import com.xmd.app.constants.HttpRequestConstant;
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
    @POST(HttpRequestConstant.URL_APP_HEART_BEAT)
    @FormUrlEncoded
    Observable<BaseBean> reportAlive(@Field("token") String token,
                                     @Field("deviceId") String deviceId);

    /**
     * 获取用户基本信息和联系权限
     */
    @GET(HttpRequestConstant.URL_CHAT_USER_INFO)
    Observable<BaseBean<User>> getUserBaseInfo(@Path("id") String id,
                                               @Query("idType") String idType,
                                               @Query("getPermission") boolean getPermission);

    /**
     * 获取用户积分
     */
    @GET(HttpRequestConstant.URL_CREDIT_USER_ACCOUNT)
    Observable<BaseBean<List<UserCredit>>> getUserCredit(@Query("clubId") String clubId,
                                                         @Query("page") int page,
                                                         @Query("pageSize") int pageSize);
}
