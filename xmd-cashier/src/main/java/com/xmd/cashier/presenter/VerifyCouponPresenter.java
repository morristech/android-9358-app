package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyCouponContract;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;
import com.xmd.cashier.widget.VerifyDiscountDialog;
import com.xmd.m.network.BaseBean;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 2017/4/16 0016.
 */

public class VerifyCouponPresenter implements VerifyCouponContract.Presenter {
    private Context mContext;
    private VerifyCouponContract.View mView;
    private Subscription mVerifyNormalCouponSubscription;

    public VerifyCouponPresenter(Context context, VerifyCouponContract.View view) {
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
        if (mVerifyNormalCouponSubscription != null) {
            mVerifyNormalCouponSubscription.unsubscribe();
        }
    }

    @Override
    public void onVerify(CouponInfo info) {
        switch (info.couponType) {
            case AppConstants.COUPON_TYPE_DISCOUNT:
                doVerifyDiscount(info);
                break;
            default:
                doVerifyOthers(info);
                break;
        }
    }

    private void doVerifyOthers(final CouponInfo info) {
        mView.showLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mVerifyNormalCouponSubscription != null) {
            mVerifyNormalCouponSubscription.unsubscribe();
        }

        switch (info.couponType) {
            case AppConstants.COUPON_TYPE_DISCOUNT: //折扣券
                mVerifyNormalCouponSubscription = VerifyManager.getInstance().verifyWithMoney(info.originAmount, info.couponNo, AppConstants.TYPE_DISCOUNT_COUPON, new Callback<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean o) {
                        mView.hideLoading();
                        mView.showToast("核销成功");
                        printNormal(info);
                        mView.finishSelf();
                    }

                    @Override
                    public void onError(String error) {
                        info.originAmount = 0;  //核销失败清空核销券消费金额
                        mView.hideLoading();
                        mView.showToast("核销失败:" + error);
                    }
                });
                break;
            default:
                mVerifyNormalCouponSubscription = VerifyManager.getInstance().verifyCommon(info.couponNo, new Callback<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean o) {
                        mView.hideLoading();
                        mView.showToast("核销成功");
                        printNormal(info);
                        mView.finishSelf();
                    }

                    @Override
                    public void onError(String error) {
                        mView.hideLoading();
                        mView.showToast("核销失败:" + error);
                    }
                });
                break;
        }
    }

    private void doVerifyDiscount(final CouponInfo info) {
        List<CouponInfo> list = new ArrayList<>();
        list.add(info);
        final VerifyDiscountDialog dialog = new VerifyDiscountDialog(mContext, list);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCallBack(new VerifyDiscountDialog.CallBack() {
            @Override
            public void onNegative() {
                dialog.dismiss();
                info.originAmount = 0;
            }

            @Override
            public void onPositive(String input) {
                if (TextUtils.isEmpty(input)) {
                    mView.showToast("请输入消费金额");
                    return;
                }
                dialog.dismiss();
                doVerifyOthers(info);
            }
        });
    }

    private void printNormal(final CouponInfo info) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        VerifyManager.getInstance().printCoupon(info, true);
                        if (SPManager.getInstance().getPrintClientSwitch()) {
                            VerifyManager.getInstance().printCoupon(info, false);
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
