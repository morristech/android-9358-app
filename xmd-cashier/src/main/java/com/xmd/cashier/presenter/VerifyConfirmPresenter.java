package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyConfirmContract;
import com.xmd.cashier.dal.bean.CheckInfo;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;

import java.util.List;

import rx.Subscription;

/**
 * Created by zr on 17-3-14.
 */

public class VerifyConfirmPresenter implements VerifyConfirmContract.Presenter {
    private Context mContext;
    private VerifyConfirmContract.View mView;

    private VerifyManager mVerifyManager;

    private Subscription mMultiResultVerifySubscription;

    public VerifyConfirmPresenter(Context context, VerifyConfirmContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mVerifyManager = VerifyManager.getInstance();
    }

    @Override
    public void onCreate() {
        init();
    }

    private void init() {
        mView.setSuccessText(mVerifyManager.getSuccessCount());
        mView.setFailedText(mVerifyManager.getFailedCount());
        mView.setButtonText(mVerifyManager.getFailedCount());
        mView.showVerifyResultList(mVerifyManager.getResultList());
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mMultiResultVerifySubscription != null) {
            mMultiResultVerifySubscription.unsubscribe();
        }
    }

    @Override
    public void onVerifyContinue() {
        if (mVerifyManager.getFailedCount() <= 0) {
            mView.showToast("已完成此次核销");
            mView.finishSelf();
            return;
        }
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        if (mMultiResultVerifySubscription != null) {
            mMultiResultVerifySubscription.unsubscribe();
        }
        mView.showLoading();
        mMultiResultVerifySubscription = mVerifyManager.verifyCheckInfo(new Callback<List<CheckInfo>>() {
            @Override
            public void onSuccess(List<CheckInfo> o) {
                mView.hideLoading();
                if (mVerifyManager.hasFailed()) {
                    //依旧存在核销失败的情况
                    init();
                    mView.showToast("部分券核销失败，点击继续核销");
                } else {
                    //全部成功
                    mView.showToast("选中的核销已处理成功");
                    mView.finishSelf();
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                init();
                mView.showToast("部分券核销失败，点击继续核销");
            }
        });
    }

    @Override
    public void onInfoClick(CheckInfo info) {

    }

    @Override
    public void onInfoSelected(CheckInfo info, boolean selected) {
        mVerifyManager.setItemSelectedStatus(info, selected);
        mView.setSuccessText(mVerifyManager.getSuccessCount());
        mView.setFailedText(mVerifyManager.getFailedCount());
        mView.setButtonText(mVerifyManager.getFailedCount());
    }

    @Override
    public void onInfoSelectedValid(CheckInfo info) {

    }
}
