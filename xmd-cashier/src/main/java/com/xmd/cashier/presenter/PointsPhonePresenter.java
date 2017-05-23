package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.PointsPhoneContract;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.TradeManager;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 16-8-22.
 */

public class PointsPhonePresenter implements PointsPhoneContract.Presenter {
    private Context mContext;
    private PointsPhoneContract.View mView;
    private TradeManager mTradeManager = TradeManager.getInstance();

    public PointsPhonePresenter(Context context, PointsPhoneContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }


    @Override
    public void onCreate() {
        Trade trade = mTradeManager.getCurrentTrade();
        mView.showPoints(trade.posPoints);
        if (trade.memberInfo != null) {
            mView.setPhone(trade.memberInfo.phone);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onClickOk() {
        final String phone = mView.getPhone();
        if (!Utils.matchPhoneNumFormat(phone)) {
            mView.showError("手机号码输入有误!");
            return;
        }
        mView.showLoading();

        mTradeManager.gainPoints(phone, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                mTradeManager.getCurrentTrade().posPointsPhone = phone;
                printInfo();
            }

            @Override
            public void onError(String error) {
                printInfo();
            }
        });
    }


    @Override
    public void onClickCancel() {
        mView.showLoading();
        printInfo();
    }

    private void printInfo() {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        mTradeManager.print();
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mTradeManager.newTrade();
                        mView.finishSelf();
                    }
                });
    }
}
