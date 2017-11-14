package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerRecordContract;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.event.InnerFinishEvent;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.InnerRecordListResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-11-1.
 */

public class InnerRecordPresenter implements InnerRecordContract.Presenter {
    private Context mContext;
    private InnerRecordContract.View mView;

    private Subscription mGetInnerRecordSubscription;
    private Subscription mGetInnerHoleBatchSubscription;

    private boolean hasMore;
    private boolean isLoading;
    private boolean isRefreshing;

    private int pageIndex;

    public InnerRecordPresenter(Context context, InnerRecordContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mGetInnerRecordSubscription != null) {
            mGetInnerRecordSubscription.unsubscribe();
        }
        if (mGetInnerHoleBatchSubscription != null) {
            mGetInnerHoleBatchSubscription.unsubscribe();
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
        if (mGetInnerRecordSubscription != null) {
            mGetInnerRecordSubscription.unsubscribe();
        }
        pageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;

        Observable<InnerRecordListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getInnerRecordList(AccountManager.getInstance().getToken(), String.valueOf(pageIndex), String.valueOf(AppConstants.APP_LIST_INNRE_PAGE_SIZE));
        mGetInnerRecordSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<InnerRecordListResult>() {
            @Override
            public void onCallbackSuccess(InnerRecordListResult result) {
                mView.clearData();
                if (result.getRespData() == null || result.getRespData().size() == 0) {
                    mView.showRefreshEmpty();
                } else {
                    mView.showRefreshSuccess();
                    if (pageIndex < result.getPageCount()) {
                        pageIndex++;
                        hasMore = true;
                        mView.showMoreSuccess();
                    } else {
                        hasMore = false;
                        mView.showMoreNone();
                    }
                    mView.showData(result.getRespData());
                }
                isRefreshing = false;
            }

            @Override
            public void onCallbackError(Throwable e) {
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
        if (mGetInnerRecordSubscription != null) {
            mGetInnerRecordSubscription.unsubscribe();
        }

        Observable<InnerRecordListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getInnerRecordList(AccountManager.getInstance().getToken(), String.valueOf(pageIndex), String.valueOf(AppConstants.APP_LIST_INNRE_PAGE_SIZE));
        mGetInnerRecordSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<InnerRecordListResult>() {
            @Override
            public void onCallbackSuccess(InnerRecordListResult result) {
                if (pageIndex < result.getPageCount()) {
                    pageIndex++;
                    hasMore = true;
                    mView.showMoreSuccess();
                } else {
                    hasMore = false;
                    mView.showMoreNone();
                }
                mView.showData(result.getRespData());
                isLoading = false;
            }

            @Override
            public void onCallbackError(Throwable e) {
                isLoading = false;
                mView.showMoreError();
            }
        });
    }

    @Override
    public void onDetail(InnerRecordInfo info) {
        mView.showLoading();
        if (mGetInnerHoleBatchSubscription != null) {
            mGetInnerHoleBatchSubscription.unsubscribe();
        }
        mGetInnerHoleBatchSubscription = InnerManager.getInstance().getInnerHoleBatchSubscription(info.id, new Callback<InnerRecordInfo>() {
            @Override
            public void onSuccess(InnerRecordInfo o) {
                mView.hideLoading();
                UiNavigation.gotoInnerDetailActivity(mContext, o);
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("获取详情失败:" + error);
            }
        });
    }

    @Override
    public void onPay(InnerRecordInfo info) {
        mView.showLoading();
        if (mGetInnerHoleBatchSubscription != null) {
            mGetInnerHoleBatchSubscription.unsubscribe();
        }
        mGetInnerHoleBatchSubscription = InnerManager.getInstance().getInnerHoleBatchSubscription(info.id, new Callback<InnerRecordInfo>() {
            @Override
            public void onSuccess(InnerRecordInfo o) {
                mView.hideLoading();
                UiNavigation.gotoInnerMethodActivity(mContext, AppConstants.INNER_METHOD_SOURCE_RECORD, o);
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("获取详情失败:" + error);
            }
        });
    }

    @Override
    public void printClient(InnerRecordInfo info) {
        mView.showLoading();
        if (mGetInnerHoleBatchSubscription != null) {
            mGetInnerHoleBatchSubscription.unsubscribe();
        }
        mGetInnerHoleBatchSubscription = InnerManager.getInstance().getInnerHoleBatchSubscription(info.id, new Callback<InnerRecordInfo>() {
            @Override
            public void onSuccess(final InnerRecordInfo o) {
                mView.hideLoading();
                Observable
                        .create(new Observable.OnSubscribe<Void>() {
                            @Override
                            public void call(Subscriber<? super Void> subscriber) {
                                InnerManager.getInstance().printInnerRecordInfo(o, true, false, null);
                                subscriber.onNext(null);
                                subscriber.onCompleted();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("获取详情失败:" + error);
            }
        });
    }

    @Override
    public void printClub(InnerRecordInfo info) {
        mView.showLoading();
        if (mGetInnerHoleBatchSubscription != null) {
            mGetInnerHoleBatchSubscription.unsubscribe();
        }
        mGetInnerHoleBatchSubscription = InnerManager.getInstance().getInnerHoleBatchSubscription(info.id, new Callback<InnerRecordInfo>() {
            @Override
            public void onSuccess(final InnerRecordInfo o) {
                mView.hideLoading();
                Observable
                        .create(new Observable.OnSubscribe<Void>() {
                            @Override
                            public void call(Subscriber<? super Void> subscriber) {
                                InnerManager.getInstance().printInnerRecordInfo(o, true, true, null);
                                subscriber.onNext(null);
                                subscriber.onCompleted();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("获取详情失败:" + error);
            }
        });
    }

    @Subscribe
    public void onEvent(InnerFinishEvent finishEvent) {
        load(false);
    }
}
