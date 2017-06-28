package com.xmd.app.event;

/**
 * Created by heyangya on 17-5-24.
 * 登录事件，每次重新启动时也算一次登录
 */

public class EventLogin {
    private String token;
    private String userId;
    private String chatId;
    private String chatPassword;

    public EventLogin(String token, String userId) {
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

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatPassword() {
        return chatPassword;
    }

    public void setChatPassword(String chatPassword) {
        this.chatPassword = chatPassword;
    }

    @Override
    public String toString() {
        return "EventLogin{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
