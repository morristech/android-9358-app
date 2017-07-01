package com.xmd.chat;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.viewmodel.BaseChatRowViewModel;
import com.xmd.chat.viewmodel.ChatRowViewModelText;

import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TEXT;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TYPE_ORDER;
import static com.xmd.chat.ChatConstants.CHAT_ROW_VIEW_TYPE_ORDER_REQUEST;

/**
 * Created by heyangya on 17-6-7.
 * 聊天视图工厂
 */

public class ChatRowViewFactory {
    //获取viewType
    public static int getViewType(ChatMessage chatMessage) {
        int baseType;
        switch (chatMessage.getMsgType()) {
            case ChatMessage.MSG_TYPE_ORIGIN_TXT:
                baseType = CHAT_ROW_VIEW_TEXT;
                break;
            case ChatMessage.MSG_TYPE_ORDER_START:
            case ChatMessage.MSG_TYPE_ORDER_REFUSE:
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                baseType = CHAT_ROW_VIEW_TYPE_ORDER;
                break;
            case ChatMessage.MSG_TYPE_ORDER_REQUEST:
                baseType = CHAT_ROW_VIEW_TYPE_ORDER_REQUEST;
                break;
            default:
                baseType = CHAT_ROW_VIEW_TEXT;
        }

        return chatMessage.getEmMessage().direct() == EMMessage.Direct.RECEIVE ? receiveType(baseType) : sendType(baseType);
    }

    public static int sendType(int baseType) {
        return baseType | ChatConstants.CHAT_VIEW_DIRECT_SEND << 31;
    }

    public static int receiveType(int baseType) {
        return baseType | ChatConstants.CHAT_VIEW_DIRECT_RECEIVE << 31;
    }

    public static boolean isSendViewType(int viewType) {
        return (viewType & (0x1 << 31)) == ChatConstants.CHAT_VIEW_DIRECT_SEND;
    }

    public static View createView(ViewGroup parent, int viewType) {
        int baseType = viewType & ~(0x1 << 31);
        switch (baseType) {
            default:
                return ChatRowViewModelText.createView(parent);
        }
    }

    public static BaseChatRowViewModel createViewModel(ChatMessage message) {
        switch (message.getMsgType()) {
            case ChatMessage.MSG_TYPE_ORIGIN_TXT:
                return new ChatRowViewModelText(message);
            default:
                return new ChatRowViewModelText(message);
        }
    }
}
