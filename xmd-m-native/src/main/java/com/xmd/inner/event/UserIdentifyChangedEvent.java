package com.xmd.inner.event;

/**
 * Created by Lhj on 17-12-5.
 */

public class UserIdentifyChangedEvent {

    private String userIdentify;

    public String getUserIdentify() {
        return userIdentify;
    }

    public void setUserIdentify(String userIdentify) {
        this.userIdentify = userIdentify;
    }
    public UserIdentifyChangedEvent(String userIdentify) {
        this.userIdentify = userIdentify;
    }
}
