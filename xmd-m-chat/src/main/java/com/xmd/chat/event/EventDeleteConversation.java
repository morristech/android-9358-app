package com.xmd.chat.event;

import com.xmd.chat.ConversationData;

/**
 * Created by mo on 17-6-30.
 * 删除会话事件
 */

public class EventDeleteConversation {
    private int position;
    private ConversationData data;

    public EventDeleteConversation(int position, ConversationData data) {
        this.position = position;
        this.data = data;
    }

    public ConversationData getData() {
        return data;
    }

    public int getPosition() {
        return position;
    }
}
