package com.xmd.technician.chat.event;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by heyangya on 17-4-21.
 */

public class EventReceiveMessage {
    private List<EMMessage> list;
    public EventReceiveMessage(){

    }

    public EventReceiveMessage(List<EMMessage> list) {
        this.list = list;
    }

    public List<EMMessage> getList() {
        return list;
    }
}
