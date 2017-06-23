package com.xmd.m.network;

import com.shidou.commonlibrary.CommenEvent;

/**
 * Created by mo on 17-6-23.
 * token失效事件
 */

public class EventTokenExpired extends CommenEvent {
    private String reason;

    public EventTokenExpired(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
