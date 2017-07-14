package com.xmd.chat;

import android.os.Handler;
import android.os.Message;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.user.User;
import com.xmd.chat.event.EventChatLoginSuccess;
import com.xmd.m.network.EventTokenExpired;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by mo on 17-6-28.
 * 账号管理（登录，登出）
 */

public class AccountManager {
    private static final AccountManager ourInstance = new AccountManager();

    public static AccountManager getInstance() {
        return ourInstance;
    }

    private AccountManager() {
    }

    private boolean isRunLogin;
    private User user;

    public void init() {
        EventBusSafeRegister.register(this);
    }

    @Subscribe(sticky = true)
    public void onLogin(EventLogin eventLogin) {
        XLogger.i(XmdChat.TAG, "user login");
        user = eventLogin.getUser();
        loopLogin();
    }

    @Subscribe(sticky = true)
    public void onLogout(EventLogout eventLogout) {
        XLogger.i(XmdChat.TAG, "user logout");
        mHandler.removeCallbacksAndMessages(null);
        logout();
        user = null;
    }

    @Subscribe
    public void onTokenExpired(EventTokenExpired eventTokenExpired) {
        XLogger.i(XmdChat.TAG, "token expire, logout");
        mHandler.removeCallbacksAndMessages(null);
        logout();
        user = null;
    }

    private void loopLogin() {
        if (isRunLogin) {
            return;
        }
        isRunLogin = true;
        mHandler.sendEmptyMessage(1);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                login();
            }
        }
    };

    private void login() {
        XLogger.d(XmdChat.TAG, "login chatId:" + user.getChatId() + ",chatPassword:" + user.getChatPassword());
        EMClient.getInstance().login(user.getChatId(), user.getChatPassword(), new EMCallBack() {
            @Override
            public void onSuccess() {
                isRunLogin = false;
                XLogger.d(XmdChat.TAG, "login success!  chatId:" + user.getChatId());
                EMClient.getInstance().chatManager().loadAllConversations();
                EMClient.getInstance().groupManager().loadAllGroups();
                EventBus.getDefault().post(new EventChatLoginSuccess());
            }

            @Override
            public void onError(int i, String s) {
                isRunLogin = false;
                if (i == 200) {
                    onSuccess();
                    return;
                }
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessageDelayed(message, 1000);
                XLogger.d(XmdChat.TAG, "login error:i=" + i + ",s=" + s + ",  chatId:" + user.getChatId());
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private void logout() {
        EMClient.getInstance().logout(false);
        isRunLogin = false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getChatId() {
        return user == null ? null : user.getChatId();
    }

    public String getUserType() {
        return user == null ? null : user.getUserType();
    }
}
