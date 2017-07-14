package com.xmd.technician.common;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by sdcm on 15-10-28.
 */
@Deprecated
public class ThreadManager {

    public static final int THREAD_TYPE_BACKGROUND = 0x01;
    public static final int THREAD_TYPE_MAIN = 0x02;

    private static BackgroundThread mBackgroundThread;

    private static Handler mBackgroundHandler;
    private static Handler mMainHandler;

    public static void initialize() {
        mBackgroundThread = new BackgroundThread();
        mBackgroundThread.start();

        mMainHandler = new Handler(Looper.getMainLooper());
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    public static void postDelayed(int threadType, Runnable r, long delayed) {
        switch (threadType) {
            case THREAD_TYPE_BACKGROUND:
                mBackgroundHandler.postDelayed(r, delayed);
                break;
            case THREAD_TYPE_MAIN:
                mMainHandler.postDelayed(r, delayed);
                break;
        }
    }

    public static void postRunnable(int threadType, Runnable r) {
        postDelayed(threadType, r, 0);
    }

    static class BackgroundThread extends HandlerThread {

        public BackgroundThread() {
            super("BackgroundThread", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        }
    }
}
