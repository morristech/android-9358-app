package com.xmd.m.notify;

import android.content.Context;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscription;

/**
 * Created by mo on 17-6-23.
 * 小摩豆推送
 */

class XmdPush {
    private final static String TAG = "XmdPush";
    private static final XmdPush ourInstance = new XmdPush();

    static XmdPush getInstance() {
        return ourInstance;
    }

    private XmdPush() {
    }


    private String getuiAppId;
    private String getuiAppKey;
    private String getuiAppSecret;
    private String getuiMasterSecret;
    private ActionListener actionListener;

    private String userId;
    private String token;
    private String clientId;
    private boolean bound; //是否绑定
    private boolean binding; //是否正在执行绑定
    private Subscription bindSubscription;

    public void init(Context context, String appId, String appKey, String appSecret, String masterSecret, ActionListener listener) {
        if (context == null || appId == null || appKey == null || appSecret == null || masterSecret == null || listener == null) {
            throw new RuntimeException("参数错误");
        }
        this.getuiAppId = appId;
        this.getuiAppKey = appKey;
        this.getuiAppSecret = appSecret;
        this.getuiMasterSecret = masterSecret;

        PushManager.getInstance().initialize(context, GetuiPushService.class);
        PushManager.getInstance().registerPushIntentService(context, GetuiReceiveService.class);

        EventBusSafeRegister.register(this);
    }

    void setClientId(String clientId) {
        this.clientId = clientId;
        loopBind(token, clientId);
    }

    void setToken(String token) {
        this.token = token;
        if (!TextUtils.isEmpty(token)) {
            loopBind(token, clientId);
        }
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    //登录事件
    @Subscribe(sticky = true)
    public void handleLogin(EventLogin eventLogin) {
        setUserId(eventLogin.getUserId());
        setToken(eventLogin.getToken());
    }

    //登出事件
    @Subscribe(sticky = true)
    private void handleLogout(EventLogout eventLogout) {
        setUserId(null);
        setToken(null);
        unbind(eventLogout.getToken());
    }

    private void loopBind(final String token, final String clientId) {
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(clientId)) {
            XLogger.d(TAG, " token or client id is null , token=" + token + ",client id=" + clientId);
            return;
        }
        if (bound) {
            XLogger.i(TAG, " already bound!");
            return;
        }
        if (binding) {
            XLogger.i(TAG, " already running bind!");
            return;
        }
        XLogger.d(TAG, "start bind ... ");
        if (mUnBindCall != null) {
            mUnBindCall.cancel();
        }
        mRunBind = true;
        RetryPool.getInstance().postWork(new RetryPool.RetryRunnable(1000, 1.1f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return !mRunBind || bind(token);
            }
        }));
    }

    private boolean bind(String token) {
        String secretBefore = getuiAppId +
                getuiAppSecret +
                userId +
                getuiAppKey +
                getuiMasterSecret +
                clientId;
        String secret = DESede.encrypt(secretBefore);
        mBindCall = getSpaService().bindGetuiClientId(token, userId, RequestConstant.USER_TYPE_TECH, RequestConstant.APP_TYPE_ANDROID, mClientId, secret);
        try {
            Response<BaseResult> response = mBindCall.execute();
            BaseResult result = response.body();
            if (!response.isSuccessful() || result == null || result.statusCode != 200) {
                XLogger.e(TAG, "bind failed:" + response.code() + "," + response.message());
                return false;
            }
            XLogger.i(TAG, "bind userId:" + userId + " with " + mClientId + " success!");
            mIsBind = true;
            mRunBind = false;
            return true;
        } catch (IOException e) {
            XLogger.e(TAG, "bind failed:" + e.getLocalizedMessage());
        }
        return false;
    }

    private void unbind(String token) {
        XLogger.i(TAG, "unbind ");
        binding = false;
        if (mBindCall != null) {
            mBindCall.cancel();
        }
        if (mIsBind) {
            mIsBind = false;
            mUnBindCall = getSpaService().unbindGetuiClientId(RequestConstant.USER_TYPE_TECH,
                    token, RequestConstant.SESSION_TYPE, mClientId);
            mUnBindCall.enqueue(new Callback<BaseResult>() {
                @Override
                public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {

                }

                @Override
                public void onFailure(Call<BaseResult> call, Throwable t) {

                }
            });
        }
    }

    public String getGetuiAppId() {
        return getuiAppId;
    }

    public void setGetuiAppId(String getuiAppId) {
        this.getuiAppId = getuiAppId;
    }

    public String getGetuiAppKey() {
        return getuiAppKey;
    }

    public void setGetuiAppKey(String getuiAppKey) {
        this.getuiAppKey = getuiAppKey;
    }

    public String getGetuiAppSecret() {
        return getuiAppSecret;
    }

    public void setGetuiAppSecret(String getuiAppSecret) {
        this.getuiAppSecret = getuiAppSecret;
    }

    public String getGetuiMasterSecret() {
        return getuiMasterSecret;
    }

    public void setGetuiMasterSecret(String getuiMasterSecret) {
        this.getuiMasterSecret = getuiMasterSecret;
    }

    public ActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }
}
