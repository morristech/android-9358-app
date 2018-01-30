package com.xmd.cashier.manager;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.SettleBusinessInfo;
import com.xmd.cashier.dal.bean.SettleContentInfo;
import com.xmd.cashier.dal.bean.SettleDetailInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.SettleRecordResult;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.Date;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-3-30.
 */

public class SettleManager {
    private IPos mPos;
    private static SettleManager instance = new SettleManager();

    private SettleManager() {
        mPos = PosFactory.getCurrentCashier();
    }

    public static SettleManager getInstance() {
        return instance;
    }

    // 确认结算
    public Subscription saveSettle(String amount, String fastPay, String recharge, String discount, String startTime, String endTime, final Callback<StringResult> callback) {
        Observable<StringResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .saveSettle(AccountManager.getInstance().getToken(), amount, fastPay, recharge, discount, startTime, endTime);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 获取结算详情
    public Subscription getSettleDetail(String id, String settleTime, final Callback<SettleSummaryResult> callback) {
        Observable<SettleSummaryResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getSettleDetail(AccountManager.getInstance().getToken(), id, settleTime);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<SettleSummaryResult>() {
            @Override
            public void onCallbackSuccess(SettleSummaryResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 获取结算记录
    public Subscription getSettleRecord(String settleYM, String page, final Callback<SettleRecordResult> callback) {
        Observable<SettleRecordResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getSettleRecord(AccountManager.getInstance().getToken(), page, settleYM);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<SettleRecordResult>() {
            @Override
            public void onCallbackSuccess(SettleRecordResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    public void printAsync(final SettleSummaryResult.RespData data, final boolean retry) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        print(data, retry);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void print(SettleSummaryResult.RespData data, boolean retry) {
        mPos.printCenter("交接班结算小票");
        if (retry) {
            mPos.printCenter("--补打小票--");
        }
        mPos.printDivide();
        mPos.printRight("开始时间：" + data.startTime);
        mPos.printRight("结束时间：" + data.endTime);
        mPos.printDivide();

        if (data.settleList != null && !data.settleList.isEmpty()) {
            for (SettleContentInfo contentInfo : data.settleList) {
                mPos.printRight("收款人员：" + contentInfo.operatorName);
                if (contentInfo.businessList != null && !contentInfo.businessList.isEmpty()) {
                    for (SettleBusinessInfo businessInfo : contentInfo.businessList) {
                        mPos.printText(businessInfo.businessName + " (" + businessInfo.count + "笔)", String.format(ResourceUtils.getString(R.string.cashier_money), Utils.moneyToStringEx(businessInfo.amount)));
                        if (businessInfo.detailList != null && !businessInfo.detailList.isEmpty()) {
                            for (SettleDetailInfo detailInfo : businessInfo.detailList) {
                                mPos.printText("    " + detailInfo.name + " (" + detailInfo.count + "笔)", String.format(ResourceUtils.getString(R.string.cashier_money), Utils.moneyToStringEx(detailInfo.amount)));
                            }
                        }
                        if (businessInfo.remarkList != null && !businessInfo.remarkList.isEmpty()) {
                            mPos.printText("    备注");
                            for (SettleDetailInfo detailInfo : businessInfo.remarkList) {
                                mPos.printText("    " + detailInfo.name + " (" + detailInfo.count + "笔)", String.format(ResourceUtils.getString(R.string.cashier_money), Utils.moneyToStringEx(detailInfo.amount)));
                            }
                        }
                    }
                }
                mPos.printDivide();
            }
        }

        mPos.printRight("结算时间：" + data.createTime);
        mPos.printRight("结算人员：" + data.settleName);
        mPos.printRight("打印时间：" + DateUtils.doDate2String(new Date()));
        mPos.printRight("打印人员：" + AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")");
        mPos.printEnd();
    }
}
