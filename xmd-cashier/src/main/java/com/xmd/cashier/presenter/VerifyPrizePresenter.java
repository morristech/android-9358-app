package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyPrizeContract;
import com.xmd.cashier.dal.bean.PrizeInfo;
import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;

import rx.Subscription;

/**
 * Created by zr on 16-12-12.
 */

public class VerifyPrizePresenter implements VerifyPrizeContract.Presenter {
    private Context mContext;
    private VerifyPrizeContract.View mView;
    private Subscription mVerifyPrizeSubscription;

    public VerifyPrizePresenter(Context context, VerifyPrizeContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onClickVerify(final PrizeInfo info) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        if (mVerifyPrizeSubscription != null) {
            mVerifyPrizeSubscription.unsubscribe();
        }
        mView.showLoadingView();
        mVerifyPrizeSubscription = VerifyManager.getInstance().verifyLuckyWheel(mView.getCode(), new Callback<BaseResult>() {
            @Override
            public void onSuccess(BaseResult o) {
                VerifyManager.getInstance().print(AppConstants.TYPE_LUCKY_WHEEL, info);
                mView.hideLoadingView();
                mView.showToast("操作成功");
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
        if (mVerifyPrizeSubscription != null) {
            mVerifyPrizeSubscription.unsubscribe();
        }
    }
}
