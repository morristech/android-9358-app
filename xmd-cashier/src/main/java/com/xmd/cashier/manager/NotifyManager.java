package com.xmd.cashier.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.InnerEmployeeInfo;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.InnerOrderItemInfo;
import com.xmd.cashier.dal.bean.OrderRecordInfo;
import com.xmd.cashier.dal.bean.PayRecordInfo;
import com.xmd.cashier.dal.bean.TradeDiscountInfo;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.OnlinePayListResult;
import com.xmd.cashier.dal.net.response.OrderRecordListResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.service.CustomService;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-4-11.
 * 订单预约 && 在线买单
 */

public class NotifyManager {
    private static final String TAG = "NotifyManager";
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
        Observable<OrderRecordListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getOrderRecordList(AccountManager.getInstance().getToken(), String.valueOf(page), String.valueOf(AppConstants.APP_LIST_PAGE_SIZE), search, status);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OrderRecordListResult>() {
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
    public Subscription acceptOrder(String orderId, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .updateOrderRecordStatus(AccountManager.getInstance().getToken(), AppConstants.SESSION_TYPE, AppConstants.ORDER_RECORD_STATUS_ACCEPT, orderId);
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

    // 拒绝订单
    public Subscription rejectOrder(String orderId, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .updateOrderRecordStatus(AccountManager.getInstance().getToken(), AppConstants.SESSION_TYPE, AppConstants.ORDER_RECORD_STATUS_REJECT, orderId);
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

    // 在线买单列表
    public Subscription getOnlinePayList(int page, String search, String status, final Callback<OnlinePayListResult> callback) {
        Observable<OnlinePayListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getOnlinePayList(AccountManager.getInstance().getToken(), String.valueOf(page), String.valueOf(AppConstants.APP_LIST_PAGE_SIZE), AppConstants.APP_REQUEST_YES, search, status);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OnlinePayListResult>() {
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
    public Subscription passOnlinePay(String orderId, String status, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .updateOnlinePayStatus(AccountManager.getInstance().getToken(), orderId, status);
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

    // 请到前台
    public Subscription unPassOnlinePay(String orderId, String status, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .updateOnlinePayStatus(AccountManager.getInstance().getToken(), orderId, status);
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

    // *****************************************未处理买单*****************************************
    public void notifyOnlinePayList() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "获取未处理在线买单：" + RequestConstant.URL_GET_ONLINE_PAY_LIST);
        Observable<OnlinePayListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getOnlinePayList(AccountManager.getInstance().getToken(), String.valueOf(AppConstants.APP_LIST_DEFAULT_PAGE), String.valueOf(Integer.MAX_VALUE), AppConstants.APP_REQUEST_YES, null, AppConstants.ONLINE_PAY_STATUS_PAID);
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OnlinePayListResult>() {
            @Override
            public void onCallbackSuccess(OnlinePayListResult result) {
                if (result != null && result.getRespData() != null) {
                    if (result.getRespData().size() > 0) {
                        int size = result.getRespData().size();
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "获取未处理在线买单---成功:" + size);
                        SPManager.getInstance().setFastPayPushTag(size);
                        for (TradeRecordInfo info : result.getRespData()) {
                            info.tempNo = result.getRespData().indexOf(info) + 1;
                        }
                        EventBus.getDefault().post(result);
                    } else {
                        SPManager.getInstance().setFastPayPushTag(0);
                    }
                } else {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "获取未处理在线买单---成功: 数据null");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                e.printStackTrace();
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "获取未处理在线买单---失败:" + e.getLocalizedMessage());
            }
        });
    }

    // *****************************************未处理预约*****************************************
    public void notifyOrderRecordList() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "获取未处理预约订单：" + RequestConstant.URL_GET_ORDER_RECORD_LIST);
        Observable<OrderRecordListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getOrderRecordList(AccountManager.getInstance().getToken(), String.valueOf(AppConstants.APP_LIST_DEFAULT_PAGE), String.valueOf(Integer.MAX_VALUE), null, AppConstants.ORDER_RECORD_STATUS_SUBMIT);
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OrderRecordListResult>() {
            @Override
            public void onCallbackSuccess(OrderRecordListResult result) {
                if (result != null && result.getRespData() != null) {
                    if (result.getRespData().size() > 0) {
                        int size = result.getRespData().size();
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "获取未处理预约订单---成功:" + size);
                        SPManager.getInstance().setOrderPushTag(size);
                        for (OrderRecordInfo info : result.getRespData()) {
                            info.tempNo = result.getRespData().indexOf(info) + 1;
                        }
                        EventBus.getDefault().post(result);
                    } else {
                        SPManager.getInstance().setOrderPushTag(0);
                    }
                } else {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "获取未处理预约订单---成功: 数据null");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                e.printStackTrace();
                XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "获取未处理预约订单---失败:" + e.getLocalizedMessage());
            }
        });
    }

    // ********************************** 打印预约订单小票 ***********************************
    public void printOrderRecordAsync(final OrderRecordInfo orderRecordInfo, final boolean retry) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        printOrderRecord(orderRecordInfo, retry);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void printOrderRecord(OrderRecordInfo info, boolean retry) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "打印预约订单记录");
        mPos.setPrintListener();
        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("(预约订单)");
        if (retry) {
            mPos.printCenter("--补打小票--");
        }
        mPos.printDivide();
        mPos.printText("客户: ", (TextUtils.isEmpty(info.phoneNum) ? info.customerName : String.format("%s(%s)", info.customerName, info.phoneNum)));
        mPos.printText("技师: ", TextUtils.isEmpty(info.techSerialNo) ? (TextUtils.isEmpty(info.techName) ? "未指定" : info.techName) : String.format("%s[%s]", info.techName, info.techSerialNo));
        mPos.printText("项目: ", TextUtils.isEmpty(info.itemName) ? "到店选择" : info.itemName);
        mPos.printText("到店: ", info.appointTime);
        mPos.printDivide();
        mPos.printRight("已付: " + info.downPayment + "元", true);
        mPos.printDivide();
        mPos.printText("订单编号: ", info.id);
        mPos.printText("下单时间: ", info.createdAt);
        mPos.printText("打印时间: ", DateUtils.doDate2String(new Date()));
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
        mPos.printText("收银员: ", AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")");
        mPos.printEnd();
    }

    // ******************************  打印在线买单记录  ********************************
    public void printOnlinePayRecordAsync(final TradeRecordInfo onlinePayInfo, final boolean retry) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        printOnlinePayRecord(onlinePayInfo, retry, true);
                        if (SPManager.getInstance().getPrintClientSwitch()) {
                            printOnlinePayRecord(onlinePayInfo, retry, false);
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void printOnlinePayRecordAsync(final TradeRecordInfo onlinePayInfo, final boolean retry, final boolean keep) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        printOnlinePayRecord(onlinePayInfo, retry, keep);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }


    public void printOnlinePayRecord(TradeRecordInfo info, boolean retry, boolean keep) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "打印在线买单记录");
        mPos.setPrintListener();
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter((keep ? "商户存根" : "客户联") + (retry ? "(补打小票)" : ""));
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();

        if (!TextUtils.isEmpty(info.telephone)) {
            mPos.printText("手机号：", (keep ? info.telephone : Utils.formatPhone(info.telephone)) + (TextUtils.isEmpty(info.userName) ? "" : "(" + Utils.formatName(info.userName, keep) + ")"));
            mPos.printDivide();
        }

        mPos.printText("消费详情", Utils.moneyToStringEx(info.originalAmount), true);
        if (info.details != null && !info.details.isEmpty()) {
            for (InnerOrderInfo orderInfo : info.details) {
                mPos.printText("[" + orderInfo.roomName + " " + orderInfo.roomTypeName + "]" + orderInfo.userIdentify + "手牌", Utils.moneyToStringEx(orderInfo.amount));
                for (InnerOrderItemInfo orderItemInfo : orderInfo.itemList) {
                    mPos.printText("  " + orderItemInfo.itemName + " * " + orderItemInfo.itemCount, Utils.moneyToString(orderItemInfo.itemAmount) + "元" + "/" + orderItemInfo.itemUnit);
                    switch (orderItemInfo.itemType) {
                        case AppConstants.INNER_ORDER_ITEM_TYPE_SPA:
                            if (orderItemInfo.employeeList != null && !orderItemInfo.employeeList.isEmpty()) {
                                for (InnerEmployeeInfo employeeInfo : orderItemInfo.employeeList) {
                                    mPos.printText("  |--[" + employeeInfo.employeeNo + "]   " + employeeInfo.bellName);
                                }
                            }
                            break;
                        case AppConstants.INNER_ORDER_ITEM_TYPE_GOODS:
                        default:
                            break;
                    }
                }
            }
        }

        int paidOrderAmount = 0;
        int paidCouponAmount = 0;
        int paidMemberAmount = 0;
        int paidReductionAmount = 0;
        if (info.orderDiscountList != null && !info.orderDiscountList.isEmpty()) {
            for (TradeDiscountInfo discountInfo : info.orderDiscountList) {
                switch (discountInfo.type) {
                    case AppConstants.PAY_DISCOUNT_COUPON:
                        paidCouponAmount += discountInfo.amount;
                        break;
                    case AppConstants.PAY_DISCOUNT_MEMBER:
                        paidMemberAmount += discountInfo.amount;
                        break;
                    case AppConstants.PAY_DISCOUNT_ORDER:
                        paidOrderAmount += discountInfo.amount;
                        break;
                    case AppConstants.PAY_DISCOUNT_REDUCTION:
                        paidReductionAmount += discountInfo.amount;
                        break;
                    default:
                        break;
                }
            }
        }
        mPos.printDivide();
        mPos.printText("订单金额：", "￥" + Utils.moneyToStringEx(info.originalAmount));
        mPos.printText("优惠减免：", "￥" + Utils.moneyToStringEx(paidOrderAmount + paidMemberAmount + paidCouponAmount + paidReductionAmount));
        mPos.printText("|--预约抵扣：", "-￥" + Utils.moneyToStringEx(paidOrderAmount));
        mPos.printText("|--用券抵扣：", "-￥" + Utils.moneyToStringEx(paidCouponAmount));
        mPos.printText("|--会员优惠：", "-￥" + Utils.moneyToStringEx(paidMemberAmount));
        mPos.printText("|--直接减免：", "-￥" + Utils.moneyToStringEx(paidReductionAmount));
        mPos.printDivide();
        mPos.printRight("实收金额：" + Utils.moneyToStringEx(info.payAmount) + "元", true);
        if (info.payRecordList != null && !info.payRecordList.isEmpty()) {
            for (PayRecordInfo payRecordInfo : info.payRecordList) {
                if (AppConstants.PAY_CHANNEL_ACCOUNT.equals(payRecordInfo.payChannel)) {    //会员消费
                    mPos.printText("  |--" + payRecordInfo.payChannelName + "：", "￥" + Utils.moneyToStringEx(payRecordInfo.amount) + "(会员卡余额：￥" + Utils.moneyToStringEx(payRecordInfo.accountAmount) + ")");
                } else {
                    mPos.printText("  |--" + payRecordInfo.payChannelName + "：", "￥" + Utils.moneyToStringEx(payRecordInfo.amount));
                }
            }
        }
        mPos.printDivide();

        mPos.printText("交易号：", info.payId);
        if (!TextUtils.isEmpty(info.techName) || !TextUtils.isEmpty(info.techNo)) {
            mPos.printText("服务技师：", (TextUtils.isEmpty(info.techName) ? "" : info.techName) + (TextUtils.isEmpty(info.techNo) ? "" : "[" + info.techNo + "]") + (TextUtils.isEmpty(info.otherTechNames) ? "" : "，" + info.otherTechNames));
        } else {
            if (!TextUtils.isEmpty(info.otherTechNames)) {
                mPos.printText("服务技师：", info.otherTechNames);
            }
        }
        mPos.printText("打印时间：", DateUtils.doDate2String(new Date()));

        if (info.payRecordList != null && !info.payRecordList.isEmpty()) {
            for (PayRecordInfo payRecordInfo : info.payRecordList) {
                mPos.printDivide();
                if (!TextUtils.isEmpty(payRecordInfo.tradeNo)) {
                    mPos.printText("流水号：", payRecordInfo.tradeNo);
                }
                mPos.printText("支付方式：", payRecordInfo.payChannelName);
                mPos.printText("支付金额：", "￥" + Utils.moneyToStringEx(payRecordInfo.amount));
                mPos.printText("支付时间：", payRecordInfo.payTime);
                if (!TextUtils.isEmpty(payRecordInfo.operatorName)) {
                    mPos.printText("收款人员：", payRecordInfo.operatorName);
                }
            }
        }

        if (!keep) {
            byte[] qrCodeBytes = QrcodeManager.getInstance().getTradeQrcodeBytes(info);
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
                mPos.printCenter("微信扫码，选技师、抢优惠");
            }
        }
        mPos.printEnd();
    }

    public void startRepeatOnlinePay(long triggerTime) {
        Context context = MainApplication.getInstance().getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CustomService.class);
        intent.setAction(CustomService.ACTION);
        intent.putExtra(AppConstants.EXTRA_CMD, CustomService.CMD_REFRESH_ONLINE_PAY_NOTIFY);
        PendingIntent pi = PendingIntent.getService(context, CustomService.CMD_REFRESH_ONLINE_PAY_NOTIFY, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Utils.isAboveKitkat()) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        } else {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        }
    }

    public void stopRepeatOnlinePay() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "结束在线买单轮询");
        Context context = MainApplication.getInstance().getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CustomService.class);
        intent.setAction(CustomService.ACTION);
        intent.putExtra(AppConstants.EXTRA_CMD, CustomService.CMD_REFRESH_ONLINE_PAY_NOTIFY);
        PendingIntent pi = PendingIntent.getService(context, CustomService.CMD_REFRESH_ONLINE_PAY_NOTIFY, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pi);
    }

    public void startRepeatOrderRecord(long triggerTime) {
        Context context = MainApplication.getInstance().getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CustomService.class);
        intent.setAction(CustomService.ACTION);
        intent.putExtra(AppConstants.EXTRA_CMD, CustomService.CMD_REFRESH_ORDER_RECORD_NOTIFY);
        PendingIntent pi = PendingIntent.getService(context, CustomService.CMD_REFRESH_ORDER_RECORD_NOTIFY, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Utils.isAboveKitkat()) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        } else {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        }
    }

    public void stopRepeatOrderRecord() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "结束预约订单轮询");
        Context context = MainApplication.getInstance().getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CustomService.class);
        intent.setAction(CustomService.ACTION);
        intent.putExtra(AppConstants.EXTRA_CMD, CustomService.CMD_REFRESH_ORDER_RECORD_NOTIFY);
        PendingIntent pi = PendingIntent.getService(context, CustomService.CMD_REFRESH_ORDER_RECORD_NOTIFY, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pi);
    }
}
