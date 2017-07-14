package com.xmd.chat;

import com.xmd.chat.beans.CreditGift;
import com.xmd.chat.beans.Journal;
import com.xmd.chat.beans.Location;
import com.xmd.chat.beans.MarketingCategory;
import com.xmd.chat.beans.ResultOnceCard;
import com.xmd.m.network.BaseBean;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    Observable<BaseBean<ResultOnceCard>> listOnceCards(@Query("clubId") String clubId,
                                                       @Query("isShare") String isShare,
                                                       @Query("page") String page,
                                                       @Query("pageSize") String pageSize);

    //营销活动列表
    @GET("/spa-manager/api/v2/tech/marketing_item/list")
    Observable<BaseBean<List<MarketingCategory>>> listMarketing();

    //积分礼物列表
    @GET("/spa-manager/api/v2/credit/gift/list")
    Observable<BaseBean<List<CreditGift>>> listCreditGift();

    /**
     * processType: accept 接受订单  reject 拒绝订单
     * id: 订单id
     */
    @FormUrlEncoded
    @POST("/spa-manager/api/v2/tech/profile/order/manage")
    Observable<BaseBean> manageOrder(@Field("processType") String processType,
                                     @Field("id") String id);
}
