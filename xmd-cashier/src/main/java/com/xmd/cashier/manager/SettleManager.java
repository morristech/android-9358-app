package com.xmd.cashier.manager;

import android.text.TextUtils;

import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.SettleSummaryInfo;
import com.xmd.cashier.dal.net.NetworkSubscriber;
import com.xmd.cashier.dal.net.SpaRetrofit;
import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.dal.net.response.SettleRecordResult;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;

import java.util.Date;
import java.util.List;

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
    public Subscription saveSettle(String settleRecord, final Callback<BaseResult> callback) {
        return SpaRetrofit.getService().saveSettle(AccountManager.getInstance().getToken(), settleRecord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<BaseResult>() {
                    @Override
                    public void onCallbackSuccess(BaseResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // 获取当前未结算的汇总
    public Subscription getSettleCurrent(final Callback<SettleSummaryResult> callback) {
        return SpaRetrofit.getService().getSettleCurrent(AccountManager.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<SettleSummaryResult>() {
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

    // 根据记录ID获取结算详情
    public Subscription getSettleDetail(String recordId, final Callback<SettleSummaryResult> callback) {
        return SpaRetrofit.getService().getSettleDetail(AccountManager.getInstance().getToken(), recordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<SettleSummaryResult>() {
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
    public Subscription getSettleRecord(String pageStart, String pageSize, String settleYm, final Callback<SettleRecordResult> callback) {
        return SpaRetrofit.getService().getSettleRecord(AccountManager.getInstance().getToken(), pageStart, pageSize, settleYm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<SettleRecordResult>() {
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

    public void print(SettleSummaryResult.RespData respData, boolean retry) {
        SettleSummaryInfo info = respData.obj;
        List<SettleSummaryInfo> list = respData.recordDetailList;

        if (info != null) {
            mPos.printText(AccountManager.getInstance().getClubName(), IPos.GRAVITY_CENTER);
            mPos.printText("(结算清单)", IPos.GRAVITY_CENTER);
            if (retry) {
                mPos.printText("--重打小票--", IPos.GRAVITY_CENTER);
            }
            mPos.printDivide();

            mPos.printText("订单总金额", "￥" + Utils.moneyToStringEx(info.orderTotalMoney));

            mPos.printText("减免总金额", "￥" + Utils.moneyToStringEx(info.deductTotalMoney));
            mPos.printText("    优惠减免：", "￥" + Utils.moneyToStringEx(info.preferentialDeduct) + "  ");
            mPos.printText("    手动减免：", "￥" + Utils.moneyToStringEx(info.manualDeduct) + "  ");
            mPos.printText("    会员折扣：", "￥" + Utils.moneyToStringEx(info.discountDeduct) + "  ");

            mPos.printText("实收总金额", "￥" + Utils.moneyToStringEx(info.incomeTotalMoney));
            mPos.printText("    银行卡实收：", "￥" + Utils.moneyToStringEx(info.bankCardIncome) + "  ");
            mPos.printText("    现金实收：", "￥" + Utils.moneyToStringEx(info.moneyIncome) + "  ");
            mPos.printText("    微信实收：", "￥" + Utils.moneyToStringEx(info.wechatIncome) + "  ");
            mPos.printText("    会员支付实收：", "￥" + Utils.moneyToStringEx(info.memberPayIncome) + "  ");
            mPos.printDivide();

            mPos.printText("共" + info.orderCount + "笔", IPos.GRAVITY_RIGHT);
            mPos.printText("开始时间：" + info.startTime, IPos.GRAVITY_RIGHT);
            mPos.printText("截止时间：" + info.endTime, IPos.GRAVITY_RIGHT);
            mPos.printText("结算人员：" + (TextUtils.isEmpty(info.operatorName) ? AccountManager.getInstance().getUser().userName : info.operatorName), IPos.GRAVITY_RIGHT);
            mPos.printText("结算时间：" + (TextUtils.isEmpty(info.createTime) ? Utils.getFormatString(new Date(), AppConstants.DEFAULT_DATE_FORMAT) : info.createTime), IPos.GRAVITY_RIGHT);
            if (!TextUtils.isEmpty(info.cashierName)) {
                mPos.printText("收款人员：" + info.cashierName, IPos.GRAVITY_RIGHT);
            }
        }
        mPos.printDivide();

        if (list != null) {
            for (SettleSummaryInfo subInfo : list) {
                mPos.printText("收款人员：" + subInfo.cashierName, IPos.GRAVITY_LEFT);
                mPos.printText("订单总金额", "￥" + Utils.moneyToStringEx(subInfo.orderTotalMoney));
                mPos.printText("减免总金额", "￥" + Utils.moneyToStringEx(subInfo.deductTotalMoney));
                mPos.printText("    优惠减免：", "￥" + Utils.moneyToStringEx(subInfo.preferentialDeduct) + "  ");
                mPos.printText("    手动减免：", "￥" + Utils.moneyToStringEx(subInfo.manualDeduct) + "  ");
                mPos.printText("    会员折扣：", "￥" + Utils.moneyToStringEx(subInfo.discountDeduct) + "  ");
                mPos.printText("实收总金额", "￥" + Utils.moneyToStringEx(subInfo.incomeTotalMoney));
                mPos.printText("    银行卡实收：", "￥" + Utils.moneyToStringEx(subInfo.bankCardIncome) + "  ");
                mPos.printText("    现金实收：", "￥" + Utils.moneyToStringEx(subInfo.moneyIncome) + "  ");
                mPos.printText("    微信实收：", "￥" + Utils.moneyToStringEx(subInfo.wechatIncome) + "  ");
                mPos.printText("    会员支付实收：", "￥" + Utils.moneyToStringEx(subInfo.memberPayIncome) + "  ");
                mPos.printDivide();
            }
        }

        mPos.printEnd();
    }
}
