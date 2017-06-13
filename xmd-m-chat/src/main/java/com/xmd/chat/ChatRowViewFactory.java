package com.xmd.chat;

import com.hyphenate.chat.EMMessage;
import com.xmd.chat.message.ChatMessage;

import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_DEFAULT;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TYPE_ORDER;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TYPE_ORDER_REQUEST;

/**
 * Created by heyangya on 17-6-7.
 * 聊天视图工厂
 */

public class ChatRowViewFactory {
    public static int getViewType(ChatMessage chatMessage) {
        int base = chatMessage.getEmMessage().direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
        switch (chatMessage.getMsgType()) {
            case ChatMessage.MSG_TYPE_ORDER_START:
            case ChatMessage.MSG_TYPE_ORDER_REFUSE:
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                return CHAT_ROW_VIEW_TYPE_ORDER + base;
            case ChatMessage.MSG_TYPE_ORDER_REQUEST:
                return CHAT_ROW_VIEW_TYPE_ORDER_REQUEST + base;
            default:
                return CHAT_ROW_VIEW_DEFAULT + base;
        }
    }
}
