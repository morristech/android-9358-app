package com.xmd.app.event;

/**
 * Created by heyangya on 17-5-24.
 * token失效事件
 */

public class EventTokenExpired {
    private String expiredReason;

    public EventTokenExpired(String expiredReason) {
        this.expiredReason = expiredReason;
    }

    public String getExpiredReason() {
        return expiredReason;
    }

    public void setExpiredReason(String expiredReason) {
        this.expiredReason = expiredReason;
    }
}
