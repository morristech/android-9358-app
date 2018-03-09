package com.xmd.chat.xmdchat.present;

import android.content.Context;
import android.content.Intent;

import com.xmd.chat.MenuFactory;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.chat.view.ChatActivity;
import com.xmd.chat.xmdchat.contract.XmdChatInterface;

/**
 * Created by Lhj on 18-1-24.
 */

public class ImXmdChatPresent implements XmdChatInterface {
    private Context context;

    @Override
    public void init(Context context, String appKey, boolean debug, MenuFactory menuFactory) {
        this.context = context;
    }

    @Override
    public void loadConversation() {

    }

    @Override
    public void onStartChat(EventStartChatActivity event) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_CHAT_ID, event.getRemoteChatId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getTotalUnreadCount() {
        return 0;
    }

    @Override
    public boolean isOnline() {
        return ImChatAccountManagerPresent.getInstance().userIsOnline();
    }
}
