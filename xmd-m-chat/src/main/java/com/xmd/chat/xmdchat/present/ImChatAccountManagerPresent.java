package com.xmd.chat.xmdchat.present;

import android.content.Context;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.chat.R;
import com.xmd.chat.XmdChat;
import com.xmd.chat.event.EventChatLoginSuccess;
import com.xmd.chat.event.EventForceOffline;
import com.xmd.chat.event.EventUserSigExpired;
import com.xmd.chat.xmdchat.constant.XmdChatConstant;
import com.xmd.chat.xmdchat.contract.XmdChatAccountManagerInterface;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Lhj on 18-1-24.
 * 因某些未知原因，将腾讯的登录放在MainActivity 的 onCreate 方法里，不对Token进行校验
 */

public class ImChatAccountManagerPresent implements XmdChatAccountManagerInterface {

    private boolean isRunLogin;
    private String token;
    private String deviceId;
    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();

    private static ImChatAccountManagerPresent present;

    private ImChatAccountManagerPresent() {

    }

    public static ImChatAccountManagerPresent getInstance() {
        if (present == null) {
            synchronized (ImChatAccountManagerPresent.class) {
                present = new ImChatAccountManagerPresent();
            }
        }
        return present;
    }


    @Override
    public void init(Context context) {
        TIMSdkConfig config = new TIMSdkConfig(XmdChatConstant.SDK_APPID);
        config.setLogLevel(TIMLogLevel.DEBUG);
        //控制台日志是否打印
        config.enableLogPrint(true);
        //错误日志上报
        config.enableCrashReport(false);
        TIMManager.getInstance().init(context, config);
        configUserInfo();
    }

    @Override
    public void login(EventLogin eventLogin) {
        if (userIsOnline()) {
            XLogger.i(">>>", "用户在线");
            return;
        }
        token = eventLogin.getToken();
     //   loopLogin();
     //   chatLogin();
    }

    @Override
    public void login() {
        chatLogin();
    }

    @Override
    public void logout() {
        //  if (userIsOnline()) {
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                XLogger.e("退出登录失败》" + i + "<fail>" + s);
            }

            @Override
            public void onSuccess() {
                XLogger.i(">>>", "IM logout success");
            }
        });
        //   }
    }

    @Override
    public void loopLogin() {

    }

//    @Override
//    public void loopLogin() {
//        if (isRunLogin) {
//            return;
//        }
//        isRunLogin = true;
//        mHandler.sendEmptyMessage(1);
//    }

    /**
     * 用户配置
     */
    private void configUserInfo() {
        TIMUserConfig userConfig = new TIMUserConfig();
        //用户状态监听
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                //被其他终端踢下线
                EventBus.getDefault().post(new EventForceOffline());
            }

            @Override
            public void onUserSigExpired() {
                //用户签名过期
                EventBus.getDefault().post(new EventUserSigExpired());
            }
        });
        //用户链接状态监听
        userConfig.setConnectionListener(new TIMConnListener() {
            @Override
            public void onConnected() {
                XLogger.d(XmdChat.TAG, "--connected--");
            }

            @Override
            public void onDisconnected(int i, String s) {
                XLogger.d(XmdChat.TAG, "--disconnected--" + i);
            }

            @Override
            public void onWifiNeedAuth(String s) {
                XToast.show("当前网络不可用");
            }
        });

        userConfig.setRefreshListener(new TIMRefreshListener() {
            @Override
            public void onRefresh() {
                XLogger.i(">>>", "刷新数据");
            }

            @Override
            public void onRefreshConversation(List<TIMConversation> list) {
                XLogger.i(">>>", "刷新会话列表");
            }
        });
        //消息扩展用户配置
        userConfig = new TIMUserConfigMsgExt(userConfig)
                //开启本地本地消息存储
                .enableStorage(true)
                //开启消息已读回执
                .enableReadReceipt(false);
        userConfig = ImChatMessageManagerPresent.getInstance().init(userConfig);
        TIMManager.getInstance().setUserConfig(userConfig);
    }

//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 1) {
//                ImChatLogin();
//            }
//        }
//    };

//    private void ImChatLogin() {
//        if (!isRunLogin) {
//            return;
//        }
//        User user = UserInfoServiceImpl.getInstance().getCurrentUser();
//        if (user == null) {
//            XToast.show("无法登录聊天账号，未找到用户信息");
//            XLogger.e("无法登录聊天账号，未找到用户信息");
//            return;
//        }
//        XLogger.i(XmdChat.TAG, "check token --> login chatId:" + user.getChatId() + ",chatPassword:" + user.getChatPassword());
//        rx.Observable<BaseBean> observable = XmdNetwork.getInstance().getService(CommonNetService.class)
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
//    }

    private void chatLogin() {
//        if (!isRunLogin || userIsOnline()) {
//            return;
//        }
        final User user = userInfoService.getCurrentUser();
        XLogger.i(XmdChat.TAG, "chat login --> login chatId:" + user.getChatId() + ",chatPassword:" + user.getChatPassword());
        TIMManager.getInstance().login(user.getChatId(), user.getChatPassword(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                XToast.show("腾讯云登录失败：" + i + ">" + s);
                switch (i) {
                    case XmdChatConstant.error_code_offline:
                       XLogger.i(">>>", "离线状态下被踢下");
                        XLogger.i(">>>>","token:"+token);
                        EventBus.getDefault().post(new EventLogout(token,user.getUserId()));
                        break;
                    case XmdChatConstant.error_code_network:
                        XToast.show(ResourceUtils.getString(R.string.net_work_error));
                        break;
                }
//                if (!isRunLogin) {
//                    return;
//                }
//                Message message = new Message();
//                message.what = 1;
//                mHandler.sendMessageDelayed(message, 3000);
            }

            @Override
            public void onSuccess() {
                XToast.show("腾讯云登录成功");
//                if (!isRunLogin) {
//                    return;
//                }
//                isRunLogin = false;
                XLogger.i(XmdChat.TAG, "login success!  ImChatId:" + userInfoService.getCurrentUser().getChatId());
                EventBus.getDefault().post(new EventChatLoginSuccess());
            }
        });
    }

    //获取当前用户名，也可以通过这个方法判断是否已登录
    public String getCurrentUser() {
        return TIMManager.getInstance().getLoginUser();
    }

    //当前用户是否在线
    public boolean userIsOnline() {
        return TextUtils.isEmpty(getCurrentUser()) ? false : true;
    }
}
