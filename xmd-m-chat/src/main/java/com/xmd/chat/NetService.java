package com.xmd.chat;

import com.xmd.app.constants.HttpRequestConstant;
import com.xmd.chat.beans.CreditGift;
import com.xmd.chat.beans.DiceGameRequestResult;
import com.xmd.chat.beans.DiceGameResult;
import com.xmd.chat.beans.DiceGameSetting;
import com.xmd.chat.beans.FastReplySetting;
import com.xmd.chat.beans.Journal;
import com.xmd.chat.beans.Location;
import com.xmd.chat.beans.MarketingCategory;
import com.xmd.chat.beans.ResultOnceCard;
import com.xmd.m.network.BaseBean;

import java.util.List;

import retrofit2.http.Body;
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
    @GET(HttpRequestConstant.URL_CHAT_POSITION_CLUB)
    Observable<BaseBean<Location>> getClubLocation();

    //电子期刊分享数据列表
    @GET(HttpRequestConstant.URL_CHAT_JOURNAL_LIST)
    Observable<BaseBean<List<Journal>>> listShareJournal(@Query("clubId") String clubId,
                                                         @Query("page") String page,
                                                         @Query("pageSize") String pageSize);

    //次卡分享数据列表
    @GET(HttpRequestConstant.URL_CHAT_ACTIVITY_LIST)
    Observable<BaseBean<ResultOnceCard>> listOnceCards(@Query("clubId") String clubId,
                                                       @Query("isShare") String isShare,
                                                       @Query("page") String page,
                                                       @Query("pageSize") String pageSize);

    //营销活动列表
    @GET(HttpRequestConstant.URL_CHAT_MARKETING_ITEM_LIST)
    Observable<BaseBean<List<MarketingCategory>>> listMarketing();

    //积分礼物列表
    @GET(HttpRequestConstant.URL_CHAT_CREDIT_GIFT_LIST)
    Observable<BaseBean<List<CreditGift>>> listCreditGift();

    /**
     * processType: accept 接受订单  reject 拒绝订单
     * id: 订单id
     */
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_CHAT_ORDER_MANAGE)
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
    @POST(HttpRequestConstant.URL_CHAT_MARK_CHAT_TO_USER)
    Observable<BaseBean> notifyServerChatMessage(@Field("currentChatId") String currentChatId,
                                                 @Field("currentUserType") String currentUserType,
                                                 @Field("friendChatId") String friendChatId,
                                                 @Field("friendUserType") String friendUserType,
                                                 @Field("msgId") String msgId,
                                                 @Field("msgType") String msgType,
                                                 @Field("msgContent") String msgContent);

    //获取快速回复
    @GET(HttpRequestConstant.URL_CHAT_SETTING_FAST_REPLY)
    Observable<BaseBean<FastReplySetting>> getFastReplySetting();

    //保存快速回复
    @POST(HttpRequestConstant.URL_CHAT_SETTING_FAST_REPLY)
    Observable<BaseBean> setFastReplySetting(@Body FastReplySetting setting);

    /**
     * 获取游戏设定
     */
    @GET(HttpRequestConstant.URL_CHAT_CREDIT_GAME_SETTING)
    Observable<BaseBean<DiceGameSetting>> getGameSetting();

    /**
     * 发起骰子游戏邀请
     *
     * @param emchatId 对方的聊天id
     * @param clubId   会所id
     * @param amount   积分值
     */
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_CHAT_GAME_DICE_SUBMIT)
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
    @POST(HttpRequestConstant.URL_CHAT_GAME_DICE_ACCEPT)
    Observable<BaseBean<DiceGameResult>> diceGamePlayOrCancel(@Field("gameId") String gameId,
                                                              @Field("status") String status);

    /**
     * 获取是否有在线的邀请有礼活动
     */
    @GET(HttpRequestConstant.URL_CHAT_INVITE_ENABLE)
    Observable<BaseBean> getInviteEnable(@Query("clubId") String clubId);

    /**
     * 统计技师分享数据
     */
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_CHAT_SHARE_COUNT_UPDATE)
    Observable<BaseBean> updateTechShareCount(@Field("actId") String actId,
                                              @Field("type") String type);
}
