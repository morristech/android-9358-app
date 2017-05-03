package com.xmd.technician.event;

/**
 * Created by heyangya on 17-2-8.
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
}
