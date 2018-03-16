package com.xmd.chat.xmdchat.present;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMClient;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DeviceInfoUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.XmdApp;
import com.xmd.app.event.EventLogin;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.XmdChat;
import com.xmd.chat.event.EventChatLoginSuccess;
import com.xmd.chat.xmdchat.contract.XmdChatAccountManagerInterface;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Lhj on 18-1-24.
 */

public class EmChatAccountManagerPresent implements XmdChatAccountManagerInterface {

    private boolean isRunLogin;
    private String token;
    private String deviceId;
    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();

    @Override
    public void init(Context context) {
        deviceId = DeviceInfoUtils.getDeviceId(XmdApp.getInstance().getContext());
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
                XLogger.d(XmdChat.TAG, "--connected--");
            }

            @Override
            public void onDisconnected(int i) {
                XLogger.d(XmdChat.TAG, "--disconnected--" + i);
                if (i == 206) {
                    loopLogin();
                }
            }
        });
    }

    @Override
    public void login(EventLogin eventLogin) {
        XLogger.i(XmdChat.TAG, "login=>" + eventLogin);
        token = eventLogin.getToken();
        loopLogin();
    }

    @Override
    public void login() {

    }

    @Override
    public void logout() {
        XLogger.i(XmdChat.TAG, "logout=<");
        mHandler.removeCallbacksAndMessages(null);
        EMClient.getInstance().logout(false, null);
        isRunLogin = false;
    }

    @Override
    public void loopLogin() {
        if (isRunLogin) {
            return;
        }
        isRunLogin = true;
        mHandler.sendEmptyMessage(1);
    }

    private void chatLogin() {
        if (!isRunLogin) {
            return;
        }
        User user = userInfoService.getCurrentUser();
        XLogger.i(XmdChat.TAG, "chat login --> login chatId:" + user.getChatId() + ",chatPassword:" + user.getChatPassword());
        EMClient.getInstance().login(user.getChatId(), user.getChatPassword(), new EMCallBack() {
            @Override
            public void onSuccess() {
                if (!isRunLogin) {
                    return;
                }
                isRunLogin = false;
                XLogger.i(XmdChat.TAG, "login success!  chatId:" + userInfoService.getCurrentUser().getChatId());
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


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                loginEM();
            }
        }
    };

    private void loginEM() {
        if (!isRunLogin) {
            return;
        }
        User user = UserInfoServiceImpl.getInstance().getCurrentUser();
        if (user == null) {
            XToast.show("无法登录聊天账号，未找到用户信息");
            XLogger.e("无法登录聊天账号，未找到用户信息");
            return;
        }
        XLogger.i(XmdChat.TAG, "check token --> login chatId:" + user.getChatId() + ",chatPassword:" + user.getChatPassword());
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
//                mHandler.sendMessageDelayed(message, 3000);
//            }
//        });
    }
}
