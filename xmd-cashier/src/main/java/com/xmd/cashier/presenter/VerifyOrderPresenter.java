package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyOrderContract;
import com.xmd.cashier.dal.bean.OrderInfo;
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
 * Created by zr on 2017/4/16 0016.
 */

public class VerifyOrderPresenter implements VerifyOrderContract.Presenter {
    private Context mContext;
    private VerifyOrderContract.View mView;
    private Subscription mModifyOrderStatusSubscription;

    public VerifyOrderPresenter(Context context, VerifyOrderContract.View view) {
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
        if (mModifyOrderStatusSubscription != null) {
            mModifyOrderStatusSubscription.unsubscribe();
        }
    }

    @Override
    public void onVerify(final OrderInfo info) {
        mView.showLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }

        if (mModifyOrderStatusSubscription != null) {
            mModifyOrderStatusSubscription.unsubscribe();
        }

        mModifyOrderStatusSubscription = VerifyManager.getInstance().verifyPaidOrder(info.orderNo, AppConstants.PAID_ORDER_OP_VERIFIED, new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                mView.hideLoading();
                mView.showToast("核销成功");
                printStep(info);
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("核销失败:" + error);
            }
        });
    }

    private void printStep(final OrderInfo info) {
        mView.showLoading();
        VerifyManager.getInstance().printOrder(info, true, new Callback() {
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
                                                VerifyManager.getInstance().printOrder(info, false, null);
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
