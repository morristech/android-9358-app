package com.xmd.chat;

import com.xmd.chat.message.ChatMessage;

import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_DEFAULT;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TYPE_ORDER;

/**
 * Created by heyangya on 17-6-7.
 * 聊天视图工厂
 */

public class ChatRowViewFactory {
    public static int getViewType(ChatMessage chatMessage) {
        switch (chatMessage.getMsgType()) {
            case ChatMessage.MSG_TYPE_ORDER_START:
            case ChatMessage.MSG_TYPE_ORDER_REFUSE:
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                return CHAT_ROW_VIEW_TYPE_ORDER;
            default:
                return CHAT_ROW_VIEW_DEFAULT;
        }

    }
}
