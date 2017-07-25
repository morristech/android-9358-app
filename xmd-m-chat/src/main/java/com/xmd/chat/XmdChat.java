package com.xmd.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMOptions;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.XmdApp;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.beans.Location;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.chat.view.ChatActivity;
import com.xmd.chat.view.ChatFastReplySettingActivity;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.Subscribe;

import rx.Observable;

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
    private Location location;

    public void init(Context context, boolean debug, MenuFactory menuFactory) {
        XLogger.i("---------聊天系统初始化---------------");

        if (!DiskCacheManager.isInit()) {
            throw new RuntimeException("dependency DiskCacheManager, but not init");
        }

        context = context.getApplicationContext();
        this.context = context;

        EMOptions options = new EMOptions();
//        options.setAppKey("xiaomodo#spa");
        EMClient.getInstance().init(context, options);
        EMClient.getInstance().setDebugMode(debug);

        EMClient.getInstance().chatManager().loadAllConversations();
        EMClient.getInstance().groupManager().loadAllGroups();

        AccountManager.getInstance().init();
        ConversationManager.getInstance().init();
        MessageManager.getInstance().init();
        setMenuFactory(menuFactory);


        EventBusSafeRegister.register(this);

        if (XmdApp.getInstance().isAppFirstStart()) {

            //历史原因，需要初始化用户信息
            EMClient.getInstance().chatManager().loadAllConversations();
            for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
                User user = ConversationManager.getInstance().parseUserFromConversation(conversation);
                if (user != null) {
                    UserInfoServiceImpl.getInstance().saveUser(user);
                }
                conversation.clear(); //清除加载的会话数据，避免聊天窗口加载出错
            }
        }
    }

    public MenuFactory getMenuFactory() {
        return menuFactory;
    }

    public void setMenuFactory(MenuFactory menuFactory) {
        this.menuFactory = menuFactory;
    }

    public void getClubLocation(final Callback<Location> callback) {
        if (location != null) {
            callback.onResponse(location, null);
            return;
        }
        Observable<BaseBean<Location>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .getClubLocation();
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<Location>>() {
            @Override
            public void onCallbackSuccess(BaseBean<Location> result) {
                location = result.getRespData();
                callback.onResponse(location, null);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onResponse(null, e);
            }
        });
    }

    @Subscribe
    public void onStartChat(EventStartChatActivity event) {
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

    public void setConversationFilter(ConversationManager.ConversationFilter filter) {
        ConversationManager.getInstance().setFilter(filter);
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
        AccountManager.getInstance().login(eventLogin);
        SettingManager.getInstance().loadDiceExpireTime();
        SettingManager.getInstance().loadFastReply(null);
    }

    @Subscribe(sticky = true)
    public void onLogout(EventLogout eventLogout) {
        AccountManager.getInstance().logout(eventLogout);
        SettingManager.getInstance().clear();
    }
}
