package com.xmd.chat.event;

import com.xmd.chat.viewmodel.ConversationViewModel;

/**
 * Created by mo on 17-7-10.
 * 未读数量变化
 */

public class EventUnreadCount {
    private ConversationViewModel conversationViewModel;

    public EventUnreadCount(ConversationViewModel conversationViewModel) {
        this.conversationViewModel = conversationViewModel;
    }

    public ConversationViewModel getConversationViewModel() {
        return conversationViewModel;
    }
}
