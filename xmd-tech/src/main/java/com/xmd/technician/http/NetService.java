package com.xmd.technician.http;

import com.xmd.app.net.BaseBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by mo on 17-6-22.
 * 网络接口
 */

public interface NetService {
    @POST("/spa-manager/api/v2/tech/customer/service/status")
    @FormUrlEncoded
    Observable<BaseBean> changeCustomerStatus(@Field("status") String status);
}
