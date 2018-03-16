package com.xmd.m.notify.push;

import com.xmd.app.constants.HttpRequestConstant;
import com.xmd.m.network.BaseBean;

import retrofit2.Call;
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
    @POST(HttpRequestConstant.URL_GETUI_BIND_CLIENT_ID)
    Call<BaseBean> bindGetuiClientId(@Field("token") String token,
                                     @Field("userId") String userId,
                                     @Field("userType") String userType,
                                     @Field("appType") String appType,
                                     @Field("clientId") String clientId,
                                     @Field("secret") String secret,
                                     @Field("targetType") String targetType);   // 对接新添加

    //解绑
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GETUI_UNBIND_CLIENT_ID)
    Observable<BaseBean> unbindGetuiClientId(@Field("userType") String userType,
                                             @Field("clientId") String clientId,
                                             @Field("targetType") String targetType);   //对接新添加
}
