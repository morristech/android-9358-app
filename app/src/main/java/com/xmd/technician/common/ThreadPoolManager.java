package com.xmd.technician.common;

import android.content.Context;
import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by heyangya on 16-8-30.
 */

public class ThreadPoolManager {
    private static ExecutorService mExecutorService = Executors.newCachedThreadPool();
    private static Handler mHandler;
    private static Context mApplicationContext;

    public static void init(Context context) {
        mApplicationContext = context.getApplicationContext();
        mHandler = new Handler(mApplicationContext.getMainLooper());
    }

    public static Future<?> run(Runnable runnable) {
        return mExecutorService.submit(runnable);
    }

    public synchronized static boolean postToUI(Runnable runnable) {
        if (mHandler == null) {
            throw new RuntimeException("Thread pool manager not init!");
        }
        return mHandler.post(runnable);
    }
}
