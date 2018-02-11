package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.contract.CashierResultContract;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.TradeManager;

import rx.Subscription;

/**
 * Created by zr on 17-5-16.
 */

public class CashierResultPresenter implements CashierResultContract.Presenter {
    private static final String TAG = "CashierResultPresenter";
    private Context mContext;
    private CashierResultContract.View mView;
    private TradeManager mTradeManager;

    private TradeRecordInfo mInfo = null;

    private Subscription mGetTradeDetailSubscription;

    public CashierResultPresenter(Context context, CashierResultContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
    }

    @Override
    public void onCreate() {
        mView.showStatus(mTradeManager.getCurrentTrade().tradeStatus);
        mView.showStatusError(mTradeManager.getCurrentTrade().tradeStatusError);
        getTradeDetail();
    }

    private void getTradeDetail() {
        if (mGetTradeDetailSubscription != null) {
            mGetTradeDetailSubscription.unsubscribe();
        }
        mGetTradeDetailSubscription = mTradeManager.getHoleBatchDetail(mTradeManager.getCurrentTrade().payOrderId, new Callback<TradeRecordInfo>() {
            @Override
            public void onSuccess(TradeRecordInfo o) {
                mView.hideLoading();
                mInfo = o;
                if (mInfo.payAmount <= mInfo.paidAmount) {
                    mTradeManager.printTradeRecordInfoAsync(mInfo, false);
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showPrint();
            }
        });
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mGetTradeDetailSubscription != null) {
            mGetTradeDetailSubscription.unsubscribe();
        }
    }

    @Override
    public void onConfirm() {
        mTradeManager.newTrade();
        mView.finishSelf();
    }

    @Override
    public void onPrint() {
        if (mInfo != null) {
            mTradeManager.printTradeRecordInfoAsync(mInfo, false);
        } else {
            mView.showLoading();
            getTradeDetail();
        }
    }
}
