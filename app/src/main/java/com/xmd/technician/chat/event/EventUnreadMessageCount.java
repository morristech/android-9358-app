package com.xmd.technician.chat.event;

/**
 * Created by heyangya on 17-4-21.
 */

public class EventUnreadMessageCount {
    private int unread;

    public EventUnreadMessageCount(int unread) {
        this.unread = unread;
    }

    public int getUnread() {
        return unread;
    }
}
