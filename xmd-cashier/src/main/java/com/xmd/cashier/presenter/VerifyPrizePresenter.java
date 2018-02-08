package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyPrizeContract;
import com.xmd.cashier.dal.bean.PrizeInfo;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;
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
                printNormal(info);
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
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

    private void printNormal(final PrizeInfo info) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        VerifyManager.getInstance().printPrize(info, true);
                        if (SPManager.getInstance().getPrintClientSwitch()) {
                            VerifyManager.getInstance().printPrize(info, false);
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
