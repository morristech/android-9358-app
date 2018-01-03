package com.xmd.cashier.manager;

import android.content.Intent;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.dal.bean.OrderRecordInfo;
import com.xmd.cashier.dal.bean.PayRecordInfo;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.OnlinePayListResult;
import com.xmd.cashier.dal.net.response.OrderRecordListResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.ServerException;
import com.xmd.m.network.XmdNetwork;

import java.io.Serializable;
import java.util.Date;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

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

    public void refreshOrderRecordNotify() {
        XLogger.i(TAG, "获取未处理预约订单");
        Observable<OrderRecordListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getOrderRecordList(AccountManager.getInstance().getToken(), String.valueOf(AppConstants.APP_LIST_DEFAULT_PAGE), String.valueOf(Integer.MAX_VALUE), null, AppConstants.ORDER_RECORD_STATUS_SUBMIT);
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OrderRecordListResult>() {
            @Override
            public void onCallbackSuccess(OrderRecordListResult result) {
                // 成功:判断列表,显示通知
                if (result != null && result.getRespData() != null && result.getRespData().size() > 0) {
                    XLogger.i(TAG, "获取未处理预约订单---成功:" + result.getRespData().size());
                    SPManager.getInstance().setOrderPushTag(result.getRespData().size());
                    for (OrderRecordInfo info : result.getRespData()) {
                        info.tempNo = result.getRespData().indexOf(info) + 1;
                    }
                    Intent intent = new Intent();
                    intent.setAction(AppConstants.ACTION_CUSTOM_NOTIFY_RECEIVER);
                    intent.putExtra(AppConstants.EXTRA_NOTIFY_TYPE, AppConstants.EXTRA_NOTIFY_TYPE_ORDER_RECORD);
                    intent.putExtra(AppConstants.EXTRA_NOTIFY_DATA, (Serializable) result.getRespData());
                    MainApplication.getInstance().getApplicationContext().sendBroadcast(intent);
                } else {
                    SPManager.getInstance().setOrderPushTag(0);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                e.printStackTrace();
                XLogger.e(TAG, "获取未处理预约订单---失败:" + e.getLocalizedMessage());
            }
        });
    }

    public void refreshOnlinePayNotify() {
        XLogger.i(TAG, "获取未处理在线买单");
        Observable<OnlinePayListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getOnlinePayList(AccountManager.getInstance().getToken(), String.valueOf(AppConstants.APP_LIST_DEFAULT_PAGE), String.valueOf(Integer.MAX_VALUE), AppConstants.APP_REQUEST_YES, null, AppConstants.ONLINE_PAY_STATUS_PAID);
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OnlinePayListResult>() {
            @Override
            public void onCallbackSuccess(OnlinePayListResult result) {
                //成功:判断列表,显示通知
                if (result != null && result.getRespData() != null && result.getRespData().size() > 0) {
                    XLogger.i(TAG, "获取未处理在线买单---成功:" + result.getRespData().size());
                    SPManager.getInstance().setFastPayPushTag(result.getRespData().size());
                    for (OnlinePayInfo info : result.getRespData()) {
                        info.tempNo = result.getRespData().indexOf(info) + 1;
                    }
                    Intent intent = new Intent();
                    intent.setAction(AppConstants.ACTION_CUSTOM_NOTIFY_RECEIVER);
                    intent.putExtra(AppConstants.EXTRA_NOTIFY_TYPE, AppConstants.EXTRA_NOTIFY_TYPE_ONLINE_PAY);
                    intent.putExtra(AppConstants.EXTRA_NOTIFY_DATA, (Serializable) result.getRespData());
                    MainApplication.getInstance().getApplicationContext().sendBroadcast(intent);
                } else {
                    SPManager.getInstance().setFastPayPushTag(0);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                e.printStackTrace();
                XLogger.e(TAG, "获取未处理在线买单---失败:" + e.getLocalizedMessage());
            }
        });
    }

    // 打印预约订单小票
    public void printOrderRecord(OrderRecordInfo info, boolean retry) {
        XLogger.i(TAG, "printOrderRecord");
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

    // 打印在线买单记录
    public void printOnlinePayRecord(OnlinePayInfo info, boolean retry, boolean keep) {
        XLogger.i(TAG, "printOnlinePayRecord");
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter((keep ? "商户存根" : "客户联") + (retry ? "(补打小票)" : ""));
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
        mPos.printText("手机号：", (keep ? info.telephone : Utils.formatPhone(info.telephone)) + "(" + Utils.formatName(info.userName, keep) + ")");
        mPos.printDivide();
        mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(info.originalAmount));
        if (info.orderDiscountList != null && !info.orderDiscountList.isEmpty()) {
            for (OnlinePayInfo.OnlinePayDiscountInfo discountInfo : info.orderDiscountList) {
                if (discountInfo.type != null) {
                    switch (discountInfo.type) {
                        case AppConstants.PAY_DISCOUNT_COUPON:
                            mPos.printText("用券抵扣：", "-￥ " + Utils.moneyToStringEx(discountInfo.amount));
                            break;
                        case AppConstants.PAY_DISCOUNT_ORDER:
                            mPos.printText("预约抵扣：", "-￥ " + Utils.moneyToStringEx(discountInfo.amount));
                            break;
                        case AppConstants.PAY_DISCOUNT_MEMBER:
                            mPos.printText("会员优惠：", "-￥ " + Utils.moneyToStringEx(discountInfo.amount));
                            break;
                        case AppConstants.PAY_DISCOUNT_REDUCTION:
                            mPos.printText("直接减免：", "-￥ " + Utils.moneyToStringEx(discountInfo.amount));
                            break;
                        default:
                            mPos.printText("其他抵扣：", "-￥ " + Utils.moneyToStringEx(discountInfo.amount));
                            break;
                    }
                }
            }
        } else {
            mPos.printText("优惠金额：", "-￥ " + Utils.moneyToStringEx(info.originalAmount - info.payAmount));
        }
        mPos.printDivide();
        mPos.printRight("实收金额：" + Utils.moneyToStringEx(info.payAmount) + " 元", true);
        mPos.printDivide();

        mPos.printText("交易号：", info.payId);
        mPos.printText("交易时间：", info.createTime);
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
            mPos.printText("交易状态：", status);
        }

        if (info.payRecordList != null && !info.payRecordList.isEmpty() && info.payRecordList.size() > 1) {
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
        } else {
            if (AppConstants.PAY_CHANNEL_ACCOUNT.equals(info.payChannel)) {
                //会员
                mPos.printText("支付方式：", Utils.getPayChannel(info.payChannel) + (TextUtils.isEmpty(info.platform) ? "" : "(" + Utils.getPlatform(info.platform) + ")"));
            } else {
                mPos.printText("支付方式：", (TextUtils.isEmpty(info.payChannelName) ? Utils.getPayChannel(info.payChannel) : info.payChannelName) + (TextUtils.isEmpty(info.qrType) ? "" : "(" + Utils.getQRPlatform(info.qrType) + ")"));
            }

            if (!TextUtils.isEmpty(info.techName)) {
                mPos.printText("服务技师：", (TextUtils.isEmpty(info.techNo) ? info.techName : String.format("%s[%s]", info.techName, info.techNo)) + (TextUtils.isEmpty(info.otherTechNames) ? "" : "，" + info.otherTechNames));
            }
            mPos.printText("收款人员：", (TextUtils.isEmpty(info.operatorName) ? (AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")") : info.operatorName));
        }

        mPos.printText("打印时间：", DateUtils.doDate2String(new Date()));
        if (!keep) {
            byte[] qrCodeBytes = TradeManager.getInstance().getClubQRCodeSync();
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
                mPos.printCenter("微信扫码，选技师、抢优惠");
            }
        }
        mPos.printEnd();
    }

    /***************************买单****************************/
    private Call<OnlinePayListResult> callOnlinePay;
    private RetryPool.RetryRunnable mRetryGetFastPayCount;
    private boolean resultFastPay;

    public void startGetFastPayCountAsync() {
        mRetryGetFastPayCount = new RetryPool.RetryRunnable(3000, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return getFastPayCount();
            }
        });
        RetryPool.getInstance().postWork(mRetryGetFastPayCount);
    }

    public void stopGetFastPayCountAsync() {
        if (callOnlinePay != null && !callOnlinePay.isCanceled()) {
            callOnlinePay.cancel();
        }
        if (mRetryGetFastPayCount != null) {
            RetryPool.getInstance().removeWork(mRetryGetFastPayCount);
            mRetryGetFastPayCount = null;
        }
    }

    private boolean getFastPayCount() {
        XLogger.i(TAG, "获取在线买单提醒数量");
        callOnlinePay = XmdNetwork.getInstance().getService(SpaService.class)
                .getOnlinePayCount(AccountManager.getInstance().getToken(), String.valueOf(AppConstants.APP_LIST_DEFAULT_PAGE), String.valueOf(Integer.MAX_VALUE), AppConstants.APP_REQUEST_YES, null, AppConstants.ONLINE_PAY_STATUS_PAID);
        XmdNetwork.getInstance().requestSync(callOnlinePay, new NetworkSubscriber<OnlinePayListResult>() {
            @Override
            public void onCallbackSuccess(OnlinePayListResult result) {
                //成功:判断列表,显示通知
                if (result != null && result.getRespData() != null && result.getRespData().size() > 0) {
                    SPManager.getInstance().setFastPayPushTag(result.getRespData().size());
                } else {
                    SPManager.getInstance().setFastPayPushTag(0);
                }
                resultFastPay = true;
            }

            @Override
            public void onCallbackError(Throwable e) {
                if (e instanceof ServerException && ((ServerException) e).statusCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                    // token过期
                    resultFastPay = true;
                } else {
                    resultFastPay = false;
                }
            }
        });
        return resultFastPay;
    }

    /***************************订单****************************/
    private boolean resultOrder;
    private RetryPool.RetryRunnable mRetryGetOrderCount;
    private Call<OrderRecordListResult> callOrderRecord;

    public void startGetOrderCountAsync() {
        mRetryGetOrderCount = new RetryPool.RetryRunnable(3000, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return getOrderCount();
            }
        });
        RetryPool.getInstance().postWork(mRetryGetOrderCount);
    }

    public void stopGetOrderCountAsync() {
        if (callOrderRecord != null && !callOrderRecord.isCanceled()) {
            callOrderRecord.cancel();
        }
        if (mRetryGetOrderCount != null) {
            RetryPool.getInstance().removeWork(mRetryGetOrderCount);
            mRetryGetOrderCount = null;
        }
    }

    private boolean getOrderCount() {
        XLogger.i(TAG, "获取预约订单提醒数量");
        callOrderRecord = XmdNetwork.getInstance().getService(SpaService.class)
                .getOrderRecordCount(AccountManager.getInstance().getToken(), String.valueOf(AppConstants.APP_LIST_DEFAULT_PAGE), String.valueOf(Integer.MAX_VALUE), null, AppConstants.ORDER_RECORD_STATUS_SUBMIT);
        XmdNetwork.getInstance().requestSync(callOrderRecord, new NetworkSubscriber<OrderRecordListResult>() {
            @Override
            public void onCallbackSuccess(OrderRecordListResult result) {
                // 成功:判断列表,显示通知
                if (result != null && result.getRespData() != null && result.getRespData().size() > 0) {
                    SPManager.getInstance().setOrderPushTag(result.getRespData().size());
                } else {
                    SPManager.getInstance().setOrderPushTag(0);
                }
                resultOrder = true;
            }

            @Override
            public void onCallbackError(Throwable e) {
                if (e instanceof ServerException && ((ServerException) e).statusCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                    // token过期
                    resultOrder = true;
                } else {
                    resultOrder = false;
                }
            }
        });
        return resultOrder;
    }
}
