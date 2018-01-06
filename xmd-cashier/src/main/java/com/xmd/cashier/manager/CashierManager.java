package com.xmd.cashier.manager;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by heyangya on 16-8-23.
 */

public class CashierManager {
    private static CashierManager mInstance = new CashierManager();
    private IPos mCashier;
    private AtomicBoolean mCanCallPay = new AtomicBoolean(true);
    private Handler mCashierManagerHandler;
    private PayInfo mPayInfo;

    private CashierManager() {
        mCashier = PosFactory.getCurrentCashier();
        mPayInfo = new PayInfo();
        HandlerThread handlerThread = new HandlerThread("thread-CashierManager");
        handlerThread.start();
        mCashierManagerHandler = new PayHandler(handlerThread.getLooper());
    }

    public static CashierManager getInstance() {
        return mInstance;
    }

    public void init(Context context, final Callback<?> callback) {
        mCashier.init(context, callback);
    }

    private static class PayInfo {
        public String tradeNo;
        public int money;
        public int payType;
        public Object payResult;

        public Context context;
        public PayCallback<Object> callback;
        public IPos cashier;
        public AtomicBoolean canCallPay;
        public boolean isUserCanceled;

        public void clear() {
            tradeNo = null;
            money = 0;
            payType = AppConstants.PAY_TYPE_UNKNOWN;
            payResult = null;
            context = null;
            callback = null;
            cashier = null;
            isUserCanceled = false;
        }
    }

    public String getAppCode() {
        return mCashier.getAppCode();
    }

    public boolean needCheckUpdate() {
        return mCashier.needCheckUpdate();
    }


    public void pay(Context context, final String tradeNo, int money, final PayCallback<Object> callback) {
        //防止重入
        if (!mCanCallPay.compareAndSet(true, false)) {
            XLogger.i("pos pay not finished, can not enter!");
            mPayInfo.clear();
            mPayInfo.callback = callback;
            mPayInfo.callback.onResult("支付冲突，当前有未完成支付，请重启POS！", mPayInfo);
            return;
        }
        mPayInfo.clear();
        mPayInfo.context = context;
        mPayInfo.money = money;
        mPayInfo.tradeNo = tradeNo;
        mPayInfo.callback = callback;
        mPayInfo.cashier = mCashier;
        mPayInfo.canCallPay = mCanCallPay;
        mPayInfo.isUserCanceled = false;

        mCashierManagerHandler.obtainMessage(PayHandler.MSG_PAY_CREATE, mPayInfo).sendToTarget();
    }

    public String getTradeNo(Object o) {
        if (o instanceof PayInfo) {
            PayInfo payInfo = ((PayInfo) o);
            if (payInfo.payResult != null) {
                return mCashier.getTradeNo(payInfo.payResult);
            }
        }
        return null;
    }

    public int getPayType(Object o) {
        if (o instanceof PayInfo) {
            PayInfo payInfo = ((PayInfo) o);
            if (payInfo.payType != AppConstants.PAY_TYPE_UNKNOWN) {
                return payInfo.payType;
            } else if (payInfo.payResult != null) {
                return mCashier.getPayType(payInfo.payResult);
            }
        }
        return AppConstants.PAY_TYPE_UNKNOWN;
    }


    public String getCertificate(Object o) {
        if (o instanceof PayInfo) {
            PayInfo payInfo = ((PayInfo) o);
            if (payInfo.payResult != null) {
                return mCashier.getPayCertificate(payInfo.payResult);
            }
        }
        return null;
    }

    public String getExtraInfo(Object o) {
        if (o instanceof PayInfo) {
            PayInfo payInfo = ((PayInfo) o);
            if (payInfo.payResult != null) {
                return mCashier.getExtraInfo(payInfo.payResult);
            }
        }
        return null;
    }

    public boolean isUserCancel(Object o) {
        if (o instanceof PayInfo) {
            PayInfo payInfo = ((PayInfo) o);
            if (payInfo.isUserCanceled) {
                return true;
            }
            if (payInfo.payResult != null) {
                return mCashier.isUserCancel(payInfo.payResult);
            }
        }
        return false;
    }

    public String getCashierAppPackageName() {
        return mCashier.getPackageName();
    }

    public void onChoicePayType(int type) {
        mCashierManagerHandler.obtainMessage(PayHandler.MSG_PAY_TYPE, type, 0).sendToTarget();
    }

    public void onCancelChoicePayType() {
        mCanCallPay.set(true);
        mPayInfo.context = null;
        mPayInfo.isUserCanceled = true;
        mPayInfo.callback.onResult("支付取消", mPayInfo);
    }

    public static class PayHandler extends Handler {
        public static final int MSG_PAY_CREATE = 0x1;
        public static final int MSG_PAY_TYPE = 0x2;

        private PayInfo mPayInfo;

        public PayHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PAY_CREATE:
                    onPayCreate(msg);
                    break;
                case MSG_PAY_TYPE:
                    onPayType(msg);
                    break;
            }
        }

        private void onPayCreate(Message msg) {
            mPayInfo = (PayInfo) msg.obj;
            if (mPayInfo.cashier.needChoicePayTypeByCaller()) {
                UiNavigation.gotoPayTypeChoice(mPayInfo.context);
            } else {
                msg.arg1 = AppConstants.PAY_TYPE_UNKNOWN;
                onPayType(msg);
            }
        }

        private void onPayType(Message msg) {
            mPayInfo.payType = msg.arg1;
            if (mPayInfo.payType == AppConstants.PAY_TYPE_CASH) {
                mPayCallback.onResult(null, null);//现金支付,直接返回成功
            } else {
                mPayInfo.cashier.pay(mPayInfo.context, mPayInfo.tradeNo, mPayInfo.money, mPayInfo.payType, mPayCallback);
            }
        }

        private PayCallback<Object> mPayCallback = new PayCallback<Object>() {
            @Override
            public void onResult(final String error, final Object o) {
                ThreadPoolManager.postToUI(new Runnable() {
                    @Override
                    public void run() {
                        mPayInfo.context = null;//释放资源
                        mPayInfo.payResult = o;
                        mPayInfo.callback.onResult(error, mPayInfo);
                        mPayInfo.canCallPay.set(true);
                        mPayInfo = null;
                    }
                });
            }
        };
    }
}
