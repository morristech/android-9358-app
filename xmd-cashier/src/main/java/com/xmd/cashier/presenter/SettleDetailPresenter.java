package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.SettleDetailContract;
import com.xmd.cashier.dal.bean.SettleRecordInfo;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.SettleManager;

import rx.Subscription;

/**
 * Created by zr on 17-4-7.
 */

public class SettleDetailPresenter implements SettleDetailContract.Presenter {
    private Context mContext;
    private SettleDetailContract.View mView;

    private Subscription mGetSettleDetailSubscription;

    private SettleRecordInfo mRecord;
    private SettleSummaryResult.RespData mDetail;

    public SettleDetailPresenter(Context context, SettleDetailContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mRecord = mView.returnRecordInfo();
        getSettleDetail();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mGetSettleDetailSubscription != null) {
            mGetSettleDetailSubscription.unsubscribe();
        }
    }

    @Override
    public void getSettleDetail() {
        mView.initLayout();
        if (mRecord == null) {
            mView.onDetailFailed("获取结算数据错误");
            return;
        }
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.onDetailFailed(mContext.getString(R.string.network_disabled));
            return;
        }
        mView.showLoading();
        if (mGetSettleDetailSubscription != null) {
            mGetSettleDetailSubscription.unsubscribe();
        }
        mGetSettleDetailSubscription = SettleManager.getInstance().getSettleDetail(String.valueOf(mRecord.id), null, new Callback<SettleSummaryResult>() {
            @Override
            public void onSuccess(SettleSummaryResult o) {
                mView.hideLoading();
                mDetail = o.getRespData();
                mDetail.createTime = mRecord.createTime;
                mDetail.settleName = mRecord.operatorName;
                mView.onDetailSuccess(mDetail);
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.onDetailFailed(error);
            }
        });
    }

    @Override
    public void onPrint() {
        SettleManager.getInstance().printAsync(mDetail, true);
    }
}
