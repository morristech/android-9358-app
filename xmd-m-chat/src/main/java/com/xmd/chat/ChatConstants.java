package com.xmd.chat;

/**
 * Created by heyangya on 17-6-7.
 * 聊天常量定义
 */

public class ChatConstants {
    //时间显示间隔
    public static final int TIME_SHOW_INTERVAL = 5 * 60 * 1000;

    //角色定义
    public static final String CHAT_ROLE_USER = "user";
    public static final String CHAT_ROLE_TECH = "tech";
    public static final String CHAT_ROLE_MGR = "manager";

    //视图定义
    public static final int CHAT_VIEW_DIRECT_SEND = 0;
    public static final int CHAT_VIEW_DIRECT_RECEIVE = 1;
    public static final int CHAT_ROW_VIEW_TEXT = 0x1;
    public static final int CHAT_ROW_VIEW_TYPE_ORDER = 0x10;
    public static final int CHAT_ROW_VIEW_TYPE_ORDER_REQUEST = 0x11;

    public static final int CHAT_ROW_VIEW_TYPE_MAX = 0x200;
}
