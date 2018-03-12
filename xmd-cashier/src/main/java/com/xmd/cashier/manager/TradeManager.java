package com.xmd.cashier.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.google.zxing.WriterException;
import com.google.zxing.client.android.MyQrEncoder;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.LocalPersistenceManager;
import com.xmd.cashier.dal.bean.ClubQrcodeBytes;
import com.xmd.cashier.dal.bean.InnerEmployeeInfo;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.InnerOrderItemInfo;
import com.xmd.cashier.dal.bean.PayRecordInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.bean.TradeBatchInfo;
import com.xmd.cashier.dal.bean.TradeChannelInfo;
import com.xmd.cashier.dal.bean.TradeDiscountCheckInfo;
import com.xmd.cashier.dal.bean.TradeDiscountInfo;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.CheckInfoListResult;
import com.xmd.cashier.dal.net.response.CommonVerifyResult;
import com.xmd.cashier.dal.net.response.CouponResult;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.OrderResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.net.response.TradeBatchHoleResult;
import com.xmd.cashier.dal.net.response.TradeBatchResult;
import com.xmd.cashier.dal.net.response.TradeChannelListResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-5-26.
 */

public class TradeManager {
    private static final String TAG = "TradeManager";
    private Trade mTrade;
    private IPos mPos;
    private static TradeManager mInstance = new TradeManager();

    public static TradeManager getInstance() {
        return mInstance;
    }

    private TradeManager() {
        mPos = PosFactory.getCurrentCashier();
        newTrade();
    }

    public void newTrade() {
        mTrade = new Trade();
    }

    // 当前交易
    public Trade getCurrentTrade() {
        return mTrade;
    }

