package com.xmd.chat.event;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by mo on 17-6-28.
 * 收到消息
 */

public class EventNewMessages {
    private List<EMMessage> list;

    public EventNewMessages(List<EMMessage> list) {
        this.list = list;
    }

    public List<EMMessage> getList() {
        return list;
    }

    public void setList(List<EMMessage> list) {
        this.list = list;
    }
}
