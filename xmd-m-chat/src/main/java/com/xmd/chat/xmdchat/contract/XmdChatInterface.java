package com.xmd.chat.xmdchat.contract;

import android.content.Context;

import com.xmd.chat.MenuFactory;
import com.xmd.chat.event.EventStartChatActivity;

/**
 * Created by Lhj on 18-1-24.
 */

public interface XmdChatInterface {

    void init(Context context, String appKey, boolean debug, MenuFactory menuFactory);

    void loadConversation();

    void onStartChat(EventStartChatActivity event);

    int getTotalUnreadCount();

    boolean isOnline();
}
