package com.xmd.app.event;

import com.xmd.app.user.User;

/**
 * Created by heyangya on 17-5-24.
 * 登录事件，每次重新启动时也算一次登录
 */

public class EventLogin {
    private String token;
    private User user;

    public EventLogin(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return user.getId();
    }

    public String getChatId() {
        return user.getChatId();
    }


    public String getChatPassword() {
        return user.getChatPassword();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "EventLogin{" +
                "token='" + token + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
