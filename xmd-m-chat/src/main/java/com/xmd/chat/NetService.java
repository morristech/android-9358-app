package com.xmd.chat;

import com.xmd.chat.beans.Journal;
import com.xmd.chat.beans.Location;
import com.xmd.chat.beans.OnceCardResult;
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

    //电子期刊分享数据列表
    @GET("/spa-manager/api/v1/techshare/journalListDetail")
    Observable<BaseBean<List<Journal>>> listShareJournal(@Query("clubId") String clubId,
                                                         @Query("page") String page,
                                                         @Query("pageSize") String pageSize);

    //次卡分享数据列表
    @GET("/spa-manager/api/v2/club/item_card/activity/list")
    Observable<BaseBean<OnceCardResult>> listOnceCards(@Query("clubId") String clubId,
                                                       @Query("isShare") String isShare,
                                                       @Query("page") String page,
                                                       @Query("pageSize") String pageSize);

//    @GET("/spa-manager/api/v1/techshare/darwActListDetail")
//    Observable<BaseBean<Journal>> listClubActivities();
}
