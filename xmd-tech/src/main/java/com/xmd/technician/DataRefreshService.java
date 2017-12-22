package com.xmd.technician;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.xmd.app.event.EventLogout;
import com.xmd.technician.common.Logger;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;

import rx.Subscription;

public class DataRefreshService extends Service {
    private final static String EXTRA_CMD = "cmd";
    private final static String EXTRA_CMD_DATA = "cmd_data";
    private final static int CMD_REFRESH_PERSONAL_DATA = 1;
    private final static int CMD_CHECK_PAY_NOTIFY = 2;
    private final static int CMD_CHECK_HELLO_REPLY = 3;
    private Thread workThread;
    private boolean mRun;
    private boolean mRefreshPersonalData;
    private boolean mRefreshHelloReply;

    private Subscription mTokenExpiredSubscription;

    public DataRefreshService() {

    }

    public static void start() {
        Context context = TechApplication.getAppContext();
        context.startService(new Intent(context, DataRefreshService.class));
    }

    public static void stop() {
        Context context = TechApplication.getAppContext();
        context.stopService(new Intent(context, DataRefreshService.class));
    }

    public static void refreshPersonalData(boolean on) {
        Context context = TechApplication.getAppContext();
        Intent intent = new Intent(context, DataRefreshService.class);
        intent.putExtra(EXTRA_CMD, DataRefreshService.CMD_REFRESH_PERSONAL_DATA);
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

    public void handleLogoutEvent(EventLogout event) {
        mRefreshPersonalData = false;
        mRefreshHelloReply = false;
    }

    private class WorkThread extends Thread {
        @Override
        public void run() {
            while (mRun) {
                Logger.i("-------------work thread running-----------");
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
}
