package com.xmd.cashier.manager;

import android.text.TextUtils;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.BillRecordResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 16-11-30.
 * 管理交易流水
 */

public class BillManager {
    private IPos mPos;
    private static BillManager mInstance = new BillManager();

    private BillManager() {
        mPos = PosFactory.getCurrentCashier();
    }

    public static BillManager getInstance() {
        return mInstance;
    }

    // 获取交易流水
    public Subscription getBillList(String billStart, String billEnd, int payType, int status, int pageNo, final Callback<BillRecordResult> callback) {
        Observable<BillRecordResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getBill(AccountManager.getInstance().getToken(), billStart, billEnd, payType, status, pageNo, AppConstants.APP_LIST_PAGE_SIZE);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BillRecordResult>() {

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
        Observable<BillRecordResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .searchBill(AccountManager.getInstance().getToken(), tradeNO, pageNo, AppConstants.APP_LIST_PAGE_SIZE);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BillRecordResult>() {
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

    public void print(BillInfo info) {
        String format = MainApplication.getInstance().getString(R.string.cashier_money);

        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("(消费账单)");
        mPos.printCenter("--补打小票--");

        mPos.printDivide();
        mPos.printText("订单金额: ", String.format(format, Utils.moneyToStringEx(info.originMoney)), true);

        mPos.printText("减免金额: ", String.format(format, Utils.moneyToStringEx(info.userDiscountMoney + info.memberPayDiscountMoney + info.couponDiscountMoney)), true);
        switch (info.discountType) {
            case AppConstants.DISCOUNT_TYPE_COUPON:
                mPos.printText("|--优惠金额: ", String.format(format, Utils.moneyToStringEx(info.couponDiscountMoney)));
                break;
            case AppConstants.DISCOUNT_TYPE_USER:
                mPos.printText("|--手动减免: ", String.format(format, Utils.moneyToStringEx(info.userDiscountMoney)));
                break;
            case AppConstants.DISCOUNT_TYPE_NONE:
            default:
                mPos.printText("|--其他优惠: ", String.format(format, Utils.moneyToStringEx(info.userDiscountMoney + info.couponDiscountMoney)));
                break;
        }
        mPos.printText("|--会员折扣: ", String.format(format, Utils.moneyToStringEx(info.memberPayDiscountMoney)));

        mPos.printText("实收金额: ", String.format(format, Utils.moneyToStringEx(info.memberPayMoney + info.posPayMoney)), true);
        mPos.printText("|--" + Utils.getPayTypeString(info.posPayType) + ": ", String.format(format, Utils.moneyToStringEx(info.posPayMoney)));
        mPos.printText("|--会员支付: ", String.format(format, Utils.moneyToStringEx(info.memberPayMoney)));

        if (info.status == AppConstants.PAY_STATUS_REFUND) {
            mPos.printText("退款金额: ", String.format(format, Utils.moneyToStringEx(info.refundMoney)));
        }
        mPos.printDivide();

        mPos.printText("交易号: " + info.tradeNo);
        mPos.printText("收款方式: " + Utils.getPayTypeString(info.posPayType));
        mPos.printText("创建时间: " + DateUtils.doLong2String(Long.parseLong(info.payDate)));
        mPos.printText("收款人: " + (TextUtils.isEmpty(info.payOperator) ? "匿名" : info.payOperator));
        mPos.printCenter("\n");

        if (info.status == AppConstants.PAY_STATUS_REFUND) {
            mPos.printText("退款单号: " + info.refundNo);
            mPos.printText("退款时间: " + DateUtils.doLong2String(Long.parseLong(info.refundDate)));
            mPos.printText("退款人: " + (TextUtils.isEmpty(info.refundOperator) ? "匿名" : info.refundOperator));
        }
        mPos.printBitmap(TradeManager.getInstance().getClubQRCodeSync());
        mPos.printCenter("扫一扫，关注9358，约技师，享优惠");
        mPos.printEnd();
    }
}
