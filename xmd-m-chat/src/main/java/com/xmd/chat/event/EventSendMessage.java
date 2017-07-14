package com.xmd.chat.event;

import com.xmd.chat.message.ChatMessage;

/**
 * Created by mo on 17-7-10.
 * 发送消息事件
 */

public class EventSendMessage {
    private ChatMessage chatMessage;

    public EventSendMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
