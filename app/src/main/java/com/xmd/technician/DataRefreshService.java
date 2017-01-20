package com.xmd.technician;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.xmd.technician.common.Logger;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfoManager;
import com.xmd.technician.onlinepaynotify.model.PayNotifyNewDataEvent;

import rx.Subscription;

public class DataRefreshService extends Service {
    private Thread workThread;
    private boolean mRun;

    private Subscription mPayNotifySubscription;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (workThread == null) {
            Logger.i("-------------start background service-----------");
            workThread = new WorkThread();
            //买单通知
            mPayNotifySubscription = RxBus.getInstance().toObservable(PayNotifyNewDataEvent.class).subscribe(this::handlePayNotifyNewDataEvent);


            mRun = true;
            workThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (workThread != null) {
            Logger.i("-------------stop background service-----------");

            mPayNotifySubscription.unsubscribe();


            mRun = false;
            workThread.interrupt();
            workThread = null;
        }
    }


    private class WorkThread extends Thread {
        @Override
        public void run() {
            while (mRun) {
                Logger.i("-------------work thread running-----------");
                //定时刷新买单通知
                PayNotifyInfoManager.getInstance().getRecentDataAndSendNotify(Constant.PAY_NOTIFY_MAIN_PAGE_TIME_LIMIT);
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            Logger.i("-------------work thread stopped-----------");
        }
    }


    //有新的买单通知
    public void handlePayNotifyNewDataEvent(PayNotifyNewDataEvent event) {
        //检查APP是否处于后台，如果处于后台，发送通知到系统通知栏
        //TODO
    }
}
