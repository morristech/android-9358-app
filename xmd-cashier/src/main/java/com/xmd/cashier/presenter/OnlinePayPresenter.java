package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.OnlinePayContract;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.dal.net.response.OnlinePayListResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.NotifyManager;
import com.xmd.m.network.BaseBean;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-4-11.
 */

public class OnlinePayPresenter implements OnlinePayContract.Presenter {
    private OnlinePayContract.View mView;
    private Context mContext;

    private boolean hasMore;
    private boolean isLoading;
    private boolean isRefreshing;

    private String mSearch;
    private String mFilter;

    private int pageIndex;

    private Subscription mGetOnlinePaySubscription;
    private Subscription mPassOnlinePaySubscription;
    private Subscription mUnpassOnlinePaySubscription;

    public OnlinePayPresenter(Context context, OnlinePayContract.View view) {
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
        if (mGetOnlinePaySubscription != null) {
            mGetOnlinePaySubscription.unsubscribe();
        }
        if (mPassOnlinePaySubscription != null) {
            mPassOnlinePaySubscription.unsubscribe();
        }
        if (mUnpassOnlinePaySubscription != null) {
            mUnpassOnlinePaySubscription.unsubscribe();
        }
    }

    /**
     * 首次加载
     */
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
        if (mGetOnlinePaySubscription != null) {
            mGetOnlinePaySubscription.unsubscribe();
        }
        pageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;
        mGetOnlinePaySubscription = NotifyManager.getInstance().getOnlinePayList(pageIndex, mSearch, mFilter, new Callback<OnlinePayListResult>() {
            @Override
            public void onSuccess(OnlinePayListResult o) {
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

    /**
     * 加载更多
     */
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
        if (mGetOnlinePaySubscription != null) {
            mGetOnlinePaySubscription.unsubscribe();
        }
        mGetOnlinePaySubscription = NotifyManager.getInstance().getOnlinePayList(pageIndex, mSearch, mFilter, new Callback<OnlinePayListResult>() {
            @Override
            public void onSuccess(OnlinePayListResult o) {
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
    public void print(final OnlinePayInfo info, final boolean retry) {
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
    public void pass(final OnlinePayInfo info, final int position) {
        // 确认
        mView.showLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mPassOnlinePaySubscription != null) {
            mPassOnlinePaySubscription.unsubscribe();
        }

        mPassOnlinePaySubscription = NotifyManager.getInstance().passOnlinePay(info.id, AppConstants.ONLINE_PAY_STATUS_PASS, new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                mView.hideLoading();
                mView.updateDataStatus(AppConstants.ONLINE_PAY_STATUS_PASS, position);
                mView.showToast("买单成功");
                info.status = AppConstants.ONLINE_PAY_STATUS_PASS;
                info.operatorName = AccountManager.getInstance().getUser().userName;

                if (SPManager.getInstance().getOnlinePassSwitch()) {
                    print(info, false);
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("确认买单失败:" + error);
            }
        });
    }

    @Override
    public void unpass(final OnlinePayInfo info, final int position) {
        // 请到前台
        mView.showLoading();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showToast(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mUnpassOnlinePaySubscription != null) {
            mUnpassOnlinePaySubscription.unsubscribe();
        }
        mUnpassOnlinePaySubscription = NotifyManager.getInstance().unPassOnlinePay(info.id, AppConstants.ONLINE_PAY_STATUS_UNPASS, new Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean o) {
                mView.hideLoading();
                mView.updateDataStatus(AppConstants.ONLINE_PAY_STATUS_UNPASS, position);
                mView.showToast("已通知请到前台");
                info.status = AppConstants.ONLINE_PAY_STATUS_PASS;
                info.operatorName = AccountManager.getInstance().getUser().userName;

                if (SPManager.getInstance().getOnlineUnpassSwitch()) {
                    print(info, false);
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("请到前台失败:" + error);
            }
        });
    }

    @Override
    public void setFilter(String filter) {
        // 设置过滤条件
        mFilter = filter;
    }

    @Override
    public void setSearch(String search) {
        // 设置查询条件
        mSearch = search;
    }

    @Override
    public void detail(String code) {
        UiNavigation.gotoDiscountCouponActivity(mContext, code);
    }
}
