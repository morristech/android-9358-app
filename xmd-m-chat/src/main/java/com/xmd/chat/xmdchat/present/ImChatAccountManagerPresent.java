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
    public void init(Context context, boolean debug) {
        TIMSdkConfig config;
        if (debug) {
            XLogger.i("init tencent account use debug setting");
            config = new TIMSdkConfig(XmdChatConstant.SDK_APP_ID_DEBUG);
        } else {
            XLogger.i("init tencent account use product setting");
            config = new TIMSdkConfig(XmdChatConstant.SDK_APP_ID_RELEASE);
        }
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
            return;
        }
        token = eventLogin.getToken();
    }

    @Override
    public void login() {
        chatLogin();
    }

    @Override
    public void logout() {
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                XLogger.e("退出登录失败》" + i + "<fail>" + s);
            }

            @Override
            public void onSuccess() {
                XLogger.i(XmdChat.TAG, "IM logout success");
            }
        });
    }

    @Override
    public void loopLogin() {

    }

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
                XToast.show("网络连接失败,请检查网络");
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
                XLogger.i(XmdChat.TAG, "刷新数据");
            }

            @Override
            public void onRefreshConversation(List<TIMConversation> list) {
                XLogger.i(XmdChat.TAG, "刷新会话列表");
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

    private void chatLogin() {
        final User user = userInfoService.getCurrentUser();
        XLogger.i(XmdChat.TAG, "chat login --> login chatId:" + user.getChatId() + ",chatPassword:" + user.getChatPassword());
        TIMManager.getInstance().login(user.getChatId(), user.getChatPassword(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                XToast.show("腾讯云登录失败：" + i + ">" + s);
                switch (i) {
                    case XmdChatConstant.error_code_offline:
                        XLogger.i(XmdChat.TAG, "离线状态下被踢下");
                        EventBus.getDefault().post(new EventLogout(token, user.getUserId()));
                        break;
                    case XmdChatConstant.error_code_network:
                        XToast.show(ResourceUtils.getString(R.string.net_work_error));
                        break;
                }
            }

            @Override
            public void onSuccess() {
                //  XToast.show("腾讯云登录成功");
                XLogger.i(XmdChat.TAG, "login success!  ImChatId:" + userInfoService.getCurrentUser().getChatId());
                XmdChat.getInstance().loadConversation();
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
