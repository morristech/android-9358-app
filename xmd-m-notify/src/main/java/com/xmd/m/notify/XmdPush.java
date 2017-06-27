package com.xmd.m.notify;

import android.content.Context;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.Subscribe;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

/**
 * Created by mo on 17-6-23.
 * 小摩豆推送
 */

public class XmdPush {
    public final static String TAG = "XmdPush";
    private static final XmdPush ourInstance = new XmdPush();

    public static XmdPush getInstance() {
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

    private Call<BaseBean> bindCall;
    private Subscription unBindSubscription;

    public void init(Context context, String appId, String appKey, String appSecret, String masterSecret, ActionListener listener) {
        if (context == null || appId == null || appKey == null || appSecret == null || masterSecret == null || listener == null) {
            throw new RuntimeException("参数错误");
        }
        this.getuiAppId = appId;
        this.getuiAppKey = appKey;
        this.getuiAppSecret = appSecret;
        this.getuiMasterSecret = masterSecret;

        this.actionListener = listener;

        PushManager.getInstance().initialize(context, GetuiPushService.class);
        PushManager.getInstance().registerPushIntentService(context, GetuiReceiveService.class);

        EventBusSafeRegister.register(this);
        XLogger.i(TAG, "XmdPush init ok!");
    }

    //设置clientId
    public void setClientId(String clientId) {
        this.clientId = clientId;
        loopBind(token, clientId);
    }

    //登录事件
    @Subscribe(sticky = true)
    public void handleLogin(EventLogin eventLogin) {
        XLogger.d(TAG, "on event login:" + eventLogin);
        setUserId(eventLogin.getUserId());
        setToken(eventLogin.getToken());
        loopBind(token, clientId);
    }

    //登出事件
    @Subscribe(sticky = true)
    private void handleLogout(EventLogout eventLogout) {
        XLogger.d(TAG, "on event logout:" + eventLogout);
        setUserId(null);
        setToken(null);
        unbind();
    }

    //返回是否绑定
    public boolean isBound() {
        return bound;
    }


    private void setToken(String token) {
        this.token = token;
        if (!TextUtils.isEmpty(token)) {
            loopBind(token, clientId);
        }
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

    private RetryPool.RetryRunnable retryRunnable = new RetryPool.RetryRunnable(1000, 1.1f, new RetryPool.RetryExecutor() {
        @Override
        public boolean run() {
            return !bound || bind();
        }
    });

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
        XLogger.d(TAG, "start loop bind ... ");
        binding = true;
        RetryPool.getInstance().postWork(retryRunnable);
    }

    private boolean bind() {
        if (!binding) {
            return true;
        }
        XLogger.d(TAG, "binding ...userId:" + userId + ",clientId:" + clientId);
        String secretBefore = getuiAppId +
                getuiAppSecret +
                userId +
                getuiAppKey +
                getuiMasterSecret +
                clientId;
        String secret = DESede.encrypt(secretBefore);
        bindCall = XmdNetwork.getInstance()
                .getService(NetService.class)
                .bindGetuiClientId(token, userId, "tech", "android", clientId, secret);
        XmdNetwork.getInstance().requestSync(bindCall, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                bound = true;
                XLogger.i(TAG, "bind userId:" + userId + " with " + clientId + " success!");
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, "bind failed:" + e.getLocalizedMessage());
            }
        });

        return bound;
    }

    private void unbind() {
        XLogger.i(TAG, "unbind ");
        binding = false;
        RetryPool.getInstance().removeWork(retryRunnable);
        //取消绑定操作
        if (bindCall != null && !bindCall.isCanceled()) {
            bindCall.cancel();
        }
        //取消解绑操作
        if (unBindSubscription != null && !unBindSubscription.isUnsubscribed()) {
            unBindSubscription.unsubscribe();
        }
        Observable<BaseBean> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .unbindGetuiClientId("tech", clientId);
        unBindSubscription = XmdNetwork.getInstance().request(observable, null);
        if (bound) {
            bound = false;
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
