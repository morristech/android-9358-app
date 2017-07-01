package com.xmd.chat.event;

import com.xmd.chat.viewmodel.ConversationViewModel;

/**
 * Created by mo on 17-6-30.
 * 删除会话事件
 */

public class EventDeleteConversation {
    private int position;
    private ConversationViewModel data;

    public EventDeleteConversation(int position, ConversationViewModel data) {
        this.position = position;
        this.data = data;
    }

    public ConversationViewModel getData() {
        return data;
    }

    public int getPosition() {
        return position;
    }
}
