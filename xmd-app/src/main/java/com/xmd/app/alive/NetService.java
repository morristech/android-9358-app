package com.xmd.app.alive;

import com.xmd.app.beans.BaseBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by heyangya on 17-5-25.
 * 网络访问
 */

public interface NetService {
    @POST("/api/v2/app/heartbeat")
    @FormUrlEncoded
    Observable<BaseBean> reportAlive(@Field("token") String token,
                                     @Field("deviceId") String deviceId);
}