    public void initTradeByRecord(TradeRecordInfo recordInfo) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "===== initTradeByRecord =====");
        newTrade();
        mTrade.innerRecordInfo = recordInfo;
        mTrade.batchNo = recordInfo.batchNo;
        mTrade.payOrderId = recordInfo.payId;
        mTrade.setAlreadyDiscountMoney(getVerifiedAmount(recordInfo));   // 已核销
        mTrade.setAlreadyCutMoney(recordInfo.originalAmount - recordInfo.payAmount); // 所有已减免
        mTrade.setAlreadyPayMoney(recordInfo.paidAmount);    // 已付
        mTrade.setWillReductionMoney(getReductionAmount(recordInfo));    // 直接减免
        formatVerifiedList(recordInfo.orderDiscountList);
        mTrade.setVerifiedList(recordInfo.orderDiscountList);
        mTrade.setOriginMoney(recordInfo.originalAmount);    // 订单金额
        mTrade.setWillPayMoney(recordInfo.payAmount);        // 实付金额
    }

    public void initTradeBySelect() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "===== initTradeBySelect =====");
        newTrade();
        mTrade.setOriginMoney(InnerManager.getInstance().getOrderAmount());
    }

    private void formatVerifiedList(List<TradeDiscountInfo> list) {
        Iterator<TradeDiscountInfo> iterator = list.iterator();
        while (iterator.hasNext()) {
            TradeDiscountInfo orderDiscountInfo = iterator.next();
            if (AppConstants.PAY_DISCOUNT_MEMBER.equals(orderDiscountInfo.type) || AppConstants.PAY_DISCOUNT_REDUCTION.equals(orderDiscountInfo.type)) {
                iterator.remove();
            }
        }
    }

    private int getVerifiedAmount(TradeRecordInfo innerRecordInfo) {
        int amount = 0;
        if (innerRecordInfo.orderDiscountList != null && !innerRecordInfo.orderDiscountList.isEmpty()) {
            for (TradeDiscountInfo orderDiscountInfo : innerRecordInfo.orderDiscountList) {
                switch (orderDiscountInfo.type) {
                    case AppConstants.PAY_DISCOUNT_COUPON:
                    case AppConstants.PAY_DISCOUNT_ORDER:
                        amount += orderDiscountInfo.amount;
                        break;
                    default:
                        break;
                }
            }
        }
        return amount;
    }

    private int getReductionAmount(TradeRecordInfo innerRecordInfo) {
        int amount = 0;
        if (innerRecordInfo.orderDiscountList != null && !innerRecordInfo.orderDiscountList.isEmpty()) {
            for (TradeDiscountInfo orderDiscountInfo : innerRecordInfo.orderDiscountList) {
                if (AppConstants.PAY_DISCOUNT_REDUCTION.equals(orderDiscountInfo.type)) {
                    amount += orderDiscountInfo.amount;
                }
            }
        }
        return amount;
    }

    /*********************************************核销相关******************************************/
    // 判断输入核销码类型: 同VerifyManager
    // 根据手机号获取可核销的 券+付费预约
    public Subscription getVerifyList(String phone, final Callback<CheckInfoListResult> callback) {
        Observable<CheckInfoListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getCheckInfoList(phone, AccountManager.getInstance().getToken());
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<CheckInfoListResult>() {
            @Override
            public void onCallbackSuccess(CheckInfoListResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 根据核销码获取券信息
    public Subscription getVerifyCoupon(String couponNo, final Callback<CouponResult> callback) {
        Observable<CouponResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getCouponInfo(AccountManager.getInstance().getToken(), couponNo);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<CouponResult>() {
            @Override
            public void onCallbackSuccess(CouponResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 根据核销码获取预约信息
    public Subscription getVerifyOrder(String orderNo, final Callback<OrderResult> callback) {
        Observable<OrderResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getPaidOrderInfo(AccountManager.getInstance().getToken(), orderNo);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OrderResult>() {
            @Override
            public void onCallbackSuccess(OrderResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 根据核销码获取请客信息
    public Subscription getVerifyTreat(String treatNo, final Callback<CommonVerifyResult> callback) {
        Observable<CommonVerifyResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getCommonVerifyInfo(AccountManager.getInstance().getToken(), treatNo, AppConstants.TYPE_PAY_FOR_OTHER);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<CommonVerifyResult>() {
            @Override
            public void onCallbackSuccess(CommonVerifyResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 根据核销码获取项目券信息
    public Subscription getVerifyServiceItem(String couponNo, final Callback<CouponResult> callback) {
        Observable<CouponResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getServiceCouponInfo(AccountManager.getInstance().getToken(), couponNo);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<CouponResult>() {
            @Override
            public void onCallbackSuccess(CouponResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    /********************************************其他功能********************************************/
    public List<TradeDiscountInfo> getVerifiedList() {
        return mTrade.getVerifiedList();
    }

    // 核销列表
    public List<VerificationItem> getVerificationList() {
        return mTrade.getCouponList();
    }

    // 清空核销列表
    public void cleanVerificationList() {
        mTrade.cleanCouponList();
    }

    // 添加优惠券
    public void addVerificationInfo(VerificationItem verificationItem) {
        List<VerificationItem> verificationItems = mTrade.getCouponList();
        if (!verificationItems.contains(verificationItem)) {
            switch (verificationItem.type) {
                case AppConstants.TYPE_PAY_FOR_OTHER:
                    verificationItems.add(0, verificationItem);
                    break;
                default:
                    verificationItems.add(verificationItem);
                    break;
            }
        }
    }

    // 根据code判断列表中是否存在
    public VerificationItem getVerificationById(String id) {
        for (VerificationItem v : mTrade.getCouponList()) {
            if (v != null && v.code.equals(id)) {
                return v;
            }
        }
        return null;
    }

    // 设置选中的折扣券的消费金额
    public void setDiscountOriginAmount() {
        for (VerificationItem v : mTrade.getCouponList()) {
            if (v.selected && !v.success && AppConstants.TYPE_DISCOUNT_COUPON.equals(v.type)) {
                v.couponInfo.originAmount = mTrade.getOriginMoney();
            }
        }
    }

    // 设置某个核销项的选中状态
    public void setVerificationSelectedStatus(VerificationItem v, boolean selected) {
        int index = mTrade.getCouponList().indexOf(v);
        if (index >= 0) {
            VerificationItem verificationItem = mTrade.getCouponList().get(index);//查找到指定项
            verificationItem.selected = selected;
            // 处理折扣券
            if (AppConstants.TYPE_DISCOUNT_COUPON.equals(verificationItem.type)) {
                verificationItem.couponInfo.originAmount = selected ? mTrade.getOriginMoney() : 0;
            }
        }
    }

    public String formatVerifyCodes(List<VerificationItem> verifys) {
        StringBuilder result = new StringBuilder();
        for (VerificationItem item : verifys) {
            if (item.selected) {
                switch (item.type) {
                    case AppConstants.TYPE_COUPON:
                    case AppConstants.TYPE_CASH_COUPON:
                    case AppConstants.TYPE_PAID_COUPON:
                    case AppConstants.TYPE_DISCOUNT_COUPON:
                    case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                        result.append(item.couponInfo.couponNo + ",");
                        break;
                    case AppConstants.TYPE_ORDER:
                        result.append(item.order.orderNo + ",");
                        break;
                    case AppConstants.TYPE_PAY_FOR_OTHER:
                        result.append(item.treatInfo.authorizeCode + ",");
                        break;
                    default:
                        break;
                }
            }
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
            return result.toString();
        } else {
            return null;
        }
    }

    public int getDiscountAmount(List<VerificationItem> verifys) {
        int total = 0;
        for (VerificationItem info : verifys) {
            if (info.selected) {
                switch (info.type) {
                    case AppConstants.TYPE_COUPON:
                    case AppConstants.TYPE_CASH_COUPON:
                    case AppConstants.TYPE_PAID_COUPON:
                    case AppConstants.TYPE_DISCOUNT_COUPON:
                    case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                        total += info.couponInfo.getReallyCouponMoney();
                        break;
                    case AppConstants.TYPE_ORDER:
                        total += info.order.downPayment;
                        break;
                    case AppConstants.TYPE_PAY_FOR_OTHER:
                        total += info.treatInfo.amount;
                        break;
                }
            }
        }
        return total;
    }

    public int getDiscountCount(List<VerificationItem> verifys) {
        int count = 0;
        for (VerificationItem info : verifys) {
            if (info.selected) {
                count++;
            }
        }
        return count;
    }

    // ------------------------收银重构-------------------------
    // 获取会所设置的支付方式
    List<TradeChannelInfo> tradeChannelInfos = new ArrayList<>();

    public List<TradeChannelInfo> getTradeChannelInfos() {
        return tradeChannelInfos;
    }

    public List<String> getTradeChannelTexts() {
        List<String> texts = new ArrayList<>();
        for (TradeChannelInfo tradeChannelInfo : tradeChannelInfos) {
            texts.add(tradeChannelInfo.name);
        }
        return texts;
    }

    public void setCurrentChannel(String text) {
        for (TradeChannelInfo tradeChannelInfo : tradeChannelInfos) {
            if (text.equals(tradeChannelInfo.name)) {
                mTrade.currentChannelName = tradeChannelInfo.name;
                mTrade.currentChannelType = tradeChannelInfo.type;
                mTrade.currentChannelMark = tradeChannelInfo.mark;
            }
        }
    }

    public Subscription getPayChannelList(final Callback<TradeChannelListResult> callback) {
        tradeChannelInfos.clear();
        Observable<TradeChannelListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getPayChannelList(AccountManager.getInstance().getToken());
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<TradeChannelListResult>() {
            @Override
            public void onCallbackSuccess(TradeChannelListResult result) {
                if (result != null && result.getRespData() != null && !result.getRespData().isEmpty()) {
                    tradeChannelInfos.addAll(result.getRespData());

                    Iterator<TradeChannelInfo> iterator = tradeChannelInfos.iterator();
                    while (iterator.hasNext()) {
                        TradeChannelInfo current = iterator.next();
                        if (AppConstants.PAY_CHANNEL_WX.equals(current.type) || AppConstants.PAY_CHANNEL_ALI.equals(current.type)) {
                            iterator.remove();
                        }
                    }

                    TradeChannelInfo qrcodeChannel = new TradeChannelInfo();
                    qrcodeChannel.name = AppConstants.CASHIER_TYPE_QRCODE_TEXT;
                    qrcodeChannel.type = AppConstants.PAY_CHANNEL_QRCODE;
                    tradeChannelInfos.add(0, qrcodeChannel);

                    if (callback != null) {
                        callback.onSuccess(result);
                    }
                } else {
                    if (callback != null) {
                        callback.onError("会所未设置支付方式");
                    }
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                if (callback != null) {
                    callback.onError(e.getLocalizedMessage());
                }
            }
        });
    }

    // 生成交易流水号
    public Subscription getTradeNo(final Callback<GetTradeNoResult> callback) {
        Observable<GetTradeNoResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getTradeNo(AccountManager.getInstance().getToken(), RequestConstant.DEFAULT_SIGN_VALUE);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<GetTradeNoResult>() {
            @Override
            public void onCallbackSuccess(GetTradeNoResult result) {
                mTrade.tradeNo = result.getRespData();
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 收银台支付
    public void posPay(Context context, final Callback<Void> callback) {
        mTrade.newCashierTradeNo();
        CashierManager.getInstance().pay(context, mTrade.getPosTradeNo(), mTrade.getWillPayMoney(), new PayCallback<Object>() {
            @Override
            public void onResult(String error, Object o) {
                if (error == null) {
                    mTrade.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                    mTrade.posPayCashierNo = CashierManager.getInstance().getTradeNo(o);
                    callback.onSuccess(null);
                } else {
                    mTrade.tradeStatus = AppConstants.TRADE_STATUS_ERROR;
                    mTrade.tradeStatusError = error;
                    callback.onError(error);
                }
            }
        });
    }

    // 生成交易记录
    public Subscription generateBatchOrder(String batchNo, String memberId, String payChannel, String orderIds, String verifyCodes, String reductionAmount, String oriAmount, String realAmount, final Callback<String> callback) {
        Observable<TradeBatchResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .generateBatchOrder(AccountManager.getInstance().getToken(),
                        batchNo, memberId, orderIds, payChannel, verifyCodes, reductionAmount, oriAmount, realAmount);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<TradeBatchResult>() {
            @Override
            public void onCallbackSuccess(TradeBatchResult result) {
                if (result != null && result.getRespData() != null) {
                    TradeBatchInfo tradeBatchInfo = result.getRespData();
                    mTrade.batchNo = tradeBatchInfo.batchNo;
                    mTrade.payNo = tradeBatchInfo.payNo;
                    mTrade.payOrderId = tradeBatchInfo.payOrderId;
                    mTrade.payUrl = tradeBatchInfo.payUrl;
                    mTrade.setOriginMoney(tradeBatchInfo.oriAmount);
                    mTrade.setWillDiscountMoney(tradeBatchInfo.discountAmount);
                    mTrade.setWillPayMoney(tradeBatchInfo.payAmount);
                    callback.onSuccess(tradeBatchInfo.status);
                } else {
                    callback.onError("数据异常，请联系系统管理员");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 买单回调确认支付
    public Subscription callbackBatchOrder(final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .callbackBatchOrder(AccountManager.getInstance().getToken(),
                        mTrade.memberId, mTrade.currentChannelType, mTrade.payOrderId, mTrade.payNo, mTrade.tradeNo, String.valueOf(mTrade.getWillPayMoney()));
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

    // 获取订单详情
    public Subscription getHoleBatchDetail(String payOrderId, final Callback<TradeRecordInfo> callback) {
        Observable<TradeBatchHoleResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getHoleBatchDetail(AccountManager.getInstance().getToken(), payOrderId);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<TradeBatchHoleResult>() {
            @Override
            public void onCallbackSuccess(TradeBatchHoleResult result) {
                callback.onSuccess(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    public void printTradeRecordInfoAsync(final TradeRecordInfo innerRecordInfo, final boolean retry) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        printTradeRecordInfo(innerRecordInfo, retry, true);
                        if (SPManager.getInstance().getPrintClientSwitch()) {
                            printTradeRecordInfo(innerRecordInfo, retry, false);
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void printTradeRecordInfoAsync(final TradeRecordInfo innerRecordInfo, final boolean retry, final boolean keep) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        printTradeRecordInfo(innerRecordInfo, retry, keep);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void printTradeRecordInfo(TradeRecordInfo info, boolean retry, boolean keep) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "打印订单详情");
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter((keep ? "商户存根" : "客户联") + (retry ? "(补打小票)" : ""));
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
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

        if (keep) {
            mPos.printText("优惠详情", "-" + Utils.moneyToStringEx(paidOrderAmount + paidMemberAmount + paidCouponAmount + paidReductionAmount), true);
            if (info.orderDiscountList != null && !info.orderDiscountList.isEmpty()) {
                for (TradeDiscountInfo discountInfo : info.orderDiscountList) {
                    switch (discountInfo.type) {
                        case AppConstants.PAY_DISCOUNT_COUPON:
                        case AppConstants.PAY_DISCOUNT_MEMBER:
                        case AppConstants.PAY_DISCOUNT_ORDER:
                            TradeDiscountCheckInfo discountCheckInfo = discountInfo.checkInfo;
                            mPos.printText("[" + discountCheckInfo.typeName + "]" + discountCheckInfo.title, "(-" + Utils.moneyToStringEx(discountCheckInfo.amount) + ")");
                            switch (discountCheckInfo.type) {
                                case AppConstants.INNER_DISCOUNT_CHECK_CASH_COUPON:
                                case AppConstants.INNER_DISCOUNT_CHECK_DISCOUNT_COUPON:
                                case AppConstants.INNER_DISCOUNT_CHECK_COUPON:
                                case AppConstants.INNER_DISCOUNT_GIFT_COUPON:
                                    mPos.printText(discountCheckInfo.consumeDescription + "/" + discountCheckInfo.verifyCode + "/" + discountCheckInfo.telephone);
                                    break;
                                case AppConstants.INNER_DISCOUNT_SERVICE_ITEM_COUPON:
                                    mPos.printText(discountCheckInfo.sourceName + "/" + discountCheckInfo.verifyCode + "/" + discountCheckInfo.telephone);
                                    break;
                                case AppConstants.INNER_DISCOUNT_CONSUME:
                                    mPos.printText(discountCheckInfo.consumeDescription + "/" + discountCheckInfo.userName + "/" + discountCheckInfo.cardNo);
                                    break;
                                case AppConstants.INNER_DISCOUNT_PAID_ORDER:
                                    mPos.printText(discountCheckInfo.verifyCode + "/" + discountCheckInfo.telephone);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case AppConstants.PAY_DISCOUNT_REDUCTION:
                            mPos.printText("[直接减免]", "(-" + Utils.moneyToStringEx(discountInfo.amount) + ")");
                            break;
                    }
                }
            }
            mPos.printDivide();
            if (!TextUtils.isEmpty(info.description)) {
                mPos.printText("备注：" + info.description);
                mPos.printDivide();
            }
        }

        mPos.printText("交易号：", info.payId);
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
            byte[] qrCodeBytes = getTradeQrcodeBytes(info);
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
                mPos.printCenter("微信扫码，选技师、抢优惠");
            }
        }
        mPos.printEnd();
    }

    // 交易二维码
    private byte[] tradeQrcodeBytes;

    public byte[] getTradeQrcodeBytes(TradeRecordInfo tradeRecordInfo) {
        tradeQrcodeBytes = null;
        Call<StringResult> tradeCodeCall = XmdNetwork.getInstance().getService(SpaService.class)
                .getTradeQrcode(AccountManager.getInstance().getToken(), tradeRecordInfo.id, tradeRecordInfo.payChannel, RequestConstant.DEFAULT_SIGN_VALUE);
        XmdNetwork.getInstance().requestSync(tradeCodeCall, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                String content = result.getRespData();
                if (!TextUtils.isEmpty(content)) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes content:" + content);
                    try {
                        Bitmap bitmap = MyQrEncoder.encode(content, 240, 240);
                        if (bitmap != null) {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                                tradeQrcodeBytes = bos.toByteArray();
                            } else {
                                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes--bitmap compress failed");
                            }
                            bitmap.recycle();
                        } else {
                            XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes--Qrcode encode failed");
                        }
                    } catch (WriterException e) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes--Qrcode encode exception:" + e.getLocalizedMessage());
                    }
                } else {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes--request success && isEmpty");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getTradeQrcodeBytes--request error:" + e.getLocalizedMessage());
            }
        });

        if (tradeQrcodeBytes == null) {
            tradeQrcodeBytes = getClubQRCodeSync();
        }
        return tradeQrcodeBytes;
    }

    // 会所活动二维码
    private byte[] clubQrcodeBytes;

    public byte[] getClubQRCodeSync() {
        final String clubId = AccountManager.getInstance().getClubId();
        ClubQrcodeBytes c = LocalPersistenceManager.getClubQrcode(clubId);
        if (c != null) {
            clubQrcodeBytes = c.data;
            return clubQrcodeBytes;
        }

        Call<StringResult> callGetUrl = XmdNetwork.getInstance().getService(SpaService.class).getClubWXQrcodeURL(AccountManager.getInstance().getClubId());
        XmdNetwork.getInstance().requestSync(callGetUrl, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                String content = result.getRespData();
                if (!TextUtils.isEmpty(content)) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeSync content:" + content);
                }
                Call<ResponseBody> callGetBytes = XmdNetwork.getInstance().getService(SpaService.class).getClubQrcodeByWX(content);
                XmdNetwork.getInstance().requestSync(callGetBytes, new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeBytes error:" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            byte[] bitmapBytes = responseBody.bytes();
                            if (bitmapBytes != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                                Matrix matrix = new Matrix();
                                matrix.postScale(240.f / bitmap.getWidth(), 240.f / bitmap.getHeight());
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                                    clubQrcodeBytes = bos.toByteArray();
                                    ClubQrcodeBytes cc = new ClubQrcodeBytes();
                                    cc.data = clubQrcodeBytes;
                                    LocalPersistenceManager.writeClubQrcodeBytes(clubId, cc);
                                } else {
                                    XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeBytes : bitmap.compress failed!");
                                }
                                bitmap.recycle();
                            } else {
                                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeBytes : can not get qrcode !");
                            }
                        } catch (IOException e) {
                            XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeBytes exception:" + e.getLocalizedMessage());
                        }
                    }
                });
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_TRADE_PAYMENT + "getClubQRCodeURL error:" + e.getLocalizedMessage());
            }
        });
        return clubQrcodeBytes;
    }

    // 二维码授权支付
    public Subscription activeAuthPay(int amount, String authCode, String payOrderId, String payNo, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .activeAuthPay(AccountManager.getInstance().getToken(), String.valueOf(amount), payNo, authCode, payOrderId);
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
}
