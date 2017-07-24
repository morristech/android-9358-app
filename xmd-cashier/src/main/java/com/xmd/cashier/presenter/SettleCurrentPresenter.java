package com.xmd.cashier.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.SettleCurrentContract;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.SettleManager;
import com.xmd.m.network.BaseBean;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-3-29.
 */

public class SettleCurrentPresenter implements SettleCurrentContract.Presenter {

    private Context mContext;
    private SettleCurrentContract.View mView;

    private Subscription mGetCurrentSummarySubscription;
    private Subscription mSettleCurrentSummarySubscription;

    private SettleSummaryResult.RespData mRespData;

    public SettleCurrentPresenter(Context context, SettleCurrentContract.View view) {
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
        if (mGetCurrentSummarySubscription != null) {
            mGetCurrentSummarySubscription.unsubscribe();
        }
        if (mSettleCurrentSummarySubscription != null) {
            mSettleCurrentSummarySubscription.unsubscribe();
        }
    }

    private void print(final SettleSummaryResult.RespData respData) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        SettleManager.getInstance().print(respData, false);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void getSummary() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        mView.showLoading();
        if (mGetCurrentSummarySubscription != null) {
            mGetCurrentSummarySubscription.unsubscribe();
        }
        mGetCurrentSummarySubscription = SettleManager.getInstance().getSettleCurrent(new Callback<SettleSummaryResult>() {
            @Override
            public void onSuccess(SettleSummaryResult o) {
                if (o.getRespData() == null) {
                    mView.onCurrentEmpty();
                } else {
                    mRespData = o.getRespData();
                    mView.onCurrentSuccess(o.getRespData().obj, o.getRespData().recordDetailList);
                }
            }

            @Override
            public void onError(String error) {
                mView.onCurrentFailed();
                mView.showError("获取结算数据失败:" + error);
            }
        });
    }

    @Override
    public void onSettle() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        mView.showLoading();
        if (mSettleCurrentSummarySubscription != null) {
            mSettleCurrentSummarySubscription.unsubscribe();
        }
        Gson gson = new GsonBuilder().serializeNulls().create();
        String data = gson.toJson(mRespData);
        XLogger.i("data :" + data);
        mSettleCurrentSummarySubscription = SettleManager.getInstance().saveSettle(data, new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                // 汇报成功
                mView.hideLoading();
                print(mRespData);
                mView.showToast("结算成功");
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                // 汇报失败
                mView.hideLoading();
                mView.showToast("结算失败:" + error);
            }
        });
    }
}
