package com.xmd.chat;

/**
 * Created by heyangya on 17-6-7.
 * 聊天常量定义
 */

public class ChatConstants {
    //时间显示间隔
    public static final int TIME_SHOW_INTERVAL = 5 * 60 * 1000;
    public static final long REVOKE_LIMIT_TIME = 2 * 60 * 1000;

    //角色定义
    public static final String CHAT_ROLE_USER = "user";
    public static final String CHAT_ROLE_TECH = "tech";
    public static final String CHAT_ROLE_MGR = "manager";

    //视图定义
    public static final int CHAT_VIEW_DIRECT_SEND = 0;
    public static final int CHAT_VIEW_DIRECT_RECEIVE = 1;
    public static final int CHAT_ROW_VIEW_TEXT = 0x1; //文本
    public static final int CHAT_ROW_VIEW_IMAGE = 0x2; //图片
    public static final int CHAT_ROW_VIEW_LOCATION = 0x3; //位置
    public static final int CHAT_ROW_VIEW_VOICE = 0x4;//语音

    public static final int CHAT_ROW_VIEW_TYPE_APPOINTMENT = 0x10; //预约消息
    public static final int CHAT_ROW_VIEW_TYPE_ORDER_REQUEST = 0x11;//求预约
    public static final int CHAT_ROW_VIEW_COUPON = 0x12; //优惠券
    public static final int CHAT_ROW_VIEW_CREDIT_GIFT = 0x13; //积分礼物
    public static final int CHAT_ROW_VIEW_NEW_ORDER = 0x14; //新订单
    public static final int CHAT_ROW_VIEW_SHARE = 0x15; //分享
    public static final int CHAT_ROW_VIEW_DICE_GAME_INVITE = 0x16; //发送邀请骰子游戏
    public static final int CHAT_ROW_VIEW_DICE_GAME_ACCEPT = 0x17; //收到邀请骰子游戏
    public static final int CHAT_ROW_VIEW_DICE_GAME_RESULT = 0x18; //游戏结果
    public static final int CHAT_ROW_VIEW_REWARD_REQUEST = 0x19; //求打赏
    public static final int CHAT_ROW_VIEW_REWARD = 0x20;//打赏
    public static final int CHAT_ROW_VIEW_TIP = 0x21;//提示
    public static final int CHAT_ROW_VIEW_EMPTY = 0x22;//空消息
    public static final int CHAT_ROW_VIEW_GROUP_IMAGE = 0x23;

    public static final int CHAT_ROW_VIEW_TYPE_MAX = 0x200;


    public static final String SP_AUDIO_MODE_SPEAKER = "sp_audio_mode_speaker"; //是否为扬声器模式
    public static final String SP_GAME_DICE_EXPIRE_TIME = "sp_game_dice_expire_time"; //游戏超时时间

    public static final String CACHE_CHAT_FAST_REPLY_SETTING = "cache_chat_fast_reply_setting";
}
