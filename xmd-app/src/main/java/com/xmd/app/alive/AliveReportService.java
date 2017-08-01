package com.xmd.app.alive;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.google.gson.stream.MalformedJsonException;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DeviceInfoUtils;
import com.xmd.app.CommonNetService;
import com.xmd.app.XmdApp;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.EventTokenExpired;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import rx.Observable;
import rx.Subscription;

/**
 * Created by heyangya on 17-5-23.
 * 向服务器汇报存活状态
 */

public class AliveReportService extends Service {
    private HandlerThread mWorkThread;
    private CmdHandler mHandler;
    private static Subscription mRequestSubscription;
    private static final int MSG_REPORT = 1;
    private static final int REPORT_INTERVAL = 10 * 60 * 1000;
    private static final int CHECK_INTERVAL = 300000;
    private static long mLastReportTime;
    private static boolean mLastReportSuccess;

    @Override
    public void onCreate() {
        super.onCreate();
        XLogger.i(">>> AliveReportService--create---");
        mWorkThread = new HandlerThread("AliveReportService");
        mWorkThread.start();
        mHandler = new CmdHandler(mWorkThread.getLooper());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeMessages(MSG_REPORT);
        if (mRequestSubscription != null) {
            mRequestSubscription.unsubscribe();
            mRequestSubscription = null;
        }
        mWorkThread.quit();
        XLogger.i("<<< AliveReportService--destroy---");
    }

    @Subscribe(sticky = true)
    public void onLogin(EventLogin event) {
        XLogger.i(">>> AliveReportService--start---");
        mHandler.removeMessages(MSG_REPORT);
        mHandler.sendMessage(createStartMessage(event.getToken()));
    }

    @Subscribe(sticky = true)
    public void onLogout(EventLogout event) {
        mHandler.removeMessages(MSG_REPORT);
        mLastReportTime = 0;
        if (mRequestSubscription != null) {
            mRequestSubscription.unsubscribe();
            mRequestSubscription = null;
        }
        XLogger.i("<<< AliveReportService--stop---");
    }

    @Subscribe
    public void onTokenExpire(EventTokenExpired tokenExpired) {
        mHandler.removeMessages(MSG_REPORT);
        mLastReportTime = 0;
        if (mRequestSubscription != null) {
            mRequestSubscription.unsubscribe();
            mRequestSubscription = null;
        }
    }

    private static Message createStartMessage(String token) {
        Message msg = new Message();
        msg.what = MSG_REPORT;
        msg.obj = token;
        return msg;
    }


    private static class CmdHandler extends Handler {
        CmdHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REPORT:
                    if (!mLastReportSuccess || System.currentTimeMillis() - REPORT_INTERVAL > mLastReportTime) {
                        mLastReportTime = System.currentTimeMillis();
                        reportAlive((String) msg.obj);
                    }
                    sendMessageDelayed(createStartMessage((String) msg.obj), CHECK_INTERVAL);
                    break;
            }
        }
    }

    private static void reportAlive(final String token) {
        XLogger.i(">>>reportAlive: " + token);
        Observable<BaseBean> observable = XmdNetwork.getInstance()
                .getService(CommonNetService.class)
                .reportAlive(token, DeviceInfoUtils.getDeviceId(XmdApp.getInstance().getContext()));
        mRequestSubscription =
                XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
                    @Override
                    public void onCallbackSuccess(BaseBean result) {
                        mLastReportSuccess = true;
                        XLogger.i("<<<reportAlive: " + token + " sucess");
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        if (e instanceof MalformedJsonException) {
                            mLastReportSuccess = true;
                            XLogger.i("<<<reportAlive: " + token + " sucess");
                            return;
                        }
                        mLastReportSuccess = false;
                        XLogger.e("<<<reportAlive: " + token + " failed:" + e.getLocalizedMessage());
                    }
                });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setForeground();
        return Service.START_STICKY;
    }

    private static int NOTIFICATION_ID = 0x2333;

    void setForeground() {
        startForeground(NOTIFICATION_ID, new Notification());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startService(new Intent(this, InnerService.class));
        }
    }

    public static class InnerService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(NOTIFICATION_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
