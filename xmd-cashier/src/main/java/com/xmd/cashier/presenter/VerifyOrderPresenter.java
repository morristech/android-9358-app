package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyOrderContract;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;

import rx.Subscription;

/**
 * Created by zr on 2017/4/16 0016.
 */

public class VerifyOrderPresenter implements VerifyOrderContract.Presenter {
    private Context mContext;
    private VerifyOrderContract.View mView;
    private Subscription mModifyOrderStatusSubscription;

    public VerifyOrderPresenter(Context context, VerifyOrderContract.View view) {
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
        if (mModifyOrderStatusSubscription != null) {
            mModifyOrderStatusSubscription.unsubscribe();
        }
    }

    @Override
    public void onVerify(OrderInfo info) {
        mView.showLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError("网络异常,请检查网络后重试");
            return;
        }

        if (mModifyOrderStatusSubscription != null) {
            mModifyOrderStatusSubscription.unsubscribe();
        }

        mModifyOrderStatusSubscription = VerifyManager.getInstance().verifyPaidOrder(info.orderNo, AppConstants.PAID_ORDER_OP_VERIFIED, new Callback<BaseResult>() {
            @Override
            public void onSuccess(BaseResult o) {
                mView.hideLoading();
                mView.showToast("核销成功");
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
