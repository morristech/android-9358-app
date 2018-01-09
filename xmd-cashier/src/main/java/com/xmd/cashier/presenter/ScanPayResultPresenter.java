package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.ScanPayResultContract;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.TradeManager;

/**
 * Created by zr on 17-5-16.
 */

public class ScanPayResultPresenter implements ScanPayResultContract.Presenter {
    private static final String TAG = "ScanPayResultPresenter";
    private Context mContext;
    private ScanPayResultContract.View mView;

    public ScanPayResultPresenter(Context context, ScanPayResultContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        finishOnlinePay();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onConfirm() {
        mView.finishSelf();
    }

    private void finishOnlinePay() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款微信支付宝支付成功(自动打印小票)");
        TradeManager.getInstance().finishPay(mContext, new Callback0<Void>() {
            @Override
            public void onFinished(Void result) {
                // do nothing
            }
        });
    }
}
