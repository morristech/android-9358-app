package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyRecordDetailContract;
import com.xmd.cashier.dal.bean.VerifyRecordDetailInfo;
import com.xmd.cashier.dal.bean.VerifyRecordInfo;
import com.xmd.cashier.dal.net.response.VerifyRecordDetailResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-5-2.
 */

public class VerifyRecordDetailPresenter implements VerifyRecordDetailContract.Presenter {
    private Context mContext;
    private VerifyRecordDetailContract.View mView;

    private Subscription mGetVerifyDetailSubscription;

    private VerifyRecordInfo mInfo;

    public VerifyRecordDetailPresenter(Context context, VerifyRecordDetailContract.View view) {
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
        if (mGetVerifyDetailSubscription != null) {
            mGetVerifyDetailSubscription.unsubscribe();
        }
    }

    @Override
    public void getVerifyDetailById(String recordId) {
        if (TextUtils.isEmpty(recordId)) {
            mView.finishSelf();
            return;
        }

        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }

        mView.showLoading();
        if (mGetVerifyDetailSubscription != null) {
            mGetVerifyDetailSubscription.unsubscribe();
        }
        mGetVerifyDetailSubscription = VerifyManager.getInstance().getVerifyRecordDetail(recordId, new Callback<VerifyRecordDetailResult>() {
            @Override
            public void onSuccess(VerifyRecordDetailResult o) {
                List<VerifyRecordDetailInfo> details = o.getRespData().detail;
                mInfo = o.getRespData().record;
                switch (mInfo.businessType) {
                    case AppConstants.TYPE_COUPON:
                    case AppConstants.TYPE_CASH_COUPON:
                    case AppConstants.TYPE_GIFT_COUPON:
                    case AppConstants.TYPE_DISCOUNT_COUPON:
                    case AppConstants.TYPE_PAID_COUPON:
                        for (VerifyRecordDetailInfo detailInfo : details) {
                            if (detailInfo.title.contains("券详情")) {
                                mInfo.consumeMoneyDescription = detailInfo.text;
                            }
                        }
                        break;
                    case AppConstants.TYPE_ORDER:
                        for (VerifyRecordDetailInfo detailInfo : details) {
                            if (detailInfo.title.contains("预约技师")) {
                                mInfo.techDescription = detailInfo.text;
                            }
                            if (detailInfo.title.contains("预约项目")) {
                                mInfo.serviceItemName = detailInfo.text;
                            }
                        }
                        break;
                    case AppConstants.TYPE_PAY_FOR_OTHER:
                        for (VerifyRecordDetailInfo detailInfo : details) {
                            if (detailInfo.title.contains("手机号")) {
                                mInfo.memberPhone = detailInfo.text;
                            }
                            if (detailInfo.title.contains("会员等级")) {
                                mInfo.memberTypeName = detailInfo.text;
                            }
                            if (detailInfo.title.contains("会员折扣")) {
                                mInfo.memberDiscountDesc = detailInfo.text;
                            }
                        }
                        break;
                    default:
                        break;
                }
                mView.hideLoading();
                mView.onDetailSuccess(o.getRespData());
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError("请求失败：" + error);
            }
        });
    }

    @Override
    public void printVerifyRecord() {
        if (mInfo == null) {
            mView.showToast("核销打印异常，请稍后重试...");
        }
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        VerifyManager.getInstance().printRecord(mInfo, true);
                        VerifyManager.getInstance().printRecord(mInfo, false);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
