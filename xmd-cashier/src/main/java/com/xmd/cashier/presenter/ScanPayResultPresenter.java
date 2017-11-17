package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.ScanPayResultContract;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.Callback0;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

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
        finishOnlinePay();
    }

    private void printStep() {
        mView.showLoading();
        TradeManager.getInstance().printOnlinePay(true, new Callback() {
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
                                finishOnlinePay();
                            }
                        })
                        .setNegativeButton("完成交易", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                TradeManager.getInstance().getCurrentTrade().isClient = false;
                                finishOnlinePay();
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("打印异常:" + error);
                finishOnlinePay();
            }
        });
    }

    private void finishOnlinePay() {
        TradeManager.getInstance().finishPay(mContext, new Callback0<Void>() {
            @Override
            public void onFinished(Void result) {
                mView.finishSelf();
            }
        });
    }
}
