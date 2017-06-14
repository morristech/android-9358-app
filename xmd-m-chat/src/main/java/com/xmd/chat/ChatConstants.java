package com.xmd.chat;

/**
 * Created by heyangya on 17-6-7.
 * 聊天常量定义
 */

public class ChatConstants {

    //角色定义
    public static final String CHAT_ROLE_USER = "user";
    public static final String CHAT_ROLE_TECH = "tech";
    public static final String CHAT_ROLE_MGR = "manager";

    //视图定义
    // 实际的视图类型是 view+direct  例如CHAT_ROW_VIEW_TYPE_ORDER+CHAT_VIEW_SEND_INC 是发送预约视图
    public static final int CHAT_VIEW_SEND_INC = 0;
    public static final int CHAT_VIEW_RECEIVE_INC = 1;
    public static final int CHAT_ROW_VIEW_DEFAULT = 0x100;
    public static final int CHAT_ROW_VIEW_TYPE_ORDER = 0x102;
    public static final int CHAT_ROW_VIEW_TYPE_ORDER_REQUEST = 0x104;

    public static final int CHAT_ROW_VIEW_TYPE_MAX = 0x200;
}
