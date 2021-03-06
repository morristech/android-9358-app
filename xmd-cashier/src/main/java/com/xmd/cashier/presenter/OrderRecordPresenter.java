package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.OrderRecordContract;
import com.xmd.cashier.dal.bean.OrderRecordInfo;
import com.xmd.cashier.dal.net.response.OrderRecordListResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.NotifyManager;
import com.xmd.m.network.BaseBean;

import rx.Subscription;

/**
 * Created by zr on 17-4-11.
 */

public class OrderRecordPresenter implements OrderRecordContract.Presenter {
    private OrderRecordContract.View mView;
    private Context mContext;

    private boolean hasMore;
    private boolean isRefreshing;
    private boolean isLoading;

    private String mSearch;
    private String mFilter;
    private int pageIndex;

    private Subscription mGetOrderRecordListSubscription;
    private Subscription mAcceptOrderRecordSubscription;
    private Subscription mRejectOrderRecordSubscription;

    public OrderRecordPresenter(Context context, OrderRecordContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {
        mSearch = null;
        mFilter = null;
    }

    @Override
    public void onDestroy() {
        if (mGetOrderRecordListSubscription != null) {
            mGetOrderRecordListSubscription.unsubscribe();
        }
        if (mAcceptOrderRecordSubscription != null) {
            mAcceptOrderRecordSubscription.unsubscribe();
        }
        if (mRejectOrderRecordSubscription != null) {
            mRejectOrderRecordSubscription.unsubscribe();
        }
    }

    @Override
    public void load(boolean init) {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        if (!init) {
            mView.showRefreshIng();
        }
        if (!Utils.isNetworkEnabled(mContext)) {
            isRefreshing = false;
            mView.showRefreshNoNetwork();
            return;
        }
        if (mGetOrderRecordListSubscription != null) {
            mGetOrderRecordListSubscription.unsubscribe();
        }
        pageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;
        mGetOrderRecordListSubscription = NotifyManager.getInstance().getOrderRecordList(pageIndex, mSearch, mFilter, new Callback<OrderRecordListResult>() {
            @Override
            public void onSuccess(OrderRecordListResult o) {
                mView.clearData();
                if (o.getRespData() == null || o.getRespData().size() == 0) {
                    mView.showRefreshEmpty();
                } else {
                    mView.showRefreshSuccess();
                    if (pageIndex < o.getPageCount()) {
                        pageIndex++;
                        hasMore = true;
                        mView.showMoreSuccess();
                    } else {
                        hasMore = false;
                        mView.showMoreNone();
                    }
                    mView.showData(o.getRespData());
                }
                isRefreshing = false;
            }

            @Override
            public void onError(String error) {
                isRefreshing = false;
                mView.showRefreshError();
            }
        });
    }

    @Override
    public void loadMore() {
        if (isLoading || !hasMore) {
            return;
        }
        isLoading = true;
        mView.showMoreLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            isLoading = false;
            mView.showMoreNoNetwork();
            return;
        }
        if (mGetOrderRecordListSubscription != null) {
            mGetOrderRecordListSubscription.unsubscribe();
        }
        mGetOrderRecordListSubscription = NotifyManager.getInstance().getOrderRecordList(pageIndex, mSearch, mFilter, new Callback<OrderRecordListResult>() {
            @Override
            public void onSuccess(OrderRecordListResult o) {
                if (pageIndex < o.getPageCount()) {
                    pageIndex++;
                    hasMore = true;
                    mView.showMoreSuccess();
                } else {
                    hasMore = false;
                    mView.showMoreNone();
                }
                mView.showData(o.getRespData());
                isLoading = false;
            }

            @Override
            public void onError(String error) {
                isLoading = false;
                mView.showMoreError();
            }
        });
    }

    @Override
    public void print(OrderRecordInfo info, boolean retry) {
        NotifyManager.getInstance().printOrderRecordAsync(info, retry);
    }

    @Override
    public void accept(final OrderRecordInfo info, final int position) {
        // 接受
        mView.showLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mAcceptOrderRecordSubscription != null) {
            mAcceptOrderRecordSubscription.unsubscribe();
        }
        mAcceptOrderRecordSubscription = NotifyManager.getInstance().acceptOrder(info.id, new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                mView.hideLoading();
                mView.showToast("订单接受成功");
                mView.updateDataStatus(AppConstants.ORDER_RECORD_STATUS_ACCEPT, position);
                info.status = AppConstants.ORDER_RECORD_STATUS_ACCEPT;
                info.receiverName = AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")";

                if (SPManager.getInstance().getOrderAcceptSwitch()) {
                    print(info, false);
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("订单接受失败:" + error);
            }
        });
    }

    @Override
    public void reject(final OrderRecordInfo info, final int position) {
        // 拒绝
        mView.showLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mRejectOrderRecordSubscription != null) {
            mRejectOrderRecordSubscription.unsubscribe();
        }
        mRejectOrderRecordSubscription = NotifyManager.getInstance().rejectOrder(info.id, new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                mView.hideLoading();
                mView.showToast("订单拒绝成功");
                mView.updateDataStatus(AppConstants.ORDER_RECORD_STATUS_REJECT, position);
                info.status = AppConstants.ORDER_RECORD_STATUS_ACCEPT;

                if (SPManager.getInstance().getOrderRejectSwitch()) {
                    print(info, false);
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("订单拒绝失败:" + error);
            }
        });
    }

    @Override
    public void setFilter(String filter) {
        mFilter = filter;
    }

    @Override
    public void setSearch(String search) {
        mSearch = search;
    }
}
