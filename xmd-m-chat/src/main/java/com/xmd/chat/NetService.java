package com.xmd.chat;

import com.xmd.chat.beans.CreditGift;
import com.xmd.chat.beans.DiceGameRequestResult;
import com.xmd.chat.beans.DiceGameResult;
import com.xmd.chat.beans.DiceGameSetting;
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

    /**
     * 通知用户有消息
     *
     * @param currentChatId   当前用户chatId
     * @param currentUserType 当前用户类型 user,tech,manager
     * @param friendChatId    对方chatId
     * @param friendUserType  对方用户类型
     * @param msgId           消息id
     * @param msgType         消息类型  text, paid_coupon ..等等
     */
    @FormUrlEncoded
    @POST("/spa-manager/api/v1/emchat/markchattouser")
    Observable<BaseBean> notifyServerChatMessage(@Field("currentChatId") String currentChatId,
                                                 @Field("currentUserType") String currentUserType,
                                                 @Field("friendChatId") String friendChatId,
                                                 @Field("friendUserType") String friendUserType,
                                                 @Field("msgId") String msgId,
                                                 @Field("msgType") String msgType);


    /**
     * 获取游戏设定
     */
    @GET("/spa-manager/api/v2/credit/game/setting")
    Observable<BaseBean<DiceGameSetting>> getGameSetting();

    /**
     * 发起骰子游戏邀请
     *
     * @param emchatId 对方的聊天id
     * @param clubId   会所id
     * @param amount   积分值
     */
    @FormUrlEncoded
    @POST("/spa-manager/api/v2/credit/game/dice/submit")
    Observable<BaseBean<DiceGameRequestResult>> diceGameRequest(@Field("emchatId") String emchatId,
                                                                @Field("clubId") String clubId,
                                                                @Field("amount") int amount);

    /**
     * 开始玩/取消 骰子游戏，返回结果
     *
     * @param gameId 游戏ID
     * @param status 状态
     * @return 游戏结果
     */
    @FormUrlEncoded
    @POST("/spa-manager/api/v2/credit/game/dice/accept")
    Observable<BaseBean<DiceGameResult>> diceGamePlayOrCancel(@Field("gameId") String gameId,
                                                              @Field("status") String status);
}
