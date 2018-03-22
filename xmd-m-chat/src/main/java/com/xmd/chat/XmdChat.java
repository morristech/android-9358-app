package com.xmd.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.chat.view.ChatFastReplySettingActivity;
import com.xmd.chat.xmdchat.contract.XmdChatInterface;
import com.xmd.chat.xmdchat.model.XmdChatModel;
import com.xmd.chat.xmdchat.present.EmXmdChatPresent;
import com.xmd.chat.xmdchat.present.ImXmdChatPresent;
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

    private XmdChatInterface mInterface;

    private XmdChat() {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            mInterface = new EmXmdChatPresent();
        } else {
            mInterface = new ImXmdChatPresent();
        }
    }

    private Context context;
    private MenuFactory menuFactory;

    public void init(Context context, String appKey, boolean debug, MenuFactory menuFactory) {
        XLogger.i("xmdchat init: appKey=" + appKey + ",debug=" + debug);
        EventBusSafeRegister.register(this);
        context = context.getApplicationContext();
        this.context = context;
        mInterface.init(context, appKey, debug, menuFactory);
        ChatAccountManager.getInstance().init(context, appKey, debug);
        ConversationManager.getInstance().init();
        ChatMessageManager.getInstance().init();
        setMenuFactory(menuFactory);

    }

    public void loadConversation() {
        mInterface.loadConversation();
    }

    public MenuFactory getMenuFactory() {
        return menuFactory;
    }

    public void setMenuFactory(MenuFactory menuFactory) {
        this.menuFactory = menuFactory;
    }

    @Subscribe
    public void onStartChat(EventStartChatActivity event) {
        mInterface.onStartChat(event);
    }

    public Context getContext() {
        return context;
    }

    public int getTotalUnreadCount() {
        return mInterface.getTotalUnreadCount();
    }

    public boolean isOnline() {
        return mInterface.isOnline();
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
        ChatSettingManager.getInstance().loadClubLocation(true, null);
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
