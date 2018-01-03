package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.CashPayContract;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

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
        XLogger.i(TAG, "补收款发起现金支付");
        mCashPaySubscription = TradeManager.getInstance().cashPay(mView.getAmount(), new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                XLogger.i(TAG, "补收款发起现金支付---成功");
                mView.hideLoading();
                mView.showCashSuccess();
                finishCashPay();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, "补收款发起现金支付---失败:" + error);
                mView.showToast(error);
                mView.hideLoading();
                mView.enableCashBtn();
            }
        });
    }

    private void printStep() {
        mView.showLoading();
        TradeManager.getInstance().printPosPay(true, new Callback() {
            @Override
            public void onSuccess(Object o) {
                mView.hideLoading();
                new CustomAlertDialogBuilder(mContext)
                        .setMessage("是否需要打印客户联小票?")
                        .setPositiveButton("打印", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                TradeManager.getInstance().getCurrentTrade().isClient = true;
                                finishCashPay();
                            }
                        })
                        .setNegativeButton("完成交易", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                TradeManager.getInstance().getCurrentTrade().isClient = false;
                                finishCashPay();
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("打印异常:" + error);
                finishCashPay();
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
