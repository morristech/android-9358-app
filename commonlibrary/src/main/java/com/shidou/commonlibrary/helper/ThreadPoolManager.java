package com.shidou.commonlibrary.helper;

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

    public static void init(Context context) {
        mHandler = new Handler(context.getApplicationContext().getMainLooper());
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

    public synchronized static boolean postToUIDelayed(Runnable runnable, long delay) {
        if (mHandler == null) {
            throw new RuntimeException("Thread pool manager not init!");
        }
        return mHandler.postDelayed(runnable, delay);
    }
}
