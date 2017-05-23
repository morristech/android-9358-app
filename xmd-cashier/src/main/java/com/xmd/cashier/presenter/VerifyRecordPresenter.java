package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyRecordContract;
import com.xmd.cashier.dal.bean.VerifyRecordInfo;
import com.xmd.cashier.dal.bean.VerifyTypeInfo;
import com.xmd.cashier.dal.net.response.VerifyRecordResult;
import com.xmd.cashier.dal.net.response.VerifyTypeResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Subscription;

/**
 * Created by zr on 17-5-2.
 */

public class VerifyRecordPresenter implements VerifyRecordContract.Presenter {
    private static final int INIT_MIN_RECORD_COUNT = 10;
    private Context mContext;
    private VerifyRecordContract.View mView;

    private boolean hasMore;
    private boolean isLoading;
    private boolean isRefreshing;

    private String mSearch;
    private String mFilter;

    private int pageIndex;
    private String startDate;
    private String endDate;

    private Subscription mGetVerifyRecordListSubscription;
    private Subscription mGetVerifyTypeMapSubscription;

    private List<VerifyTypeInfo> mTypeMap = new ArrayList<>();

    // 首次进入时让记录满屏 >10 条
    private int initPageCount;

    public VerifyRecordPresenter(Context context, VerifyRecordContract.View view) {
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
        if (mGetVerifyRecordListSubscription != null) {
            mGetVerifyRecordListSubscription.unsubscribe();
        }
        if (mGetVerifyTypeMapSubscription != null) {
            mGetVerifyTypeMapSubscription.unsubscribe();
        }
    }

