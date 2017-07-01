package com.xmd.chat;

import com.hyphenate.chat.EMMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.view.BaseChatRowData;
import com.xmd.chat.view.ChatRowDataText;
import com.xmd.chat.view.ChatRowDataUnSupport;

import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_DEFAULT;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TYPE_ORDER;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TYPE_ORDER_REQUEST;

/**
 * Created by heyangya on 17-6-7.
 * 聊天视图工厂
 */

public class ChatRowViewFactory {
    public static int getViewType(ChatMessage chatMessage) {
        int inc = chatMessage.getEmMessage().direct() == EMMessage.Direct.RECEIVE ?
                ChatConstants.CHAT_VIEW_RECEIVE_INC : ChatConstants.CHAT_VIEW_SEND_INC;
        switch (chatMessage.getMsgType()) {
            case ChatMessage.MSG_TYPE_ORDER_START:
            case ChatMessage.MSG_TYPE_ORDER_REFUSE:
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                return CHAT_ROW_VIEW_TYPE_ORDER + inc;
            case ChatMessage.MSG_TYPE_ORDER_REQUEST:
                return CHAT_ROW_VIEW_TYPE_ORDER_REQUEST + inc;
            default:
                return CHAT_ROW_VIEW_DEFAULT + inc;
        }
    }

    public static int sendType(int baseType) {
        return baseType + ChatConstants.CHAT_VIEW_SEND_INC;
    }

    public static int receiveType(int baseType) {
        return baseType + ChatConstants.CHAT_VIEW_RECEIVE_INC;
    }

    public static BaseChatRowData createView(ChatMessage message) {
        switch (message.getMsgType()) {
            case ChatMessage.MSG_TYPE_ORIGIN_TXT:
                return new ChatRowDataText(message);
            default:
                return new ChatRowDataUnSupport(message);
        }
    }
}
