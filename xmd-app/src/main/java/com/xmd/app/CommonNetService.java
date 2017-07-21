package com.xmd.app;

import com.xmd.app.beans.UserCredit;
import com.xmd.m.network.BaseBean;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by mo on 17-7-21.
 * 一些公用的网络接口
 */

public interface CommonNetService {

    /**
     * 获取用户积分
     */
    @GET("/spa-manager/api/v2/credit/user/account")
    Observable<BaseBean<List<UserCredit>>> getUserCredit(@Query("clubId") String clubId,
                                                         @Query("page") int page,
                                                         @Query("pageSize") int pageSize);
}
