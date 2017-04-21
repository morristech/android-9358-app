package com.shidou.commonlibrary.helper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by heyangya on 16-3-21.
 * 重试
 */
public class RetryPool {
    private Handler mHandler;
    private Map<RetryRunnable, Future> mFutureMap = new HashMap<>();
    private Map<RetryRunnable, Message> mMessageMap = new HashMap<>();

    private final Object mFutureMapLock = new Object();
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();

    private static RetryPool mInstance;

    public static RetryPool getInstance() {
        if (mInstance == null) {
            mInstance = new RetryPool();
        }
        return mInstance;
    }

    public interface RetryExecutor {
        boolean run(); //返回true则不再执行
    }

    public static class RetryRunnable implements Runnable {
        private long currentRetryInterval; //当前重试间隔
        private float baseRetryIntervalMulti; //倍数，本次尝试间隔=(倍数^当前尝试次数)*基本尝试间隔;
        private RetryExecutor runnable;
        private RetryPool retryPool;
        private boolean exit;
        private Object lock;
        private Map<RetryRunnable, Future> futureMap;

        private int currentRetryCount;

        public int getCurrentRetryCount() {
            return currentRetryCount;
        }

        /**
         * 重试对像
         *
         * @param baseInterval 基本间隔
         * @param multi        倍数
         * @param runnable     任务, return true表示成功，不再执行
         */
        public RetryRunnable(long baseInterval, float multi, @NonNull RetryExecutor runnable) {
            this.baseRetryIntervalMulti = multi;
            this.runnable = runnable;
            this.currentRetryInterval = baseInterval;
        }

        public void setFutureMap(Object lock, Map<RetryRunnable, Future> futureMap) {
            this.lock = lock;
            this.futureMap = futureMap;
        }

        @Override
        public void run() {
//            XLogger.v("", "----call runnable----" + runnable);
            currentRetryCount++;
            if (!exit && !runnable.run()) {
                currentRetryInterval = (long) (currentRetryInterval * baseRetryIntervalMulti);
                retryPool.postWorkDelay(this, currentRetryInterval);
            } else {
                synchronized (lock) {
                    futureMap.remove(this);
                }
            }
        }

        public void exit() {
            this.exit = true;
        }
    }


    private RetryPool() {
        HandlerThread mHandlerThread = new HandlerThread("retryPool");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                synchronized (mFutureMapLock) {
                    RetryRunnable retryRunnable = (RetryRunnable) msg.obj;
                    retryRunnable.exit = false;
                    retryRunnable.setFutureMap(mFutureMapLock, mFutureMap);
                    Future future = mExecutorService.submit((RetryRunnable) msg.obj);
                    mFutureMap.put((RetryRunnable) msg.obj, future);
                    mMessageMap.remove(msg.obj);
                    XLogger.d("size:" + mFutureMap.size());
                }
            }
        };
    }

    /**
     * 添加工作
     *
     * @param retryRunnable
     */
    public void postWork(RetryRunnable retryRunnable) {
        retryRunnable.retryPool = this;
        Message msg = mHandler.obtainMessage(0, retryRunnable);
        synchronized (mFutureMapLock) {
            mMessageMap.put(retryRunnable, msg);
            msg.sendToTarget();
        }
    }

    public void postWorkDelay(RetryRunnable retryRunnable, long delay) {
        retryRunnable.retryPool = this;
        Message msg = mHandler.obtainMessage(0, retryRunnable);
        synchronized (mFutureMapLock) {
            mMessageMap.put(retryRunnable, msg);
            mHandler.sendMessageDelayed(msg, delay);
        }
    }

    /**
     * 移除工作
     *
     * @param retryRunnable
     */
    public void removeWork(RetryRunnable retryRunnable) {
        retryRunnable.exit();
        synchronized (mFutureMapLock) {
            Message msg = mMessageMap.remove(retryRunnable);
            if (msg != null) {
                mHandler.removeMessages(0, retryRunnable);
            }
            Future future = mFutureMap.remove(retryRunnable);
            if (future != null) {
                future.cancel(true);
            }
        }
    }

}
