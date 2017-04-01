package com.xmd.technician.event;

/**
 * Created by heyangya on 17-4-1.
 */

public class EventEmChatLogin {
    private boolean loginSuccess;

    public EventEmChatLogin(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }
}
