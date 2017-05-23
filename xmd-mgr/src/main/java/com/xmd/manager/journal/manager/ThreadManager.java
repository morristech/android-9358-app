package com.xmd.manager.journal.manager;

import android.content.Context;
import android.os.Handler;

import com.xmd.manager.ManagerApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by heyangya on 16-8-30.
 */

public class ThreadManager {
    private static ExecutorService mExecutorService = Executors.newCachedThreadPool();
    private static Handler mHandler;


    public static void init(Context context) {
        mHandler = new Handler(context.getMainLooper());
    }

    public static Future<?> run(Runnable runnable) {
        return mExecutorService.submit(runnable);
    }

    public synchronized static boolean postToUI(Runnable runnable) {
        if (mHandler == null) {
            init(ManagerApplication.getAppContext());
        }
        return mHandler.post(runnable);
    }
}
