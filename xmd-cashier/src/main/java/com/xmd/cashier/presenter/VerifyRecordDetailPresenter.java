package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyRecordDetailContract;
import com.xmd.cashier.dal.net.response.VerifyRecordDetailResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;

import rx.Subscription;

/**
 * Created by zr on 17-5-2.
 */

public class VerifyRecordDetailPresenter implements VerifyRecordDetailContract.Presenter {
    private Context mContext;
    private VerifyRecordDetailContract.View mView;

    private Subscription mGetVerifyDetailSubscription;

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
            mView.showError("网络异常，请检查网络后重试");
            return;
        }

        mView.showLoading();
        if (mGetVerifyDetailSubscription != null) {
            mGetVerifyDetailSubscription.unsubscribe();
        }
        mGetVerifyDetailSubscription = VerifyManager.getInstance().getVerifyRecordDetail(recordId, new Callback<VerifyRecordDetailResult>() {
            @Override
            public void onSuccess(VerifyRecordDetailResult o) {
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
}
