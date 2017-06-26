package com.xmd.m.notify;

import com.xmd.m.network.BaseBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by mo on 17-6-26.
 * 网络接口
 */

public interface NetService {
    @FormUrlEncoded
    @POST("/spa-manager/api/v2/push/clientid")
    Observable<BaseBean> bindGetuiClientId(@Field("token") String token,
                                           @Field("userId") String userId,
                                           @Field("userType") String userType,
                                           @Field("appType") String appType,
                                           @Field("clientId") String clientId,
                                           @Field("secret") String secret);
}
