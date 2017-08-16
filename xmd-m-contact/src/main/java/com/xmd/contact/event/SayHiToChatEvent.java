package com.xmd.contact.event;

/**
 * Created by Lhj on 17-7-31.
 */

public class SayHiToChatEvent {
    public String emChatId;
    public int position;

    public SayHiToChatEvent(String emChatId,int position) {
        this.emChatId = emChatId;
        this.position = position;
    }
}
