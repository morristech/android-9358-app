package com.xmd.chat.event;

import java.util.List;

/**
 * Created by mo on 17-6-28.
 * 收到消息
 */

public class EventNewMessages<T> {
    private List<T> list;

    public EventNewMessages(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
