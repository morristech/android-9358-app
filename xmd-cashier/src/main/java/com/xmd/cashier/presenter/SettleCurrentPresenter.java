package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.SettleCurrentContract;
import com.xmd.cashier.dal.bean.SettleBusinessInfo;
import com.xmd.cashier.dal.bean.SettleContentInfo;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.SettleManager;

import java.util.Date;

import rx.Subscription;

/**
 * Created by zr on 17-3-29.
 */

public class SettleCurrentPresenter implements SettleCurrentContract.Presenter {
    private Context mContext;
    private SettleCurrentContract.View mView;

    private Subscription mGetSettleDetailSubscription;
    private Subscription mSaveSettleSubscription;

    private SettleSummaryResult.RespData mDetail;

    private String mStartTime;
    private String mEndTime;

    public SettleCurrentPresenter(Context context, SettleCurrentContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mEndTime = DateUtils.doDate2String(new Date());
        getSettle();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mGetSettleDetailSubscription != null) {
            mGetSettleDetailSubscription.unsubscribe();
        }
        if (mSaveSettleSubscription != null) {
            mSaveSettleSubscription.unsubscribe();
        }
    }

    private long getAmount() {
        long totalAmount = 0;
        for (SettleContentInfo contentInfo : mDetail.settleList) {
            for (SettleBusinessInfo businessInfo : contentInfo.businessList) {
                totalAmount += businessInfo.amount;
            }
        }
        return totalAmount;
    }

    @Override
    public void onSettle() {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        mView.showLoading();
        if (mSaveSettleSubscription != null) {
            mSaveSettleSubscription.unsubscribe();
        }
        mSaveSettleSubscription = SettleManager.getInstance().saveSettle(String.valueOf(getAmount()), mStartTime, mEndTime, new Callback<StringResult>() {
            @Override
            public void onSuccess(StringResult o) {
                mView.hideLoading();
                mDetail.createTime = o.getRespData();
                mDetail.settleName = AccountManager.getInstance().getUser().loginName;
                SettleManager.getInstance().printAsync(mDetail, false);
                mView.showToast("交接班结算成功");
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("交接班结算失败:" + error);
            }
        });
    }

    @Override
    public void getSettle() {
        mView.initLayout();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.onCurrentFailed(mContext.getString(R.string.network_disabled));
            return;
        }
        mView.showLoading();
        if (mGetSettleDetailSubscription != null) {
            mGetSettleDetailSubscription.unsubscribe();
        }
        mGetSettleDetailSubscription = SettleManager.getInstance().getSettleDetail(null, mEndTime, new Callback<SettleSummaryResult>() {
            @Override
            public void onSuccess(SettleSummaryResult o) {
                mView.hideLoading();
                if (o.getRespData() != null && o.getRespData().settleList != null && !o.getRespData().settleList.isEmpty()) {
                    mDetail = o.getRespData();
                    mStartTime = mDetail.startTime;
                    mEndTime = mDetail.endTime;
                    mView.onCurrentSuccess(mDetail);
                } else {
                    mView.onCurrentEmpty();
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.onCurrentFailed(error);
            }
        });
    }
}
