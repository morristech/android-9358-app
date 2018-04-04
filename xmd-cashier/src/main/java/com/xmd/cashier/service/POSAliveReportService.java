package com.xmd.cashier.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.common.Http;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.pos.PosImpl;
import com.xmd.m.network.EventTokenExpired;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by lyf on 2018/4/2
 * 向服务器汇报存活状态
 */

public class POSAliveReportService extends Service {
    private final static String TAG = "[心跳上报]";
    private HandlerThread mWorkThread;
    private CmdHandler mHandler;
    private static final int MSG_REPORT = 1;
    private static int REPORT_INTERVAL = 5 * 60 * 1000;
    private static int CHECK_INTERVAL = 5 * 60 * 1000;
    private static long mLastReportTime;
    private static boolean mLastReportSuccess;

    // 记录服务器返回的心跳包频率时间间隔，单位秒
    private static int mIntervalFromServer = 5 * 60;


    public static void start() {
        Context context = MainApplication.getInstance().getApplicationContext();
        context.startService(new Intent(context, POSAliveReportService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        XLogger.i(">>> POSAliveReportService--create---");
        mWorkThread = new HandlerThread("POSAliveReportService");
        mWorkThread.start();
        mHandler = new CmdHandler(mWorkThread.getLooper());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeMessages(MSG_REPORT);
        mWorkThread.quit();
        XLogger.i("<<< POSAliveReportService--destroy---");
    }

    @Subscribe(sticky = true)
    public void onLogin(EventLogin event) {
        XLogger.i(">>> POSAliveReportService--start---");
        mHandler.removeMessages(MSG_REPORT);
        mHandler.sendMessage(createStartMessage(event.getToken()));
    }

    @Subscribe(sticky = true)
    public void onLogout(EventLogout event) {
        mHandler.removeMessages(MSG_REPORT);
        mLastReportTime = 0;
        XLogger.i("<<< POSAliveReportService--stop---");
    }

    @Subscribe
    public void onTokenExpire(EventTokenExpired tokenExpired) {
        mHandler.removeMessages(MSG_REPORT);
        mLastReportTime = 0;
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

        HashMap<String, Object> params = new HashMap<>();
        params.put("token", token);
        params.put("posEn", PosImpl.getInstance().getPosIdentifierNo());
        params.put("posVersion", Utils.getAppVersionName());
        params.put("networkType", Utils.getNetworkType(MainApplication.getInstance().getApplicationContext()));

        String aliveReportUrl = SPManager.getInstance().getSpaServerAddress() + RequestConstant.URL_ALIVE_REPORT;
//        String aliveReportUrl = "http://192.168.2.86:8080" + RequestConstant.URL_ALIVE_REPORT;
        XLogger.i(TAG, "request url: " + aliveReportUrl + ", \n params: " + params.toString());

        Http.ResponseData result = Http.post(aliveReportUrl, params);
        if (result.isResponseOK()) {
            mLastReportSuccess = true;
            String jsonStr = result.responseMsg;
            mIntervalFromServer = getIntervalFromServer(jsonStr);
            if ((mIntervalFromServer * 1000) != REPORT_INTERVAL) {
                REPORT_INTERVAL = mIntervalFromServer * 1000;
                CHECK_INTERVAL = REPORT_INTERVAL;
            }
            XLogger.v(TAG, "success: " + result.responseMsg);
        } else {
            mLastReportSuccess = false;
            XLogger.e(TAG, "alive report fail: " + result.error.toString());
        }
    }

    /**
     * 获取服务器返回的心跳时间间隔
     * @param jsonStr
     * @return 服务器返回的心跳时间间隔
     */
    private static int getIntervalFromServer(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            int statusCode = jsonObject.getInt("statusCode");
            if (statusCode != 200) {
                return 5 * 60;
            }
            JSONObject respData = jsonObject.getJSONObject("respData");
            int interval = respData.getInt("interval");
            return interval;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 默认返回时间间隔为300秒（5分钟）
            return 5 * 60;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

}