    public void load(final boolean init, final String start, final String end) {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        pageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;
        initPageCount = 0;
        if (!init) {
            mView.showRefreshIng();
        }
        if (!Utils.isNetworkEnabled(mContext)) {
            isRefreshing = false;
            mView.showRefreshNoNetwork();
            return;
        }
        if (mGetVerifyRecordListSubscription != null) {
            mGetVerifyRecordListSubscription.unsubscribe();
        }
        mGetVerifyRecordListSubscription = VerifyManager.getInstance().getVerifyRecordList(
                pageIndex,
                AppConstants.APP_LIST_PAGE_SIZE,
                mSearch,
                mFilter,
                start,
                end,
                new Callback<VerifyRecordResult>() {
                    @Override
                    public void onSuccess(VerifyRecordResult o) {
                        mView.clearData();
                        if (o.respData == null) {
                            isRefreshing = false;
                            mView.showRefreshEmpty();
                        } else {
                            if (o.respData.total == 0) {
                                // 当前没有返回数据
                                isRefreshing = false;
                                if (o.respData.remainderCount == 0) {
                                    // 无剩余数据
                                    hasMore = false;
                                    mView.showRefreshEmpty();
                                } else {
                                    // 有剩余数据,加载下一个月的数据
                                    hasMore = true;
                                    startDate = getPreMonthStart(start);
                                    endDate = getPreMonthEnd(end);
                                    load(false, startDate, endDate);
                                }
                            } else {
                                // 存在数据
                                mView.showRefreshSuccess();
                                for (VerifyRecordInfo info : o.respData.data) {
                                    info.currentCount = o.respData.total;
                                }
                                mView.showData(o.respData.data);
                                initPageCount += o.respData.data.size();
                                if (pageIndex < o.pageCount) {
                                    pageIndex++;
                                    hasMore = true;
                                    startDate = start;
                                    endDate = end;
                                    mView.showMoreSuccess();
                                    isRefreshing = false;
                                } else {
                                    if (o.respData.remainderCount == 0) {
                                        hasMore = false;
                                        mView.showMoreNone();
                                        isRefreshing = false;
                                    } else {
                                        pageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;
                                        hasMore = true;
                                        startDate = getPreMonthStart(start);
                                        endDate = getPreMonthEnd(end);
                                        mView.showMoreSuccess();
                                        isRefreshing = false;
                                        if (initPageCount < INIT_MIN_RECORD_COUNT) {
                                            loadMore();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        isRefreshing = false;
                        mView.showRefreshError();
                        mView.showToast(error);
                    }
                }
        );
    }

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
        if (mGetVerifyRecordListSubscription != null) {
            mGetVerifyRecordListSubscription.unsubscribe();
        }
        mGetVerifyRecordListSubscription = VerifyManager.getInstance().getVerifyRecordList(
                pageIndex,
                AppConstants.APP_LIST_PAGE_SIZE,
                mSearch,
                mFilter,
                startDate,
                endDate,
                new Callback<VerifyRecordResult>() {
                    @Override
                    public void onSuccess(VerifyRecordResult o) {
                        if (o.respData == null) {
                            isLoading = false;
                            mView.showMoreError();
                        } else {
                            if (o.respData.total == 0) {
                                // 当前没有返回数据
                                isLoading = false;
                                if (o.respData.remainderCount == 0) {
                                    // 无剩余数据
                                    hasMore = false;
                                    mView.showMoreNone();
                                } else {
                                    // 有剩余数据,加载下一个月的数据
                                    startDate = getPreMonthStart(startDate);
                                    endDate = getPreMonthEnd(endDate);
                                    hasMore = true;
                                    mView.showMoreSuccess();
                                    loadMore();
                                }
                            } else {
                                // 存在数据
                                for (VerifyRecordInfo info : o.respData.data) {
                                    info.currentCount = o.respData.total;
                                }
                                mView.showData(o.respData.data);
                                initPageCount += o.respData.data.size();
                                if (pageIndex < o.pageCount) {
                                    pageIndex++;
                                    hasMore = true;
                                    mView.showMoreSuccess();
                                    isLoading = false;
                                } else {
                                    if (o.respData.remainderCount == 0) {
                                        hasMore = false;
                                        mView.showMoreNone();
                                        isLoading = false;
                                    } else {
                                        pageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;
                                        startDate = getPreMonthStart(startDate);
                                        endDate = getPreMonthEnd(endDate);
                                        hasMore = true;
                                        mView.showMoreSuccess();
                                        isLoading = false;
                                        if (initPageCount < INIT_MIN_RECORD_COUNT) {
                                            loadMore();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        isLoading = false;
                        mView.showMoreError();
                    }
                }
        );
    }

    @Override
    public void loadTypeMap() {
        if (mGetVerifyTypeMapSubscription != null) {
            mGetVerifyTypeMapSubscription.unsubscribe();
        }
        mGetVerifyTypeMapSubscription = VerifyManager.getInstance().getVerifyTypeList(new Callback<VerifyTypeResult>() {
            @Override
            public void onSuccess(VerifyTypeResult o) {
                mTypeMap = o.respData;
                if (mTypeMap.size() > 0) {
                    List<String> tempList = new ArrayList<>();
                    for (VerifyTypeInfo info : mTypeMap) {
                        tempList.add(info.value);
                    }
                    tempList.add(0, AppConstants.STATUS_ALL_TEXT);
                    mView.initFilter(tempList);
                }
            }

            @Override
            public void onError(String error) {
                XLogger.e(error);
            }
        });
    }

    @Override
    public void setTypeFilter(String filter) {
        mFilter = null;
        for (VerifyTypeInfo info : mTypeMap) {
            if (info.value.equals(filter)) {
                mFilter = info.key;
            }
        }
    }

    @Override
    public void setSearch(String search) {
        mSearch = search;
    }

    @Override
    public void onRecordClick(String recordId) {
        UiNavigation.gotoVerifyDetailActivity(mContext, recordId);
    }

    // 上个月初
    private String getPreMonthStart(String startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.doString2Date(startDate, DateUtils.DF_DEFAULT));
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        return DateUtils.doDate2String(calendar.getTime(), DateUtils.DF_DEFAULT);
    }

    // 上个月末
    private String getPreMonthEnd(String endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.doString2Date(endDate, DateUtils.DF_DEFAULT));
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        return DateUtils.doDate2String(calendar.getTime(), DateUtils.DF_DEFAULT);
    }
}
