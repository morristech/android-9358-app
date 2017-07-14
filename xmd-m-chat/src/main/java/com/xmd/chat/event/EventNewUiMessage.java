package com.xmd.chat.event;

import com.xmd.chat.message.ChatMessage;

/**
 * Created by mo on 17-7-8.
 * 新增消息到ui界面
 */

public class EventNewUiMessage {
    private ChatMessage chatMessage;

    public EventNewUiMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
