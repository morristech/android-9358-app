package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.OrderRecordContract;
import com.xmd.cashier.dal.bean.OrderRecordInfo;
import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.dal.net.response.OrderRecordListResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.NotifyManager;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
                if (o.respData == null || o.respData.size() == 0) {
                    mView.showRefreshEmpty();
                } else {
                    mView.showRefreshSuccess();
                    if (pageIndex < o.pageCount) {
                        pageIndex++;
                        hasMore = true;
                        mView.showMoreSuccess();
                    } else {
                        hasMore = false;
                        mView.showMoreNone();
                    }
                    mView.showData(o.respData);
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
                if (pageIndex < o.pageCount) {
                    pageIndex++;
                    hasMore = true;
                    mView.showMoreSuccess();
                } else {
                    hasMore = false;
                    mView.showMoreNone();
                }
                mView.showData(o.respData);
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
    public void print(final OrderRecordInfo info, final boolean retry) {
        // 打印
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        NotifyManager.getInstance().print(info, retry);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        mView.showToast("打印成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showToast("打印失败");
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }

    @Override
    public void accept(final OrderRecordInfo info, final int position) {
        // 接受
        mView.showLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        if (mAcceptOrderRecordSubscription != null) {
            mAcceptOrderRecordSubscription.unsubscribe();
        }
        mAcceptOrderRecordSubscription = NotifyManager.getInstance().acceptOrder(info.id, new Callback<BaseResult>() {
            @Override
            public void onSuccess(BaseResult o) {
                mView.hideLoading();
                mView.showToast("订单接受成功");
                mView.updateDataStatus(AppConstants.ORDER_RECORD_STATUS_ACCEPT, position);
                info.status = AppConstants.ORDER_RECORD_STATUS_ACCEPT;
                print(info, false);
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("订单接受失败:" + error);
            }
        });
    }

    @Override
    public void reject(OrderRecordInfo info, final int position) {
        // 拒绝
        mView.showLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        if (mRejectOrderRecordSubscription != null) {
            mRejectOrderRecordSubscription.unsubscribe();
        }
        mRejectOrderRecordSubscription = NotifyManager.getInstance().rejectOrder(info.id, new Callback<BaseResult>() {
            @Override
            public void onSuccess(BaseResult o) {
                mView.hideLoading();
                mView.showToast("订单拒绝成功");
                mView.updateDataStatus(AppConstants.ORDER_RECORD_STATUS_REJECT, position);
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
