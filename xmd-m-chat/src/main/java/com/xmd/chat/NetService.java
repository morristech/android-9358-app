package com.xmd.chat;

import com.xmd.chat.beans.Journal;
import com.xmd.chat.beans.Location;
import com.xmd.m.network.BaseBean;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
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


    @GET("/spa-manager/api/v1/techshare/journalListDetail")
    Observable<BaseBean<List<Journal>>> listClubJournal(@Query("clubId") String clubId,
                                                        @Query("page") String page,
                                                        @Query("pageSize") String pageSize);

    @GET("/spa-manager/api/v1/techshare/darwActListDetail")
    Observable<BaseBean<Journal>> listClubActivities();
}
