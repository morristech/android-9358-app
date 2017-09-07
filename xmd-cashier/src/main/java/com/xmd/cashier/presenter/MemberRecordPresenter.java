package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberRecordContract;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.MemberRecordListResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-7-11.
 */

public class MemberRecordPresenter implements MemberRecordContract.Presenter {
    private Context mContext;
    private MemberRecordContract.View mView;

    private boolean hasMore;
    private boolean isLoading;
    private boolean isRefreshing;

    private String mSearch;
    private String mFilter;

    private int pageIndex;

    private Subscription mGetMemberRecordListSubscription;

    public MemberRecordPresenter(Context context, MemberRecordContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mSearch = null;
        mFilter = null;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mGetMemberRecordListSubscription != null) {
            mGetMemberRecordListSubscription.unsubscribe();
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
        if (mGetMemberRecordListSubscription != null) {
            mGetMemberRecordListSubscription.unsubscribe();
        }
        pageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;
        Observable<MemberRecordListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getMemberRecordList(AccountManager.getInstance().getToken(), String.valueOf(pageIndex), String.valueOf(AppConstants.APP_LIST_PAGE_SIZE), mFilter, mSearch);
        mGetMemberRecordListSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<MemberRecordListResult>() {
            @Override
            public void onCallbackSuccess(MemberRecordListResult result) {
                mView.clearData();
                if (result.getRespData() == null || result.getRespData().isEmpty()) {
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
        if (mGetMemberRecordListSubscription != null) {
            mGetMemberRecordListSubscription.unsubscribe();
        }
        Observable<MemberRecordListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getMemberRecordList(AccountManager.getInstance().getToken(), String.valueOf(pageIndex), String.valueOf(AppConstants.APP_LIST_PAGE_SIZE), mFilter, mSearch);
        mGetMemberRecordListSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<MemberRecordListResult>() {
            @Override
            public void onCallbackSuccess(MemberRecordListResult result) {
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
    public void setSearch(String search) {
        mSearch = search;
    }

    @Override
    public void setFilter(String filter) {
        mFilter = filter;
    }

    @Override
    public void print(final MemberRecordInfo info, final boolean retry) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        MemberManager.getInstance().printMemberRecordInfo(info, retry, true, null);
                        MemberManager.getInstance().printMemberRecordInfo(info, retry, false, null);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
