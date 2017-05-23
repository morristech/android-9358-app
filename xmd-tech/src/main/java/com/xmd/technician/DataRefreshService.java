package com.xmd.technician;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.xmd.technician.common.Logger;
import com.xmd.technician.event.EventLogout;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfoManager;

import rx.Subscription;

public class DataRefreshService extends Service {
    private Thread workThread;
    private boolean mRun;
    private final static String EXTRA_CMD = "cmd";
    private final static String EXTRA_CMD_DATA = "cmd_data";

    private final static int CMD_REFRESH_PERSONAL_DATA = 1;
    private final static int CMD_CHECK_PAY_NOTIFY = 2;
    private final static int CMD_CHECK_HELLO_REPLY = 3;

    private boolean mRefreshPersonalData;
    private boolean mRefreshPayNotify;
    private boolean mRefreshHelloReply;

    private Subscription mTokenExpiredSubscription;

    public DataRefreshService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mTokenExpiredSubscription = RxBus.getInstance().toObservable(EventLogout.class).subscribe(this::handleLogoutEvent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (workThread == null) {
            Logger.i("-------------start background service-----------");
            workThread = new WorkThread();

            mRun = true;
            workThread.start();
        }
        if (intent != null) {
            int cmd = intent.getIntExtra(EXTRA_CMD, -1);
            switch (cmd) {
                case CMD_CHECK_PAY_NOTIFY:
                    mRefreshPayNotify = intent.getBooleanExtra(EXTRA_CMD_DATA, false);
                    Logger.i("set refresh pay notify to " + mRefreshPayNotify);
                    break;
                case CMD_REFRESH_PERSONAL_DATA:
                    mRefreshPersonalData = intent.getBooleanExtra(EXTRA_CMD_DATA, false);
                    Logger.i("set refresh personal data to " + mRefreshPersonalData);
                    break;
                case CMD_CHECK_HELLO_REPLY:
                    mRefreshHelloReply = intent.getBooleanExtra(EXTRA_CMD_DATA, false);
                    Logger.i("set refresh reply data to " + mRefreshHelloReply);
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (workThread != null) {
            Logger.i("-------------stop background service-----------");
            mRun = false;
            workThread.interrupt();
            workThread = null;
        }
        mTokenExpiredSubscription.unsubscribe();
    }


    private class WorkThread extends Thread {
        @Override
        public void run() {
            while (mRun) {
                Logger.i("-------------work thread running-----------");
                if (mRefreshPayNotify) {
                    //定时刷新买单通知
                    Logger.i("work thread: check pay notify");
                    PayNotifyInfoManager.getInstance().checkNewPayNotify();
                }
                if (mRefreshPersonalData) {
                    Logger.i("work thread: refresh personal data");
                    LoginTechnician.getInstance().getTechPersonalData();
                }
                if (mRefreshHelloReply) {
                    Logger.i("work thread: refresh reply data");
                    HelloSettingManager.getInstance().checkHelloReply();
                }

                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            Logger.i("-------------work thread stopped-----------");
        }
    }

    public static void start() {
        Context context = TechApplication.getAppContext();
        context.startService(new Intent(context, DataRefreshService.class));
    }

    public static void stop() {
        Context context = TechApplication.getAppContext();
        context.stopService(new Intent(context, DataRefreshService.class));
    }

    public void handleLogoutEvent(EventLogout event) {
        mRefreshPayNotify = false;
        mRefreshPersonalData = false;
        mRefreshHelloReply = false;
    }

    public static void refreshPersonalData(boolean on) {
        Context context = TechApplication.getAppContext();
        Intent intent = new Intent(context, DataRefreshService.class);
        intent.putExtra(EXTRA_CMD, DataRefreshService.CMD_REFRESH_PERSONAL_DATA);
        intent.putExtra(EXTRA_CMD_DATA, on);
        context.startService(intent);
    }

    public static void refreshPayNotify(boolean on) {
        Context context = TechApplication.getAppContext();
        Intent intent = new Intent(context, DataRefreshService.class);
        intent.putExtra(EXTRA_CMD, DataRefreshService.CMD_CHECK_PAY_NOTIFY);
        intent.putExtra(EXTRA_CMD_DATA, on);
        context.startService(intent);
    }

    public static void refreshHelloReply(boolean on) {
        Context context = TechApplication.getAppContext();
        Intent intent = new Intent(context, DataRefreshService.class);
        intent.putExtra(EXTRA_CMD, DataRefreshService.CMD_CHECK_HELLO_REPLY);
        intent.putExtra(EXTRA_CMD_DATA, on);
        context.startService(intent);
    }
}
