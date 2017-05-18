package com.xmd.technician.chat.event;

/**
 * Created by heyangya on 17-4-1.
 */

public class EventEmChatLoginSuccess {
    public boolean loginSuccess; //是否登录成功
    public EventEmChatLoginSuccess(Boolean loginSuccess){
        this.loginSuccess = loginSuccess;
    }

}
