package com.xmd.chat.event;

import com.xmd.chat.viewmodel.ChatRowViewModel;

/**
 * Created by mo on 17-7-7.
 * 删除消息事件
 */

public class EventDeleteMessage {
    private ChatRowViewModel chatRowViewModel;

    public EventDeleteMessage(ChatRowViewModel chatRowViewModel) {
        this.chatRowViewModel = chatRowViewModel;
    }

    public ChatRowViewModel getChatRowViewModel() {
        return chatRowViewModel;
    }
}
