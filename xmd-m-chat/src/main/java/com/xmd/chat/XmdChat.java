package com.xmd.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.chat.view.ChatActivity;
import com.xmd.chat.view.ChatFastReplySettingActivity;
import com.xmd.m.network.EventTokenExpired;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by mo on 17-6-21.
 * 聊天模块
 */

public class XmdChat {
    public final static String TAG = "XmdChat";
    private static final XmdChat ourInstance = new XmdChat();

    public static XmdChat getInstance() {
        return ourInstance;
    }

    private XmdChat() {
    }

    private Context context;
    private MenuFactory menuFactory;


    public void init(Context context, String appKey, boolean debug, MenuFactory menuFactory) {
        XLogger.i("--聊天系统初始化--debug=" + debug + "--appKey=" + appKey);

        if (!DiskCacheManager.isInit()) {
            throw new RuntimeException("dependency DiskCacheManager, but not init");
        }

        context = context.getApplicationContext();
        this.context = context;

        EMOptions options = new EMOptions();
        options.setAppKey(appKey);
        EMClient.getInstance().init(context, options);
        EMClient.getInstance().setDebugMode(debug);

        loadConversation();

        ChatAccountManager.getInstance().init();
        ConversationManager.getInstance().init();
        MessageManager.getInstance().init();
        setMenuFactory(menuFactory);


        EventBusSafeRegister.register(this);
    }

    public void loadConversation() {
        EMClient.getInstance().chatManager().loadAllConversations();
        EMClient.getInstance().groupManager().loadAllGroups();
    }

    public MenuFactory getMenuFactory() {
        return menuFactory;
    }

    public void setMenuFactory(MenuFactory menuFactory) {
        this.menuFactory = menuFactory;
    }

    @Subscribe
    public void onStartChat(EventStartChatActivity event) {
        XLogger.i(">>>","startChat");
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_CHAT_ID, event.getRemoteChatId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public Context getContext() {
        return context;
    }

    public int getTotalUnreadCount() {
        return EMClient.getInstance().chatManager().getUnreadMessageCount();
    }

    public boolean isOnline() {
        return EMClient.getInstance().isConnected();
    }

    public SharedPreferences getSp() {
        return context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public void showFastReplyEditView() {
        Intent intent = new Intent(context, ChatFastReplySettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Subscribe(sticky = true)
    public void onLogin(EventLogin eventLogin) {
        ChatAccountManager.getInstance().login(eventLogin);
        ChatSettingManager.getInstance().loadClubLocation(true, null);
        ChatSettingManager.getInstance().loadDiceExpireTime();
        ChatSettingManager.getInstance().loadFastReply(null);
    }

    @Subscribe(sticky = true)
    public void onLogout(EventLogout eventLogout) {
        ChatAccountManager.getInstance().logout();
        ChatSettingManager.getInstance().clear();
    }

    @Subscribe
    public void onTokenExpire(EventTokenExpired tokenExpired) {
        ChatAccountManager.getInstance().logout();
        ChatSettingManager.getInstance().clear();
    }
}
