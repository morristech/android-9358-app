package com.xmd.chat;

import com.hyphenate.chat.EMMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.CustomLocationMessage;
import com.xmd.chat.message.OrderChatMessage;
import com.xmd.chat.message.ShareChatMessage;

/**
 * Created by heyangya on 17-6-7.
 * 消息转换工厂  环信消息 <---> 自定义消息
 */

public class ChatMessageFactory {
    /**
     * 使用已有EMMessage创建一个合适的ChatMessage
     */
    public static ChatMessage get(EMMessage message) {
        String msgType = ChatMessage.getMsgType(message);
        switch (msgType) {
            case ChatMessage.MSG_TYPE_CLUB_LOCATION:
                return new CustomLocationMessage(message);
            case ChatMessage.MSG_TYPE_ORDER_START:
            case ChatMessage.MSG_TYPE_ORDER_REFUSE:
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                return new OrderChatMessage(message);
            case ChatMessage.MSG_TYPE_JOURNAL:
            case ChatMessage.MSG_TYPE_ONCE_CARD:
                return new ShareChatMessage(message);
            default:
                return new ChatMessage(message);
        }
    }
}
