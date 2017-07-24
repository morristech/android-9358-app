package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyCouponContract;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;
import com.xmd.m.network.BaseBean;

import rx.Subscription;

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
    public void onVerify(final CouponInfo info) {
        mView.showLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mVerifyNormalCouponSubscription != null) {
            mVerifyNormalCouponSubscription.unsubscribe();
        }

        mVerifyNormalCouponSubscription = VerifyManager.getInstance().verifyCommon(info.couponNo, new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                VerifyManager.getInstance().print(info.customType, info);
                mView.showToast("核销成功");
                mView.hideLoading();
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("核销失败:" + error);
            }
        });
    }
}
