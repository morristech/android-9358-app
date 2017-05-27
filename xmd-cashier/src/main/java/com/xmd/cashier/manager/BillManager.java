package com.xmd.cashier.manager;

import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.net.NetworkSubscriber;
import com.xmd.cashier.dal.net.SpaRetrofit;
import com.xmd.cashier.dal.net.response.BillRecordResult;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 16-11-30.
 * 管理交易流水
 */

public class BillManager {
    private static BillManager mInstance = new BillManager();

    private BillManager() {
    }

    public static BillManager getInstance() {
        return mInstance;
    }

    // 获取交易流水
    public Subscription getBillList(String billStart, String billEnd, int payType, int status, int pageNo, final Callback<BillRecordResult> callback) {
        return SpaRetrofit.getService().getBill(AccountManager.getInstance().getToken(), billStart, billEnd, payType, status, pageNo, AppConstants.APP_LIST_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<BillRecordResult>() {

                    @Override
                    public void onCallbackSuccess(BillRecordResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        e.printStackTrace();
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // 搜索交易流水
    public Subscription searchBillList(String tradeNO, int pageNo, final Callback<BillRecordResult> callback) {
        return SpaRetrofit.getService().searchBill(AccountManager.getInstance().getToken(), tradeNO, pageNo, AppConstants.APP_LIST_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<BillRecordResult>() {
                    @Override
                    public void onCallbackSuccess(BillRecordResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }
}
