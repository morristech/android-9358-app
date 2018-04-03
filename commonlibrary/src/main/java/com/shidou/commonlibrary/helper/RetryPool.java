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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by heyangya on 16-3-21.
 * 重试
 */
public class RetryPool {
    private Handler mHandler;
    private Map<Integer, Future> mFutureMap = new HashMap<>();
    private Map<Integer, Message> mMessageMap = new HashMap<>();

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
        public static AtomicInteger INDEX = new AtomicInteger(0);
        private final int index;
        private long currentRetryInterval; //当前重试间隔
        private float baseRetryIntervalMulti; //倍数，本次尝试间隔=(倍数^当前尝试次数)*基本尝试间隔;
        private RetryExecutor runnable;
        private RetryPool retryPool;
        private boolean exit;
        private Object lock;
        private Map<Integer, Future> futureMap;

        private int currentRetryCount;

        public int getCurrentRetryCount() {
            return currentRetryCount;
        }

        public int getIndex() {
            return index;
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
            this.index = INDEX.incrementAndGet();
        }

        public void setFutureMap(Object lock, Map<Integer, Future> futureMap) {
            this.lock = lock;
            this.futureMap = futureMap;
        }

        @Override
        public void run() {
//            XLogger.v("", "----call runnable----" + runnable);
            currentRetryCount++;
            boolean continueRun = !exit;
            try {
                continueRun = continueRun && !runnable.run();
            } catch (Exception e) {
                XLogger.i("runnable exception...ignore");
            }
            if (continueRun) {
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
                if (msg.what != 1) {
                    return;
                }
                synchronized (mFutureMapLock) {
                    RetryRunnable retryRunnable = (RetryRunnable) msg.obj;
                    retryRunnable.exit = false;
                    retryRunnable.setFutureMap(mFutureMapLock, mFutureMap);
                    Future future = mExecutorService.submit((RetryRunnable) msg.obj);
                    int index = ((RetryRunnable) msg.obj).getIndex();
                    mFutureMap.put(index, future);
                    mMessageMap.remove(index);
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
        Message msg = mHandler.obtainMessage(1, retryRunnable);
        synchronized (mFutureMapLock) {
            mMessageMap.put(retryRunnable.getIndex(), msg);
            msg.sendToTarget();
        }
    }

    public void postWorkDelay(RetryRunnable retryRunnable, long delay) {
        retryRunnable.retryPool = this;
        Message msg = mHandler.obtainMessage(1, retryRunnable);
        synchronized (mFutureMapLock) {
            mMessageMap.put(retryRunnable.getIndex(), msg);
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
            mMessageMap.remove(retryRunnable.getIndex());
            mHandler.removeCallbacks(retryRunnable);
            Future future = mFutureMap.remove(retryRunnable.getIndex());
            if (future != null) {
                future.cancel(true);
            }
        }
    }
}