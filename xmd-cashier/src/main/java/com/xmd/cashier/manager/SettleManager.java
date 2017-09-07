package com.xmd.cashier.manager;

import android.text.TextUtils;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.SettleSummaryInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.SettleRecordResult;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscription;

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
    public Subscription saveSettle(String settleRecord, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .saveSettle(AccountManager.getInstance().getToken(), settleRecord);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
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
        Observable<SettleSummaryResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getSettleCurrent(AccountManager.getInstance().getToken());
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

    // 根据记录ID获取结算详情
    public Subscription getSettleDetail(String recordId, final Callback<SettleSummaryResult> callback) {
        Observable<SettleSummaryResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getSettleDetail(AccountManager.getInstance().getToken(), recordId);
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
    public Subscription getSettleRecord(String pageStart, String pageSize, String settleYm, final Callback<SettleRecordResult> callback) {
        Observable<SettleRecordResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getSettleRecord(AccountManager.getInstance().getToken(), pageStart, pageSize, settleYm);
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

    public void print(SettleSummaryResult.RespData respData, boolean retry) {
        SettleSummaryInfo info = respData.obj;
        List<SettleSummaryInfo> list = respData.recordDetailList;

        if (info != null) {
            mPos.printCenter(AccountManager.getInstance().getClubName());
            mPos.printCenter("(结算清单)");
            if (retry) {
                mPos.printCenter("--补打小票--");
            }
            mPos.printDivide();

            mPos.printText("订单总金额", "￥" + Utils.moneyToStringEx(info.orderTotalMoney), true);

            mPos.printText("减免总金额", "￥" + Utils.moneyToStringEx(info.deductTotalMoney), true);
            mPos.printText("    优惠减免：", "￥" + Utils.moneyToStringEx(info.preferentialDeduct) + "  ");
            mPos.printText("    手动减免：", "￥" + Utils.moneyToStringEx(info.manualDeduct) + "  ");
            mPos.printText("    会员折扣：", "￥" + Utils.moneyToStringEx(info.discountDeduct) + "  ");

            mPos.printText("实收总金额", "￥" + Utils.moneyToStringEx(info.incomeTotalMoney), true);
            mPos.printText("    银行卡实收：", "￥" + Utils.moneyToStringEx(info.bankCardIncome) + "  ");
            mPos.printText("    现金实收：", "￥" + Utils.moneyToStringEx(info.moneyIncome) + "  ");
            mPos.printText("    微信实收：", "￥" + Utils.moneyToStringEx(info.wechatIncome) + "  ");
            mPos.printText("    会员支付实收：", "￥" + Utils.moneyToStringEx(info.memberPayIncome) + "  ");
            mPos.printDivide();

            mPos.printRight("共" + info.orderCount + "笔");
            mPos.printRight("开始时间：" + info.startTime);
            mPos.printRight("截止时间：" + info.endTime);
            mPos.printRight("结算人员：" + (TextUtils.isEmpty(info.operatorName) ? AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")" : info.operatorName));
            mPos.printRight("结算时间：" + (TextUtils.isEmpty(info.createTime) ? Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT) : info.createTime));
            if (!TextUtils.isEmpty(info.cashierName)) {
                mPos.printRight("收款人员：" + info.cashierName);
            }
        }
        mPos.printDivide();

        if (list != null) {
            for (SettleSummaryInfo subInfo : list) {
                mPos.printText("收款人员：" + subInfo.cashierName);
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
