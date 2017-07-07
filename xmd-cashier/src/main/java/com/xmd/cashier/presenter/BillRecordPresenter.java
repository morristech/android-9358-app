package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.BillRecordContract;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.dal.net.response.BillRecordResult;
import com.xmd.cashier.manager.BillManager;
import com.xmd.cashier.manager.Callback;

import java.util.Calendar;
import java.util.Date;

import rx.Subscription;

/**
 * Created by zr on 16-11-29.
 */

public class BillRecordPresenter implements BillRecordContract.Presenter {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private Context mContext;
    private BillRecordContract.View mView;
    private BillManager mBillManager;
    private Subscription mGetBillListSubscription;
    private int mPageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;

    public BillRecordPresenter(Context context, BillRecordContract.View view) {
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
        if (mGetBillListSubscription != null) {
            mGetBillListSubscription.unsubscribe();
        }
    }

    @Override
    public void loadBills(int timeSelect, int typeSelect, int statusSelect) {
        mPageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showLoadNoNetwork();
            return;
        }
        if (mGetBillListSubscription != null) {
            mGetBillListSubscription.unsubscribe();
        }
        mView.showLoadIng();
        mGetBillListSubscription = mBillManager.getBillList(getStartTime(timeSelect), getEndTime(timeSelect), typeSelect, statusSelect, mPageIndex,
                new Callback<BillRecordResult>() {
                    @Override
                    public void onSuccess(BillRecordResult o) {
                        mView.hideLoadIng();
                        if (o.getRespData() == null || o.getRespData().list == null || o.getRespData().list.size() == 0) {
                            mView.showLoadEmpty();
                        } else {
                            mView.showLoadSuccess();
                            mView.updateSumInfo(o.getRespData().count, o.getRespData().payMoney);
                            if (AppConstants.APP_LIST_DEFAULT_PAGE < o.getPageCount()) {
                                mPageIndex++;
                                mView.showMoreSuccess();
                                mView.setHasMore(true);
                            } else {
                                mView.showMoreNone();
                                mView.setHasMore(false);
                            }
                            mView.showBillData(o.getRespData().list);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        mView.hideLoadIng();
                        mView.showLoadError();
                    }
                });
    }

    @Override
    public void loadMoreBills(int timeSelect, int typeSelect, int statusSelect) {
        mView.showMoreIng();
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showMoreNoNetwork();
            return;
        }
        if (mGetBillListSubscription != null) {
            mGetBillListSubscription.unsubscribe();
        }
        mGetBillListSubscription = mBillManager.getBillList(getStartTime(timeSelect), getEndTime(timeSelect), typeSelect, statusSelect, mPageIndex,
                new Callback<BillRecordResult>() {
                    @Override
                    public void onSuccess(BillRecordResult o) {
                        if (mPageIndex < o.getPageCount()) {
                            mPageIndex++;
                            mView.showMoreSuccess();
                            mView.setHasMore(true);
                        } else {
                            mView.showMoreNone();
                            mView.setHasMore(false);
                        }
                        mView.showBillData(o.getRespData().list);
                    }

                    @Override
                    public void onError(String error) {
                        mView.showMoreError();
                    }
                });
    }

    @Override
    public void onClickSearch() {
        UiNavigation.gotoBillSearchActivity(mContext);
    }

    @Override
    public void onBillItemClick(BillInfo info) {
        UiNavigation.gotoBillDetailActivity(mContext, info);
    }

    @Override
    public void onTimePopClick() {
        mView.showTimePop();
    }

    @Override
    public void onTypePopClick() {
        mView.showTypePop();
    }

    @Override
    public void onStatusPopClick() {
        mView.showStatusPop();
    }

    @Override
    public void dismissTimePop() {
        mView.resetTimePop();
    }

    @Override
    public void dismissTypePop() {
        mView.resetTypePop();
    }

    @Override
    public void dismissStatusPop() {
        mView.resetStatusPop();
    }

    private String getStartTime(int select) {
        // 默认三月前
        String start = getFormatString(getThreeMonthAgo());
        switch (select) {
            case AppConstants.PAY_TIME_MONTH:
                // 月初
                start = getFormatString(getThisMonth());
                break;
            case AppConstants.PAY_TIME_TODAY:
                // 今天
                start = getFormatString(getToDay());
                break;
            case AppConstants.PAY_TIME_ALL_THREE_MONTH:
            default:
                break;
        }
        return start;
    }

    private String getEndTime(int select) {
        String end = getFormatString(getToDay());
        switch (select) {
            case AppConstants.PAY_TIME_MONTH:
                // 昨天
                end = getFormatString(getYesterDay());
                break;
            default:
                break;
        }
        return end;
    }

    // 三个月前
    private Date getThreeMonthAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        return calendar.getTime();
    }

    // 月初
    private Date getThisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    // 昨天
    private Date getYesterDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    // 今天
    private Date getToDay() {
        return Calendar.getInstance().getTime();
    }

    private String getFormatString(Date date) {
        return Utils.getFormatString(date, DEFAULT_DATE_FORMAT);
    }
}
