package com.xmd.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.chat.beans.Location;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.chat.view.ChatActivity;
import com.xmd.chat.view.ChatFastReplySettingActivity;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.EventTokenExpired;
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
        XLogger.i("---------聊天系统初始化---------------debug=" + debug);

        if (!DiskCacheManager.isInit()) {
            throw new RuntimeException("dependency DiskCacheManager, but not init");
        }
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String appId = applicationInfo.metaData.getString("EASEMOB_APPKEY", "");
            XLogger.i(TAG, "EASEMOB_APPKEY=" + appId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        context = context.getApplicationContext();
        this.context = context;

        EMOptions options = new EMOptions();
//        options.setAppKey("xiaomodo#spa");
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
        SettingManager.getInstance().loadDiceExpireTime();
        SettingManager.getInstance().loadFastReply(null);
    }

    @Subscribe(sticky = true)
    public void onLogout(EventLogout eventLogout) {
        ChatAccountManager.getInstance().logout();
        SettingManager.getInstance().clear();
    }

    @Subscribe
    public void onTokenExpire(EventTokenExpired tokenExpired) {
        ChatAccountManager.getInstance().logout();
        SettingManager.getInstance().clear();
    }
}
