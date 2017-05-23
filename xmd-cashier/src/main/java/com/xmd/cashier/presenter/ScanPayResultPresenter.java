package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.ScanPayResultContract;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.TradeManager;

/**
 * Created by zr on 17-5-16.
 */

public class ScanPayResultPresenter implements ScanPayResultContract.Presenter {
    private Context mContext;
    private ScanPayResultContract.View mView;

    public ScanPayResultPresenter(Context context, ScanPayResultContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onConfirm() {
        TradeManager.getInstance().finishPay(mContext, AppConstants.TRADE_STATUS_SUCCESS, new Callback0<Void>() {
            @Override
            public void onFinished(Void result) {
                mView.finishSelf();
            }
        });
    }
}
