package com.xmd.chat;

import android.os.Handler;
import android.os.Message;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DeviceInfoUtils;
import com.xmd.app.XmdApp;
import com.xmd.app.event.EventLogin;
import com.xmd.app.user.User;
import com.xmd.chat.event.EventChatLoginSuccess;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mo on 17-6-28.
 * 账号管理（登录，登出）
 */

public class ChatAccountManager {
    private static final ChatAccountManager ourInstance = new ChatAccountManager();

    public static ChatAccountManager getInstance() {
        return ourInstance;
    }

    private ChatAccountManager() {
    }

    private boolean isRunLogin;
    private User user;
    private String token;
    private String deviceId;

    public void init() {
        deviceId = DeviceInfoUtils.getDeviceId(XmdApp.getInstance().getContext());
    }

    public void login(EventLogin eventLogin) {
        user = eventLogin.getUser();
        token = eventLogin.getToken();
        loopLogin();
    }

    public void logout() {
        mHandler.removeCallbacksAndMessages(null);
        EMClient.getInstance().logout(false, null);
        isRunLogin = false;
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
//        if (!isRunLogin) {
//            return;
//        }
//        XLogger.i(XmdChat.TAG, "check token --> login chatId:" + user.getChatId() + ",chatPassword:" + user.getChatPassword());
//        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(CommonNetService.class)
//                .reportAlive(token, deviceId);
//        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
//            @Override
//            public void onCallbackSuccess(BaseBean result) {
//                chatLogin();
//            }
//
//            @Override
//            public void onCallbackError(Throwable e) {
//                XLogger.e(e.getMessage());
//                Message message = new Message();
//                message.what = 1;
//                mHandler.sendMessageDelayed(message, 1000);
//            }
//        });
        chatLogin();
    }

    private void chatLogin() {
        if (!isRunLogin) {
            return;
        }
        XLogger.i(XmdChat.TAG, "chat login --> login chatId:" + user.getChatId() + ",chatPassword:" + user.getChatPassword());
        EMClient.getInstance().login(user.getChatId(), user.getChatPassword(), new EMCallBack() {
            @Override
            public void onSuccess() {
                if (!isRunLogin) {
                    return;
                }
                isRunLogin = false;
                XLogger.i(XmdChat.TAG, "login success!  chatId:" + user.getChatId());
                EMClient.getInstance().chatManager().loadAllConversations();
                EMClient.getInstance().groupManager().loadAllGroups();
                EventBus.getDefault().post(new EventChatLoginSuccess());
            }

            @Override
            public void onError(int i, String s) {
                XLogger.e(XmdChat.TAG, "login error:i=" + i + ",s=" + s);
                if (!isRunLogin) {
                    return;
                }
                if (i == 200) {
                    onSuccess();
                    return;
                }
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessageDelayed(message, 1000);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
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
