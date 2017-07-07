package com.xmd.chat.event;

import com.xmd.chat.viewmodel.ChatRowViewModel;

/**
 * Created by mo on 17-7-7.
 * 撤回事件
 */

public class EventRevokeMessage {
    private ChatRowViewModel chatRowViewModel;

    public EventRevokeMessage(ChatRowViewModel chatRowViewModel) {
        this.chatRowViewModel = chatRowViewModel;
    }

    public ChatRowViewModel getChatRowViewModel() {
        return chatRowViewModel;
    }
}
