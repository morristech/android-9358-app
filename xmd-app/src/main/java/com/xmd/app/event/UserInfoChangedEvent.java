package com.xmd.app.event;

/**
 * Created by Lhj on 17-11-28.
 */

public class UserInfoChangedEvent {

    public String userHeadUrl;

    public UserInfoChangedEvent(String userHeadUrl) {
        this.userHeadUrl = userHeadUrl;
    }
}
