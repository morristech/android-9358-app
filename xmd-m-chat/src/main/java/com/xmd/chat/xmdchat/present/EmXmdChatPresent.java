package com.xmd.chat.xmdchat.present;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.chat.MenuFactory;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.chat.view.ChatActivity;
import com.xmd.chat.xmdchat.contract.XmdChatInterface;

/**
 * Created by Lhj on 18-1-24.
 */

public class EmXmdChatPresent implements XmdChatInterface {

    private Context context;

    @Override
    public void init(Context context, String appKey, boolean debug, MenuFactory menuFactory) {
        XLogger.i("--聊天系统初始化--debug=" + debug + "--appKey=" + appKey);
        if (!DiskCacheManager.isInit()) {
            throw new RuntimeException("dependency DiskCacheManager, but not init");
        }
        this.context = context;
        EMOptions options = new EMOptions();
        options.setAppKey(appKey);
        EMClient.getInstance().init(context, options);
        EMClient.getInstance().setDebugMode(debug);
    }

    @Override
    public void loadConversation() {
        EMClient.getInstance().chatManager().loadAllConversations();
        EMClient.getInstance().groupManager().loadAllGroups();
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
      return   EMClient.getInstance().chatManager().getUnreadMessageCount();
    }

    @Override
    public boolean isOnline() {
        return EMClient.getInstance().isConnected();
    }
}
