package com.xmd.cashier.manager;

import android.text.TextUtils;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.dal.bean.Trade;
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
    private AccountManager mAccountManager;
    private CashierManager mPosManager;
    private TradeManager mTradeManager;

    private BillManager() {
        mAccountManager = AccountManager.getInstance();
        mPosManager = CashierManager.getInstance();
        mTradeManager = TradeManager.getInstance();
    }

    public static BillManager getInstance() {
        return mInstance;
    }

    // 获取交易流水
    public Subscription getBillList(String billStart, String billEnd, int payType, int status, int pageNo, final Callback<BillRecordResult> callback) {
        return SpaRetrofit.getService().getBill(mAccountManager.getToken(), billStart, billEnd, payType, status, pageNo, AppConstants.APP_LIST_PAGE_SIZE)
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
        return SpaRetrofit.getService().searchBill(mAccountManager.getToken(), tradeNO, pageNo, AppConstants.APP_LIST_PAGE_SIZE)
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

    // 补打小票
    public void againPrint(BillInfo info) {
        String format = MainApplication.getInstance().getString(R.string.cashier_money);
        // 抬头
        mPosManager.printTextCenter("深圳市小摩豆网络科技");
        mPosManager.printTextCenter("(消费单)");
        mPosManager.printTextCenter("--重打小票--");

        mPosManager.printDivide();
        mPosManager.printText("订单金额: ", String.format(format, Utils.moneyToStringEx(info.originMoney)));

        mPosManager.printText("减免金额: ", String.format(format, Utils.moneyToStringEx(info.userDiscountMoney + info.memberPayDiscountMoney + info.couponDiscountMoney)));
        switch (info.discountType) {
            case Trade.DISCOUNT_TYPE_COUPON:
                mPosManager.printText("|--优惠金额: ", String.format(format, Utils.moneyToStringEx(info.couponDiscountMoney)));
                break;
            case Trade.DISCOUNT_TYPE_USER:
                mPosManager.printText("|--手动减免: ", String.format(format, Utils.moneyToStringEx(info.userDiscountMoney)));
                break;
            case Trade.DISCOUNT_TYPE_NONE:
            default:
                mPosManager.printText("|--其他优惠: ", String.format(format, Utils.moneyToStringEx(info.userDiscountMoney + info.couponDiscountMoney)));
                break;
        }
        mPosManager.printText("|--会员折扣: ", String.format(format, Utils.moneyToStringEx(info.memberPayDiscountMoney)));

        mPosManager.printText("实收金额: ", String.format(format, Utils.moneyToStringEx(info.memberPayMoney + info.posPayMoney)));
        mPosManager.printText("|--" + Utils.getPayTypeString(info.posPayType) + ": ", String.format(format, Utils.moneyToStringEx(info.posPayMoney)));
        mPosManager.printText("|--会员支付: ", String.format(format, Utils.moneyToStringEx(info.memberPayMoney)));

        if (info.status == AppConstants.PAY_STATUS_REFUND) {
            mPosManager.printText("退款金额: ", String.format(format, Utils.moneyToStringEx(info.refundMoney)));
        }
        mPosManager.printDivide();

        mPosManager.printText("交易号: " + info.tradeNo);
        mPosManager.printText("收款方式: " + Utils.getPayTypeString(info.posPayType));
        mPosManager.printText("创建时间: " + DateUtils.doLong2String(Long.parseLong(info.payDate)));
        mPosManager.printText("收款人: " + (TextUtils.isEmpty(info.payOperator) ? "匿名" : info.payOperator));
        mPosManager.printTextCenter("\n");

        if (info.status == AppConstants.PAY_STATUS_REFUND) {
            mPosManager.printText("退款单号: " + info.refundNo);
            mPosManager.printText("退款时间: " + DateUtils.doLong2String(Long.parseLong(info.refundDate)));
            mPosManager.printText("退款人: " + (TextUtils.isEmpty(info.refundOperator) ? "匿名" : info.refundOperator));
        }
        mPosManager.printBitmap(mTradeManager.getClubQRCodeSync());
        mPosManager.printTextCenter("扫一扫，关注9358，约技师，享优惠");
        mPosManager.printEnd();
    }
}
