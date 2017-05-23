package com.xmd.manager.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by sdcm on 15-11-19.
 */
public class HeartBeatTimer {

    private static HeartBeatTimer sInstance;
    private final ScheduledExecutorService mInactivityTimer;
    private ScheduledFuture<?> inactivityFuture = null;

    private HeartBeatTimer() {
        mInactivityTimer = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
    }

    public static HeartBeatTimer getInstance() {
        if (sInstance == null) {
            synchronized (HeartBeatTimer.class) {
                if (sInstance == null) {
                    sInstance = new HeartBeatTimer();
                }
            }
        }
        return sInstance;
    }

    private synchronized void cancel() {
        if (inactivityFuture != null) {
            inactivityFuture.cancel(true);
            inactivityFuture = null;
        }
    }

    public void shutdown() {
        cancel();
        mInactivityTimer.shutdown();
        synchronized (HeartBeatTimer.class) {
            sInstance = null;
        }
    }

    public void start(int period, final Runnable runnable) {
        cancel();
        inactivityFuture = mInactivityTimer.scheduleAtFixedRate(runnable, 0, period, TimeUnit.SECONDS);
    }

    private static final class DaemonThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }
    }
}
