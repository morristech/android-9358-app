package com.xmd.m.notify.push;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.EventTokenExpired;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

/**
 * Created by mo on 17-6-27.
 * 推送管理
 */

public class XmdPushManager {
    public final static String TAG = "XmdPush";
    private static final XmdPushManager ourInstance = new XmdPushManager();

    public static XmdPushManager getInstance() {
        return ourInstance;
    }

    private XmdPushManager() {
    }

    private Context context;

    private String getuiAppId;
    private String getuiAppKey;
    private String getuiAppSecret;
    private String getuiMasterSecret;

    private String userId;
    private String token;
    private String clientId;
    private boolean bound; //是否绑定
    private boolean binding; //是否正在执行绑定

    private Call<BaseBean> bindCall;
    private Subscription unBindSubscription;
    private String userType;

    public void init(Context context, String userType, XmdPushMessageListener listener) {
        if (context == null) {
            throw new RuntimeException("参数错误");
        }
        context = context.getApplicationContext();
        this.context = context;
        addListener(listener);
        this.userType = userType;

        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            getuiAppId = applicationInfo.metaData.getString("PUSH_APPID", "");
            getuiAppKey = applicationInfo.metaData.getString("PUSH_APPKEY", "");
            getuiAppSecret = applicationInfo.metaData.getString("PUSH_APPSECRET", "");
            getuiMasterSecret = applicationInfo.metaData.getString("GETUI_MASTER_SECRET", "");
            XLogger.i("getuiAppId:" + getuiAppId);
            XLogger.i("getuiAppKey:" + getuiAppKey);
            XLogger.i("getuiAppSecret:" + getuiAppSecret);
            XLogger.i("getuiMasterSecret:" + getuiMasterSecret);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("can not get meta data!");
        }

        com.igexin.sdk.PushManager.getInstance().initialize(context, GetuiPushService.class);
        com.igexin.sdk.PushManager.getInstance().registerPushIntentService(context, GetuiReceiveService.class);

        EventBusSafeRegister.register(this);
        XLogger.i(TAG, "XmdPushModule init ok!");
    }

    //设置clientId
    public void setClientId(String clientId) {
        this.clientId = clientId;
        loopBind(token, clientId);
    }

    public String getClientId() {
        return this.clientId;
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
    public void handleLogout(EventLogout eventLogout) {
        XLogger.d(TAG, "on event logout:" + eventLogout);
        RetryPool.getInstance().removeWork(retryRunnable);
        setUserId(null);
        setToken(null);
        unbind();
    }

    //token失效事件
    @Subscribe
    public void onTokenExpire(EventTokenExpired tokenExpired) {
        RetryPool.getInstance().removeWork(retryRunnable);
        binding = false;
        setUserId(null);
        setToken(null);
    }

    //返回是否绑定
    public boolean isBound() {
        return bound;
    }

    public Context getContext() {
        return context;
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
            return bind();
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
        XLogger.d(TAG, "binding... userId:" + userId + " & clientId:" + clientId);
        String secretBefore = getuiAppId +
                getuiAppSecret +
                userId +
                getuiAppKey +
                getuiMasterSecret +
                clientId;
        String secret = DESede.encrypt(secretBefore);
        bindCall = XmdNetwork.getInstance()
                .getService(NetService.class)
                .bindGetuiClientId(token, userId, userType, "android", clientId, secret, userType);
        XmdNetwork.getInstance().requestSync(bindCall, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                bound = true;
                XLogger.i(TAG, "bind getui success --- userId:" + userId + " & clientId:" + clientId);
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, "bind getui failed --- " + e.getLocalizedMessage());
            }
        });

        return bound;
    }

    private void unbind() {
        XLogger.i(TAG, "unbind getui");
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
                .unbindGetuiClientId(userType, clientId, userType);
        unBindSubscription = XmdNetwork.getInstance().request(observable);
        if (bound) {
            bound = false;
        }
    }

    private List<XmdPushMessageListener> listenerList = new ArrayList<>();

    public void addListener(XmdPushMessageListener listener) {
        XLogger.i(TAG, "add push listener");
        if (listener != null && !listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void removeListener(XmdPushMessageListener listener) {
        XLogger.i(TAG, "remove push listener");
        listenerList.remove(listener);
    }

    public void notifyMessage(XmdPushMessage message, String rowMessage) {
        for (XmdPushMessageListener listener : listenerList) {
            if (message != null) {
                listener.onMessage(message);
            }
            if (!TextUtils.isEmpty(rowMessage)) {
                listener.onRawMessage(rowMessage);
            }
        }
    }

    public void checkPushStatus() {
        boolean isPushTurnedOn = com.igexin.sdk.PushManager.getInstance().isPushTurnedOn(context);
        XLogger.i(TAG, "isPushTurnedOn = " + isPushTurnedOn);
        if (!isPushTurnedOn) {
            com.igexin.sdk.PushManager.getInstance().turnOnPush(context);
        }
    }
}
