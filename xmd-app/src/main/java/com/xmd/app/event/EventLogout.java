package com.xmd.app.event;

/**
 * Created by heyangya on 17-5-24.
 * 登出事件
 */

public class EventLogout {
    private String token;
    private String userId;

    public EventLogout(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "EventLogout{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
