package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyServiceContract;
import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;

import rx.Subscription;

/**
 * Created by zr on 16-12-12.
 */

public class VerifyServicePresenter implements VerifyServiceContract.Presenter {
    private Context mContext;
    private VerifyServiceContract.View mView;
    private Subscription mVerifyServiceSubscription;

    public VerifyServicePresenter(Context context, VerifyServiceContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onClickVerify() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        if (mVerifyServiceSubscription != null) {
            mVerifyServiceSubscription.unsubscribe();
        }
        mView.showLoadingView();
        mVerifyServiceSubscription = VerifyManager.getInstance().verifyServiceCoupon(mView.getCode(), new Callback<BaseResult>() {
            @Override
            public void onSuccess(BaseResult o) {
                mView.showToast("操作成功");
                mView.hideLoadingView();
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                XLogger.i(error);
                mView.hideLoadingView();
                mView.showError("操作失败：" + error);
            }
        });
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mVerifyServiceSubscription != null) {
            mVerifyServiceSubscription.unsubscribe();
        }
    }
}
