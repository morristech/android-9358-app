package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyPrizeContract;
import com.xmd.cashier.dal.bean.PrizeInfo;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.m.network.BaseBean;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 16-12-12.
 */

public class VerifyPrizePresenter implements VerifyPrizeContract.Presenter {
    private Context mContext;
    private VerifyPrizeContract.View mView;
    private Subscription mVerifyPrizeSubscription;

    public VerifyPrizePresenter(Context context, VerifyPrizeContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onClickVerify(final PrizeInfo info) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mVerifyPrizeSubscription != null) {
            mVerifyPrizeSubscription.unsubscribe();
        }
        mView.showLoadingView();
        mVerifyPrizeSubscription = VerifyManager.getInstance().verifyLuckyWheel(mView.getCode(), new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                mView.hideLoadingView();
                mView.showToast("操作成功");
                printStep(info);
            }

            @Override
            public void onError(String error) {
                XLogger.i(error);
                mView.hideLoadingView();
                mView.showError("操作失败：" + error);
            }
        });
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mVerifyPrizeSubscription != null) {
            mVerifyPrizeSubscription.unsubscribe();
        }
    }

    private void printStep(final PrizeInfo info) {
        mView.showLoading();
        VerifyManager.getInstance().printPrize(info, true, new Callback() {
            @Override
            public void onSuccess(Object o) {
                mView.hideLoading();
                new CustomAlertDialogBuilder(mContext)
                        .setMessage("是否需要打印客户联小票?")
                        .setPositiveButton("打印", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Observable
                                        .create(new Observable.OnSubscribe<Void>() {
                                            @Override
                                            public void call(Subscriber<? super Void> subscriber) {
                                                VerifyManager.getInstance().printPrize(info, false, null);
                                                subscriber.onNext(null);
                                                subscriber.onCompleted();
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe();
                                mView.finishSelf();
                            }
                        })
                        .setNegativeButton("完成核销", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mView.finishSelf();
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("打印异常:" + error);
                mView.finishSelf();
            }
        });
    }
}
