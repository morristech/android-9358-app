package com.xmd.app.event;

import com.xmd.app.user.User;

/**
 * Created by mo on 17-7-25.
 * 头像点击事件
 */

public class EventClickAvatar {
    private User user;

    public EventClickAvatar(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
