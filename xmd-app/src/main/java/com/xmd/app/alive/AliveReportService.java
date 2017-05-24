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

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.network.OkHttpUtil;
import com.shidou.commonlibrary.util.DeviceInfoUtils;
import com.xmd.app.Init;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by heyangya on 17-5-23.
 * 向服务器汇报存活状态
 */

public class AliveReportService extends Service {
    private HandlerThread mWorkThread;
    private CmdHandler mHandler;
    private static Call mRequestCall;
    private static final int MSG_REPORT = 1;
    private static final int REPORT_INTERVAL = 10 * 60 * 1000;
    private static long mLastReportTime;
    private static boolean mLastReportSuccess;

    @Override
    public void onCreate() {
        super.onCreate();
        XLogger.i(">>> AliveReportService--create---");
        EventBus.getDefault().register(this);
        mWorkThread = new HandlerThread("AliveReportService");
        mWorkThread.start();
        mHandler = new CmdHandler(mWorkThread.getLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeMessages(MSG_REPORT);
        if (mRequestCall != null) {
            mRequestCall.cancel();
            mRequestCall = null;
        }
        mWorkThread.quit();
        XLogger.i("<<< AliveReportService--destroy---");
    }

    @Subscribe
    public void onLogin(EventLogin event) {
        XLogger.i(">>> AliveReportService--start---");
        mHandler.removeMessages(MSG_REPORT);
        mHandler.sendMessage(createStartMessage(event.getToken()));
    }

    @Subscribe
    public void onLogout(EventLogout event) {
        mHandler.removeMessages(MSG_REPORT);
        mLastReportTime = 0;
        XLogger.i("<<< AliveReportService--stop---");
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
                    sendMessageDelayed(createStartMessage((String) msg.obj), 30000);
                    break;
            }
        }
    }

    private static void reportAlive(final String token) {
        XLogger.i(">>>reportAlive: " + token);
        FormBody formBody = new FormBody.Builder()
                .add("token", token)
                .add("deviceId", DeviceInfoUtils.getDeviceId(Init.getContext()))
                .build();
        Request request = new Request.Builder()
                .url("http://www.baidu.com/") //FIXME
                .post(formBody)
                .build();
        mRequestCall = OkHttpUtil.getInstance().getClient().newCall(request);
        mRequestCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mLastReportSuccess = false;
                XLogger.i("<<<reportAlive: " + token + " failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mLastReportSuccess = response.isSuccessful();
                XLogger.i("<<<reportAlive: " + token + (mLastReportSuccess ? " sucess" : " failed"));
            }
        });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private static int NOTIFICATION_ID = 0x2333;

    void setForeground() {
        startForeground(NOTIFICATION_ID, new Notification());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startService(new Intent(this, InnerService.class));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setForeground();
        return Service.START_STICKY;
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
