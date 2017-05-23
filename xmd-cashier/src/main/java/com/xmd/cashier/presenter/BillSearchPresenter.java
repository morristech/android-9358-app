package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.BillSearchContract;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.dal.net.response.BillRecordResult;
import com.xmd.cashier.manager.BillManager;
import com.xmd.cashier.manager.Callback;

import rx.Subscription;

/**
 * Created by zr on 16-11-29.
 */

public class BillSearchPresenter implements BillSearchContract.Presenter {
    private Context mContext;
    private BillSearchContract.View mView;
    private String mTradeNo;
    private BillManager mBillManager;
    private Subscription mSearchBillListSubscription;
    private int mPageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;

    public BillSearchPresenter(Context context, BillSearchContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mBillManager = BillManager.getInstance();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mSearchBillListSubscription != null) {
            mSearchBillListSubscription.unsubscribe();
        }
    }

    @Override
    public void onBillItemClick(BillInfo info) {
        UiNavigation.gotoBillDetailActivity(mContext, info);
    }

    @Override
    public void searchBills() {
        mPageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;
        mTradeNo = mView.getTradeNo();
        if (TextUtils.isEmpty(mTradeNo)) {
            mView.showToast("请输入交易号");
            return;
        }
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError("网络异常，请检查网络后重试...");
            return;
        }

        if (mSearchBillListSubscription != null) {
            mSearchBillListSubscription.unsubscribe();
        }
        mView.hideKeyBoard();
        mView.showLoadIng();
        mSearchBillListSubscription = mBillManager.searchBillList(mTradeNo, mPageIndex, new Callback<BillRecordResult>() {
            @Override
            public void onSuccess(BillRecordResult o) {
                mView.hideLoadIng();
                if (o.respData == null || o.respData.list == null || o.respData.list.size() == 0) {
                    mView.showLoadEmpty();
                } else {
                    mView.showLoadSuccess();
                    if (mPageIndex < o.pageCount) {
                        mPageIndex++;
                        mView.showMoreSuccess();
                        mView.setHasMore(true);
                    } else {
                        mView.showMoreNone();
                        mView.setHasMore(false);
                    }
                    mView.showBillData(o.respData.list);
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoadIng();
                mView.showKeyBoard();
                mView.showError("访问失败，请稍后重试...");
            }
        });
    }

    @Override
    public void searchMoreBills() {
        mView.showMoreIng();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showMoreNoNetwork();
            return;
        }

        if (mSearchBillListSubscription != null) {
            mSearchBillListSubscription.unsubscribe();
        }

        mSearchBillListSubscription = mBillManager.searchBillList(mTradeNo, mPageIndex, new Callback<BillRecordResult>() {
            @Override
            public void onSuccess(BillRecordResult o) {
                if (mPageIndex < o.pageCount) {
                    mPageIndex++;
                    mView.showMoreSuccess();
                    mView.setHasMore(true);
                } else {
                    mView.showMoreNone();
                    mView.setHasMore(false);
                }
                mView.showBillData(o.respData.list);
            }

            @Override
            public void onError(String error) {
                mView.showMoreError();
            }
        });
    }
}
