package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.contract.DiscountCouponContract;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.OnlinePayCouponResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-7-27.
 */

public class DiscountCouponPresenter implements DiscountCouponContract.Presenter {
    private Context mContext;
    private DiscountCouponContract.View mView;
    private Subscription mGetDiscountCouponSubscription;

    public DiscountCouponPresenter(Context context, DiscountCouponContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        String code = mView.getCode();
        if (TextUtils.isEmpty(code)) {
            mView.showToast("无法获取券详情");
            mView.finishSelf();
            return;
        }
        mView.showLoading();
        if (mGetDiscountCouponSubscription != null) {
            mGetDiscountCouponSubscription.unsubscribe();
        }
        Observable<OnlinePayCouponResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getDiscountCoupon(AccountManager.getInstance().getToken(), code);
        mGetDiscountCouponSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OnlinePayCouponResult>() {
            @Override
            public void onCallbackSuccess(OnlinePayCouponResult result) {
                mView.hideLoading();
                if (result != null) {
                    mView.showCouponInfo(result.getRespData());
                } else {
                    mView.showToast("解析详情数据异常");
                    mView.finishSelf();
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                mView.showToast("获取详情失败:" + e.getLocalizedMessage());
                mView.finishSelf();
            }
        });
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mGetDiscountCouponSubscription != null) {
            mGetDiscountCouponSubscription.unsubscribe();
        }
    }
}
