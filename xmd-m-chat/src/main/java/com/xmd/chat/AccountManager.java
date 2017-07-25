package com.xmd.chat;

import android.os.Handler;
import android.os.Message;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.user.User;
import com.xmd.chat.event.EventChatLoginSuccess;

import org.greenrobot.eventbus.EventBus;

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

    }

    public void login(EventLogin eventLogin) {
        user = eventLogin.getUser();
        loopLogin();
    }

    public void logout(EventLogout eventLogout) {
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
        XLogger.i(XmdChat.TAG, "login chatId:" + user.getChatId() + ",chatPassword:" + user.getChatPassword());
        EMClient.getInstance().login(user.getChatId(), user.getChatPassword(), new EMCallBack() {
            @Override
            public void onSuccess() {
                isRunLogin = false;
                XLogger.i(XmdChat.TAG, "login success!  chatId:" + user.getChatId());
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
                XLogger.e(XmdChat.TAG, "login error:i=" + i + ",s=" + s + ",  chatId:" + user.getChatId());
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
