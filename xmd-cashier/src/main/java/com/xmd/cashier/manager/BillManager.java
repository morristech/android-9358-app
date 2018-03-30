package com.xmd.cashier.manager;

import android.text.TextUtils;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.BillRecordResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.Date;

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

    public void printBillRecord(BillInfo info, boolean keep) {
        mPos.setPrintListener();
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter((keep ? "商户存根" : "客户联") + "(补打小票)");
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
        mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(info.originMoney));
        if (info.userDiscountMoney > 0) {
            mPos.printText("手动减免：", "-￥ " + Utils.moneyToStringEx(info.userDiscountMoney));
        }
        if (info.memberPayDiscountMoney > 0) {
            mPos.printText("会员优惠：", "-￥ " + Utils.moneyToStringEx(info.memberPayDiscountMoney));
        }
        if (info.couponDiscountMoney > 0) {
            mPos.printText("用券抵扣：", "-￥ " + Utils.moneyToStringEx(info.couponDiscountMoney));
        }
        mPos.printDivide();
        mPos.printRight("实收金额：" + Utils.moneyToStringEx(info.memberPayMoney + info.posPayMoney) + " 元", true);
        mPos.printDivide();
        mPos.printText("交易号：", info.tradeNo);
        mPos.printText("交易时间：", DateUtils.doLong2String(Long.parseLong(info.payDate)));
        mPos.printText("支付方式：", Utils.getPayTypeString(info.posPayType));
        mPos.printText("收款人员：", (TextUtils.isEmpty(info.payOperator) ? "匿名" : info.payOperator));
        mPos.printText("打印时间：", DateUtils.doDate2String(new Date()));
        if (!keep) {
            byte[] qrCodeBytes = QrcodeManager.getInstance().getClubQRCodeSync();
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
                mPos.printCenter("微信扫码，选技师、抢优惠");
            }
        }
        mPos.printEnd();
    }
}
