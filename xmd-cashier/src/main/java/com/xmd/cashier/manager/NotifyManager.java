package com.xmd.cashier.manager;

import android.content.Intent;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.dal.bean.OrderRecordInfo;
import com.xmd.cashier.dal.net.NetworkSubscriber;
import com.xmd.cashier.dal.net.SpaRetrofit;
import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.dal.net.response.OnlinePayListResult;
import com.xmd.cashier.dal.net.response.OrderRecordListResult;

import java.io.Serializable;
import java.util.Date;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-4-11.
 * 订单预约 && 在线买单
 */

public class NotifyManager {
    private IPos mPos;

    private NotifyManager() {
        mPos = PosFactory.getCurrentCashier();
    }

    private static NotifyManager mInstance = new NotifyManager();

    public static NotifyManager getInstance() {
        return mInstance;
    }

    // 预约订单列表
    public Subscription getOrderRecordList(int page, String search, String status, final Callback<OrderRecordListResult> callback) {
        return SpaRetrofit.getService().getOrderRecordList(AccountManager.getInstance().getToken(),
                String.valueOf(page), String.valueOf(AppConstants.APP_LIST_PAGE_SIZE),
                search, status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<OrderRecordListResult>() {
                    @Override
                    public void onCallbackSuccess(OrderRecordListResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        e.printStackTrace();
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // 接受订单
    public Subscription acceptOrder(String orderId, final Callback<BaseResult> callback) {
        return SpaRetrofit.getService().updateOrderRecordStatus(AccountManager.getInstance().getToken(),
                AppConstants.SESSION_TYPE,
                AppConstants.ORDER_RECORD_STATUS_ACCEPT, orderId)
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

    // 拒绝订单
    public Subscription rejectOrder(String orderId, final Callback<BaseResult> callback) {
        return SpaRetrofit.getService().updateOrderRecordStatus(AccountManager.getInstance().getToken(),
                AppConstants.SESSION_TYPE,
                AppConstants.ORDER_RECORD_STATUS_REJECT, orderId)
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

    // 在线买单列表
    public Subscription getOnlinePayList(int page, String search, String status, final Callback<OnlinePayListResult> callback) {
        return SpaRetrofit.getService().getOnlinePayList(AccountManager.getInstance().getToken(),
                String.valueOf(page), String.valueOf(AppConstants.APP_LIST_PAGE_SIZE), AppConstants.APP_REQUEST_YES, search, status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<OnlinePayListResult>() {
                    @Override
                    public void onCallbackSuccess(OnlinePayListResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // 确认买单
    public Subscription passOnlinePay(String orderId, String status, final Callback<BaseResult> callback) {
        return SpaRetrofit.getService().updateOnlinePayStatus(AccountManager.getInstance().getToken(),
                orderId, status)
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

    // 请到前台
    public Subscription unPassOnlinePay(String orderId, String status, final Callback<BaseResult> callback) {
        return SpaRetrofit.getService().updateOnlinePayStatus(AccountManager.getInstance().getToken(),
                orderId, status)
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

    public void refreshOrderRecordNotify() {
        SpaRetrofit.getService().getOrderRecordList(AccountManager.getInstance().getToken(), String.valueOf(AppConstants.APP_LIST_DEFAULT_PAGE), String.valueOf(Integer.MAX_VALUE), null, AppConstants.ORDER_RECORD_STATUS_SUBMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<OrderRecordListResult>() {
                    @Override
                    public void onCallbackSuccess(OrderRecordListResult result) {
                        // 成功:判断列表,显示通知
                        if (result != null && result.respData != null && result.respData.size() > 0) {
                            for (OrderRecordInfo info : result.respData) {
                                info.tempNo = result.respData.indexOf(info) + 1;
                            }
                            Intent intent = new Intent();
                            intent.setAction(AppConstants.ACTION_CUSTOM_NOTIFY_RECEIVER);
                            intent.putExtra(AppConstants.EXTRA_NOTIFY_TYPE, AppConstants.EXTRA_NOTIFY_TYPE_ORDER_RECORD);
                            intent.putExtra(AppConstants.EXTRA_NOTIFY_DATA, (Serializable) result.respData);
                            MainApplication.getInstance().getApplicationContext().sendBroadcast(intent);
                        }
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        e.printStackTrace();
                        XLogger.i(e.getLocalizedMessage());
                    }
                });
    }

    public void refreshOnlinePayNotify() {
        SpaRetrofit.getService().getOnlinePayList(AccountManager.getInstance().getToken(), String.valueOf(AppConstants.APP_LIST_DEFAULT_PAGE), String.valueOf(Integer.MAX_VALUE), AppConstants.APP_REQUEST_YES, null, AppConstants.ONLINE_PAY_STATUS_PAID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<OnlinePayListResult>() {
                    @Override
                    public void onCallbackSuccess(OnlinePayListResult result) {
                        //成功:判断列表,显示通知
                        if (result != null && result.respData != null && result.respData.size() > 0) {
                            for (OnlinePayInfo info : result.respData) {
                                info.tempNo = result.respData.indexOf(info) + 1;
                            }
                            Intent intent = new Intent();
                            intent.setAction(AppConstants.ACTION_CUSTOM_NOTIFY_RECEIVER);
                            intent.putExtra(AppConstants.EXTRA_NOTIFY_TYPE, AppConstants.EXTRA_NOTIFY_TYPE_ONLINE_PAY);
                            intent.putExtra(AppConstants.EXTRA_NOTIFY_DATA, (Serializable) result.respData);
                            MainApplication.getInstance().getApplicationContext().sendBroadcast(intent);
                        }
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        e.printStackTrace();
                        XLogger.i(e.getLocalizedMessage());
                    }
                });
    }

    // 打印预约订单小票
    public void print(OrderRecordInfo info, boolean retry) {
        mPos.printCenter("小摩豆预约订单");
        if (retry) {
            mPos.printCenter("--重打小票--");
        }
        mPos.printCenter("\n");
        mPos.printDivide();
        mPos.printText("客户: ", (TextUtils.isEmpty(info.phoneNum) ? info.customerName : String.format("%s(%s)", info.customerName, info.phoneNum)));
        mPos.printText("技师: ", TextUtils.isEmpty(info.techSerialNo) ? info.techName : String.format("%s[%s]", info.techName, info.techSerialNo));
        mPos.printText("项目: ", TextUtils.isEmpty(info.itemName) ? "到店选择" : info.itemName);
        mPos.printDivide();
        mPos.printText("到店: ", info.appointTime);
        mPos.printText("已付: ", Utils.moneyToStringEx(info.downPayment) + "元");
        mPos.printDivide();
        mPos.printText("订单编号: ", info.id);
        mPos.printText("下单时间: ", info.createdAt);
        mPos.printText("打印时间: ", Utils.getFormatString(new Date(), AppConstants.DEFAULT_DATE_FORMAT));
        String status = null;
        switch (info.status) {
            case AppConstants.ORDER_RECORD_STATUS_ACCEPT:
                status = AppConstants.ORDER_RECORD_STATUS_ACCEPT_TEXT;
                break;
            case AppConstants.ORDER_RECORD_STATUS_CANCEL:
                status = AppConstants.ORDER_RECORD_STATUS_CANCEL_TEXT;
                break;
            case AppConstants.ORDER_RECORD_STATUS_COMPLETE:
                if (info.downPayment > 0) {
                    status = AppConstants.ORDER_RECORD_STATUS_COMPLETE_TEXT;
                } else {
                    status = AppConstants.ORDER_RECORD_STATUS_DONE_TEXT;
                }
                break;
            case AppConstants.ORDER_RECORD_STATUS_FAILURE:
                status = AppConstants.ORDER_RECORD_STATUS_FAILURE_TEXT;
                break;
            case AppConstants.ORDER_RECORD_STATUS_OVERTIME:
                status = AppConstants.ORDER_RECORD_STATUS_OVERTIME_TEXT;
                break;
            case AppConstants.ORDER_RECORD_STATUS_REJECT:
                status = AppConstants.ORDER_RECORD_STATUS_REJECT_TEXT;
                break;
            case AppConstants.ORDER_RECORD_STATUS_SUBMIT:
                status = AppConstants.ORDER_RECORD_STATUS_SUBMIT_TEXT;
                break;
            default:
                break;
        }
        if (!TextUtils.isEmpty(status)) {
            mPos.printText("订单状态: ", status);
        }
        if (!TextUtils.isEmpty(info.receiverName)) {
            mPos.printText("接单员: ", info.receiverName);
        }
        mPos.printText("收银员: ", AccountManager.getInstance().getUser().userName);
        mPos.printEnd();
    }

    // 打印在线买单小票 带二维码
    public void print(OnlinePayInfo info, boolean retry) {
        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("(结账单)");
        if (retry) {
            mPos.printCenter("--重打小票--");
        }

        mPos.printDivide();
        mPos.printText("消费", Utils.moneyToStringEx(info.originalAmount) + " 元");
        mPos.printText("减免", Utils.moneyToStringEx(info.originalAmount - info.payAmount) + " 元");
        mPos.printDivide();
        mPos.printRight("实收 " + Utils.moneyToStringEx(info.payAmount) + " 元");
        mPos.printDivide();
        mPos.printText("交易号: ", info.payId);
        mPos.printText("付款方式: ", "小摩豆在线买单");
        if (!TextUtils.isEmpty(info.techName)) {
            mPos.printText("技师: ", TextUtils.isEmpty(info.techNo) ? info.techName : String.format("%s[%s]", info.techName, info.techNo));
        }
        if (!TextUtils.isEmpty(info.otherTechNames)) {
            String[] otherNames = info.otherTechNames.split(",|，");
            for (String name : otherNames) {
                mPos.printRight(name);
            }
        }
        mPos.printText("交易时间: ", info.createTime);
        mPos.printText("打印时间: ", Utils.getFormatString(new Date(), AppConstants.DEFAULT_DATE_FORMAT));
        String status = null;
        switch (info.status) {
            case AppConstants.ONLINE_PAY_STATUS_PAID:
                status = AppConstants.ONLINE_PAY_STATUS_PAID_TEXT;
                break;
            case AppConstants.ONLINE_PAY_STATUS_PASS:
                status = AppConstants.ONLINE_PAY_STATUS_PASS_TEXT;
                break;
            case AppConstants.ONLINE_PAY_STATUS_UNPASS:
                status = AppConstants.ONLINE_PAY_STATUS_UNPASS_TEXT;
                break;
            default:
                break;
        }
        if (!TextUtils.isEmpty(status)) {
            mPos.printText("买单状态: ", status);
        }
        mPos.printText("操作人: ", TextUtils.isEmpty(info.operatorName) ? AccountManager.getInstance().getUser().userName : info.operatorName);
        mPos.printBitmap(TradeManager.getInstance().getClubQRCodeSync());
        mPos.printCenter("--- 微信扫码，选技师，享优惠 ---");
        mPos.printEnd();
    }
}