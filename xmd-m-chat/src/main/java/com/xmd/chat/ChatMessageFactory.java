package com.xmd.chat;

import com.hyphenate.chat.EMMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.OrderChatMessage;

/**
 * Created by heyangya on 17-6-7.
 * 消息转换工厂
 */

public class ChatMessageFactory {
    public static ChatMessage get(EMMessage message) {
        String msgType = message.getStringAttribute(ChatMessage.ATTRIBUTE_MESSAGE_TYPE, "");
        switch (msgType) {
            case ChatMessage.MSG_TYPE_ORDER_START:
            case ChatMessage.MSG_TYPE_ORDER_REFUSE:
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                return new OrderChatMessage(message, msgType);
            default:
                return new ChatMessage(message, msgType);
        }
    }
}
