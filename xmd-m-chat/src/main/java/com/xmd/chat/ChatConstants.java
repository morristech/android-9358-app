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

    public static final int CHAT_ROW_VIEW_TYPE_ORDER = 0x10; //订单
    public static final int CHAT_ROW_VIEW_TYPE_ORDER_REQUEST = 0x11;//求预约
    public static final int CHAT_ROW_VIEW_REWARD = 0x12;//用户打赏
    public static final int CHAT_ROW_VIEW_SHARE = 0x15; //分享

    public static final int CHAT_ROW_VIEW_TIP = 0x20;//提示

    public static final int CHAT_ROW_VIEW_TYPE_MAX = 0x200;
}
