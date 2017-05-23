package com.xmd.cashier.presenter;


import android.content.Context;

import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.SettleRecordContract;
import com.xmd.cashier.dal.net.response.SettleRecordResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.SettleManager;

import rx.Subscription;

/**
 * Created by zr on 17-3-29.
 */

public class SettleRecordPresenter implements SettleRecordContract.Presenter {
    private Context mContext;
    private SettleRecordContract.View mView;

    private Subscription mGetSettleRecordSubscription;

    private int mPageIndex;
    private boolean hasMore;
    private boolean isLoadingMore;

    public SettleRecordPresenter(Context context, SettleRecordContract.View view) {
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
        if (mGetSettleRecordSubscription != null) {
            mGetSettleRecordSubscription.unsubscribe();
        }
    }

    @Override
    public void loadInit() {
        mView.showLoadIng();
        mPageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showLoadNoNetwork();
            return;
        }
        if (mGetSettleRecordSubscription != null) {
            mGetSettleRecordSubscription.unsubscribe();
        }
        mGetSettleRecordSubscription = SettleManager.getInstance().getSettleRecord(String.valueOf(mPageIndex), String.valueOf(AppConstants.APP_LIST_PAGE_SIZE), null, new Callback<SettleRecordResult>() {
            @Override
            public void onSuccess(SettleRecordResult o) {
                mView.hideLoading();
                if (o.respData != null && o.respData.records != null && o.respData.records.size() > 0) {
                    mView.showLoadSuccess();
                    if (mPageIndex < o.pageCount) {
                        hasMore = true;
                        mPageIndex++;
                        mView.showMoreSuccess();
                    } else {
                        hasMore = false;
                        mView.showMoreNone();
                    }
                    mView.showData(o.respData.records);
                } else {
                    mView.showLoadEmpty();
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showLoadError();
            }
        });
    }

    @Override
    public void loadMore() {
        if (!hasMore || isLoadingMore) {
            return;
        }
        mView.showMoreIng();
        isLoadingMore = true;
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showMoreNoNetwork();
            return;
        }
        if (mGetSettleRecordSubscription != null) {
            mGetSettleRecordSubscription.unsubscribe();
        }
        mGetSettleRecordSubscription = SettleManager.getInstance().getSettleRecord(String.valueOf(mPageIndex), String.valueOf(AppConstants.APP_LIST_PAGE_SIZE), null, new Callback<SettleRecordResult>() {
            @Override
            public void onSuccess(SettleRecordResult o) {
                isLoadingMore = false;
                if (mPageIndex < o.pageCount) {
                    mPageIndex++;
                    hasMore = true;
                    mView.showMoreSuccess();
                } else {
                    hasMore = false;
                    mView.showMoreNone();
                }
                mView.showData(o.respData.records);
            }

            @Override
            public void onError(String error) {
                isLoadingMore = false;
                mView.showMoreError();
            }
        });
    }

    @Override
    public void loadMonth(String month) {
        mView.showLoadIng();
        mView.clearData();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showLoadNoNetwork();
            return;
        }
        if (mGetSettleRecordSubscription != null) {
            mGetSettleRecordSubscription.unsubscribe();
        }
        mGetSettleRecordSubscription = SettleManager.getInstance().getSettleRecord(String.valueOf(AppConstants.APP_LIST_DEFAULT_PAGE), String.valueOf(Integer.MAX_VALUE), month, new Callback<SettleRecordResult>() {
            @Override
            public void onSuccess(SettleRecordResult o) {
                mView.hideLoading();
                if (o.respData != null && o.respData.records != null && o.respData.records.size() > 0) {
                    mView.showLoadSuccess();
                    mView.showData(o.respData.records);
                } else {
                    mView.showLoadEmpty();
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showLoadError();
            }
        });
    }

    @Override
    public void onRecordClick(String recordId) {
        UiNavigation.gotoSettleDetailActivity(mContext, recordId);
    }
}
