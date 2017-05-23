package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.SettleDetailContract;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.SettleManager;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-4-7.
 */

public class SettleDetailPresenter implements SettleDetailContract.Presenter {
    private Context mContext;
    private SettleDetailContract.View mView;

    private Subscription mGetSummaryByIdSubscription;

    public SettleDetailPresenter(Context context, SettleDetailContract.View view) {
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
        if (mGetSummaryByIdSubscription != null) {
            mGetSummaryByIdSubscription.unsubscribe();
        }
    }

    @Override
    public void getSummaryById(String recordId) {
        if (TextUtils.isEmpty(recordId)) {
            mView.showError("结算记录数据异常，请稍后重试");
            return;
        }
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        mView.showLoading();
        if (mGetSummaryByIdSubscription != null) {
            mGetSummaryByIdSubscription.unsubscribe();
        }
        mGetSummaryByIdSubscription = SettleManager.getInstance().getSettleDetail(recordId, new Callback<SettleSummaryResult>() {
            @Override
            public void onSuccess(SettleSummaryResult o) {
                mView.onDetailSuccess(o.respData);
            }

            @Override
            public void onError(String error) {
                mView.onDetailFailed();
                mView.showError("查看明细失败:" + error);
            }
        });
    }

    @Override
    public void onPrint(final SettleSummaryResult.RespData respData) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        SettleManager.getInstance().print(respData, true);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        mView.showToast("正在打印小票");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showToast("打印失败");
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }
}
