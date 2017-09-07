package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.contract.BillDetailContract;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.manager.BillManager;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 16-11-29.
 */

public class BillDetailPresenter implements BillDetailContract.Presenter {
    private Context mContext;
    private BillDetailContract.View mView;

    public BillDetailPresenter(Context context, BillDetailContract.View view) {
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
    public void onClickMore() {
        mView.showMorePop();
    }

    @Override
    public void print(final BillInfo info) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        BillManager.getInstance().printBillRecord(info, true);
                        BillManager.getInstance().printBillRecord(info, false);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mView.finishSelf();
                    }
                });
    }

    @Override
    public void refund(BillInfo info) {
        mView.showToast("暂不支持退款");
    }
}
