package com.xmd.chat;

import com.xmd.chat.beans.Location;
import com.xmd.m.network.BaseBean;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by heyangya on 17-5-25.
 * 网络接口
 */

public interface NetService {
    /**
     * 获取会所位置
     */
    @GET("/spa-manager/api/v1/position/club")
    Observable<BaseBean<Location>> getClubLocation();
}
