package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.app.EventBusSafeRegister;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerRecordContract;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.dal.event.InnerFinishEvent;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.InnerRecordListResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Observable;
import rx.Subscription;

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
        EventBusSafeRegister.register(this);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        EventBusSafeRegister.unregister(this);
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
    public void onDetail(TradeRecordInfo info) {
        mView.showLoading();
        if (mGetInnerHoleBatchSubscription != null) {
            mGetInnerHoleBatchSubscription.unsubscribe();
        }
        mGetInnerHoleBatchSubscription = TradeManager.getInstance().getHoleBatchDetail(info.id, new Callback<TradeRecordInfo>() {
            @Override
            public void onSuccess(TradeRecordInfo o) {
                mView.hideLoading();
                UiNavigation.gotoInnerDetailActivity(mContext, AppConstants.INNER_DETAIL_SOURCE_RECORD, o);
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("获取详情失败:" + error);
            }
        });
    }

    @Override
    public void onPay(TradeRecordInfo info) {
        mView.showLoading();
        if (mGetInnerHoleBatchSubscription != null) {
            mGetInnerHoleBatchSubscription.unsubscribe();
        }
        mGetInnerHoleBatchSubscription = TradeManager.getInstance().getHoleBatchDetail(info.id, new Callback<TradeRecordInfo>() {
            @Override
            public void onSuccess(TradeRecordInfo o) {
                mView.hideLoading();
                TradeManager.getInstance().initTradeByRecord(o);
                if (o.paidAmount > 0) {
                    UiNavigation.gotoInnerModifyActivity(mContext);
                } else {
                    UiNavigation.gotoInnerMethodActivity(mContext, AppConstants.INNER_METHOD_SOURCE_RECORD, o);
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("获取详情失败:" + error);
            }
        });
    }

    @Override
    public void printClient(TradeRecordInfo info) {
        mView.showLoading();
        if (mGetInnerHoleBatchSubscription != null) {
            mGetInnerHoleBatchSubscription.unsubscribe();
        }
        mGetInnerHoleBatchSubscription = TradeManager.getInstance().getHoleBatchDetail(info.id, new Callback<TradeRecordInfo>() {
            @Override
            public void onSuccess(TradeRecordInfo o) {
                mView.hideLoading();
                TradeManager.getInstance().printTradeRecordInfoAsync(o, true, false);
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("获取详情失败:" + error);
            }
        });
    }

    @Override
    public void printClub(TradeRecordInfo info) {
        mView.showLoading();
        if (mGetInnerHoleBatchSubscription != null) {
            mGetInnerHoleBatchSubscription.unsubscribe();
        }
        mGetInnerHoleBatchSubscription = TradeManager.getInstance().getHoleBatchDetail(info.id, new Callback<TradeRecordInfo>() {
            @Override
            public void onSuccess(TradeRecordInfo o) {
                mView.hideLoading();
                TradeManager.getInstance().printTradeRecordInfoAsync(o, true, true);
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("获取详情失败:" + error);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InnerFinishEvent finishEvent) {
        load(false);
    }
}
