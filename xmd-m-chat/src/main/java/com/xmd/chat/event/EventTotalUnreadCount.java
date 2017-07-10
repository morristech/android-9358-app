package com.xmd.chat.event;

/**
 * Created by mo on 17-6-28.
 * 未读消息总数
 */

public class EventTotalUnreadCount {
    private int count;

    public EventTotalUnreadCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
