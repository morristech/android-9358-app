package com.xmd.chat;

import android.os.Handler;
import android.os.Message;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by mo on 17-6-28.
 * 账号管理（登录，登出）
 */

class AccountManager {
    private static final AccountManager ourInstance = new AccountManager();

    static AccountManager getInstance() {
        return ourInstance;
    }

    private AccountManager() {
    }

    private boolean logining;
    private String account;
    private String password;

    public void init() {
        EventBusSafeRegister.register(this);
    }

    @Subscribe(sticky = true)
    public void onLogin(EventLogin eventLogin) {
        account = eventLogin.getChatId();
        password = eventLogin.getChatPassword();
        loopLogin();
    }

    @Subscribe(sticky = true)
    public void onLogout(EventLogout eventLogout) {
        mHandler.removeCallbacksAndMessages(null);
        logout();
    }

    private void loopLogin() {
        if (logining) {
            return;
        }
        logining = true;
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
        XLogger.d(XmdChat.TAG, "login account:" + account + ",password:" + password);
        EMClient.getInstance().login(account, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                logining = false;
                XLogger.d(XmdChat.TAG, "login success!  account:" + account + ",password:" + password);
                EMClient.getInstance().chatManager().loadAllConversations();
                EMClient.getInstance().groupManager().loadAllGroups();
                EventBus.getDefault().post(new EventChatLoginSuccess());
            }

            @Override
            public void onError(int i, String s) {
                logining = false;
                if (i == 200) {
                    onSuccess();
                    return;
                }
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessageDelayed(message, 1000);
                XLogger.d(XmdChat.TAG, "login error:i=" + i + ",s=" + s + ",  account:" + account + ",password:" + password);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private void logout() {
        EMClient.getInstance().logout(false);
        logining = false;
    }
}
