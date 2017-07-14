package com.xmd.chat.event;

/**
 * Created by mo on 17-7-7.
 * 开始和某人聊天
 */

public class EventStartChatActivity {
    private String remoteChatId;

    public EventStartChatActivity(String remoteChatId) {
        this.remoteChatId = remoteChatId;
    }

    public String getRemoteChatId() {
        return remoteChatId;
    }
}
