package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerDetailContract;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.InnerManager;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-11-7.
 */

public class InnerDetailPresenter implements InnerDetailContract.Presenter {
    private Context mContext;
    private InnerDetailContract.View mView;

    public InnerDetailPresenter(Context context, InnerDetailContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        switch (mView.returnSource()) {
            case AppConstants.INNER_DETAIL_SOURCE_RECORD:
                mView.showOperate();
                break;
            case AppConstants.INNER_DETAIL_SOURCE_OTHER:
            default:
                mView.showPositive();
                break;
        }

        InnerRecordInfo info = mView.returnRecordInfo();
        if (info != null) {
            mView.showAmount(info);
            if (info.details != null && !info.details.isEmpty()) {
                mView.showRecordDetail(info.details);
            } else {
                mView.showToast("未查询到相应详情");
                return;
            }
            mView.setOperate(AppConstants.INNER_BATCH_STATUS_UNPAID.equals(info.status));
        } else {
            mView.showRecordDetail(InnerManager.getInstance().getSelectedInnerOrderInfos());
            mView.showAmount(InnerManager.getInstance().getOrderAmount());
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onDetailNegative() {
        mView.finishSelf();
    }

    @Override
    public void onDetailPositive() {
        mView.finishSelf();
    }

    @Override
    public void onDetailPrint() {
        final InnerRecordInfo recordInfo = mView.returnRecordInfo();
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        InnerManager.getInstance().printInnerRecordInfo(recordInfo, true, true, null);
                        if (SPManager.getInstance().getPrintClientSwitch()) {
                            InnerManager.getInstance().printInnerRecordInfo(recordInfo, true, false, null);
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void onDetailPay() {
        InnerRecordInfo recordInfo = mView.returnRecordInfo();
        InnerManager.getInstance().initTradeByRecord(recordInfo);
        if (recordInfo.paidAmount > 0) {
            UiNavigation.gotoInnerModifyActivity(mContext);
        } else {
            UiNavigation.gotoInnerMethodActivity(mContext, AppConstants.INNER_METHOD_SOURCE_RECORD, recordInfo);
        }
        mView.finishSelf();
    }
}
