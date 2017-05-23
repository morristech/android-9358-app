package com.xmd.cashier.common;

import android.content.Context;
import android.os.Handler;

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

    public static boolean postToUI(Runnable runnable) {
        return mHandler.post(runnable);
    }
}
