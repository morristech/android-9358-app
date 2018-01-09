package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.CashPayContract;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.TradeManager;

import rx.Subscription;

/**
 * Created by zr on 17-8-17.
 */

public class CashPayPresenter implements CashPayContract.Presenter {
    private static final String TAG = "CashPayPresenter";
    private Context mContext;
    private CashPayContract.View mView;
    private Subscription mCashPaySubscription;

    public CashPayPresenter(Context context, CashPayContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mView.showAmount(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(mView.getAmount())));
        mView.showCashBtn();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mCashPaySubscription != null) {
            mCashPaySubscription.unsubscribe();
        }
    }

    @Override
    public void onCashPay() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mCashPaySubscription != null) {
            mCashPaySubscription.unsubscribe();
        }
        mView.disableCashBtn();
        mView.showLoading();
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款发起现金支付：" + RequestConstant.URL_REPORT_TRADE_DATA);
        mCashPaySubscription = TradeManager.getInstance().cashPay(mView.getAmount(), new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款发起现金支付---成功");
                PosFactory.getCurrentCashier().textToSound("买单成功");
                mView.hideLoading();
                mView.showCashSuccess();
                finishCashPay();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款发起现金支付---失败:" + error);
                mView.showToast(error);
                mView.hideLoading();
                mView.enableCashBtn();
            }
        });
    }

    private void finishCashPay() {
        TradeManager.getInstance().finishPay(mContext, new Callback0<Void>() {
            @Override
            public void onFinished(Void result) {
                mView.finishSelf();
            }
        });
    }

    @Override
    public void onNavigationBack() {
        UiNavigation.gotoConfirmActivity(mContext, null);
        mView.finishSelf();
    }
}
