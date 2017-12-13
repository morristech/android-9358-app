package com.xmd.cashier.manager;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.WriterException;
import com.google.zxing.client.android.MyQrEncoder;
import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.InnerChannelInfo;
import com.xmd.cashier.dal.bean.InnerEmployeeInfo;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.InnerOrderItemInfo;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.bean.OrderDiscountCheckInfo;
import com.xmd.cashier.dal.bean.OrderDiscountInfo;
import com.xmd.cashier.dal.bean.SwitchInfo;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.InnerBatchHoleResult;
import com.xmd.cashier.dal.net.response.InnerChannelListResult;
import com.xmd.cashier.dal.net.response.InnerSwitchResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.net.response.WorkTimeResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.ServerException;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-11-1.
 * 内网收银:处理房间,手牌,技师号;
 */

public class InnerManager {
    private IPos mPos;

    private InnerManager() {
        mPos = PosFactory.getCurrentCashier();
    }

    private static InnerManager mInstance = new InnerManager();

    public static InnerManager getInstance() {
        return mInstance;
    }

    private List<InnerOrderInfo> innerOrderInfos = new ArrayList<>();

    public List<InnerOrderInfo> getInnerOrderInfos() {
        return innerOrderInfos;
    }

    public void clearInnerOrderInfos() {
        innerOrderInfos.clear();
    }

    public boolean findOrderByRoom(long roomId) {
        for (InnerOrderInfo orderInfo : innerOrderInfos) {
            if (roomId == orderInfo.roomId) {
                return true;
            }
        }
        return false;
    }

    public boolean findOrderByHand(long handId) {
        for (InnerOrderInfo orderInfo : innerOrderInfos) {
            if (handId == orderInfo.id) {
                return true;
            }
        }
        return false;
    }

    public void addInnerOrderInfo(InnerOrderInfo info) {
        if (!innerOrderInfos.contains(info)) {
            innerOrderInfos.add(info);
        }
    }

    public void removeInnerOrderInfo(InnerOrderInfo info) {
        if (innerOrderInfos.contains(info)) {
            innerOrderInfos.remove(info);
        }
    }

    public void removeUnselectedInfos() {
        Iterator<InnerOrderInfo> it = innerOrderInfos.iterator();
        while (it.hasNext()) {
            InnerOrderInfo info = it.next();
            if (!info.selected) {
                it.remove();
            }
        }
    }

    public void selectedOrderInfos() {
        for (InnerOrderInfo orderInfo : innerOrderInfos) {
            orderInfo.selected = true;
        }
    }

    public void unselectedOrderInfos() {
        for (InnerOrderInfo orderInfo : innerOrderInfos) {
            orderInfo.selected = false;
        }
    }

    public String getOrderIds() {
        StringBuilder result = new StringBuilder();
        for (InnerOrderInfo info : innerOrderInfos) {
            if (info.selected) {
                result.append(info.id + ",");
            }
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
            return result.toString();
        } else {
            return null;
        }
    }

    public int getSelectCount() {
        int count = 0;
        for (InnerOrderInfo info : innerOrderInfos) {
            if (info.selected) {
                count += 1;
            }
        }
        return count;
    }

    public int getOrderAmount() {
        int amount = 0;
        for (InnerOrderInfo info : innerOrderInfos) {
            if (info.selected) {
                amount += info.amount;
            }
        }
        return amount;
    }

    //获取会所营业时间
    private String startTime;

    public String getStartTime() {
        return startTime;
    }

    public void getClubWorkTime() {
        Observable<WorkTimeResult> observable = XmdNetwork.getInstance().getService(SpaService.class).getWorkTime(AccountManager.getInstance().getToken());
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<WorkTimeResult>() {
            @Override
            public void onCallbackSuccess(WorkTimeResult result) {
                startTime = result.getRespData().startTimeStr;
            }

            @Override
            public void onCallbackError(Throwable e) {
                startTime = AppConstants.STATISTICS_DEFAULT_TIME;
            }
        });
    }

    public void resetClubWorkTime() {
        startTime = AppConstants.STATISTICS_DEFAULT_TIME;
    }


    //获取内网开关
    private boolean mInnerSwitch = false;
    private Call<InnerSwitchResult> mCallInnerSwitch;
    private RetryPool.RetryRunnable mRetryGetInnerSwitch;
    private boolean resultInnerSwitch;

    public boolean getInnerSwitch() {
        return mInnerSwitch;
    }

    public void startGetInnerSwitch() {
        mRetryGetInnerSwitch = new RetryPool.RetryRunnable(3000, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return getInnerSwitchConfig();
            }
        });
        RetryPool.getInstance().postWork(mRetryGetInnerSwitch);
    }

    public void stopGetInnerSwitch() {
        if (mCallInnerSwitch != null && !mCallInnerSwitch.isCanceled()) {
            mCallInnerSwitch.cancel();
        }
        if (mRetryGetInnerSwitch != null) {
            RetryPool.getInstance().removeWork(mRetryGetInnerSwitch);
            mRetryGetInnerSwitch = null;
        }
        mInnerSwitch = false;   //默认内网开关关闭
    }

    public boolean getInnerSwitchConfig() {
        mCallInnerSwitch = XmdNetwork.getInstance().getService(SpaService.class)
                .getInnerSwitch(AccountManager.getInstance().getToken(), AppConstants.INNER_SWITCH_CODE);
        XmdNetwork.getInstance().requestSync(mCallInnerSwitch, new NetworkSubscriber<InnerSwitchResult>() {
            @Override
            public void onCallbackSuccess(InnerSwitchResult result) {
                SwitchInfo switchInfo = result.getRespData();
                if (AppConstants.APP_REQUEST_YES.equals(switchInfo.status)) {
                    mInnerSwitch = true;
                } else {
                    mInnerSwitch = false;
                }
                resultInnerSwitch = true;
                EventBus.getDefault().post(switchInfo);
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.i("getInnerSwitchConfig error :" + e.getLocalizedMessage());
                if (e instanceof ServerException && ((ServerException) e).statusCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                    resultInnerSwitch = true;
                } else {
                    resultInnerSwitch = false;
                }
            }
        });
        return resultInnerSwitch;
    }


    //获取会所支付方式
    private List<InnerChannelInfo> mChannels = new ArrayList<>();
    private Call<InnerChannelListResult> callInnerChannel;
    private RetryPool.RetryRunnable mRetryGetInnerChannel;
    private boolean resultInnerChannel;

    public Map<String, String> getChannels() {
        Map<String, String> map = null;
        if (mChannels != null && !mChannels.isEmpty()) {
            map = new LinkedHashMap<>();
            for (InnerChannelInfo info : mChannels) {
                map.put(info.name, info.type);
            }
        } else {
            map = AppConstants.PAY_CHANNEL_FILTER;
        }
        return map;
    }

    public String getChannelMark(String type) {
        String mark = null;
        if (mChannels != null && !mChannels.isEmpty()) {
            for (InnerChannelInfo info : mChannels) {
                if (type.equals(info.type)) {
                    mark = info.mark;
                }
            }
        }
        return mark;
    }

    public void startGetInnerChannel() {
        mRetryGetInnerChannel = new RetryPool.RetryRunnable(3000, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return getInnerChannelList();
            }
        });
        RetryPool.getInstance().postWork(mRetryGetInnerChannel);
    }

    public void stopGetInnerChannel() {
        if (callInnerChannel != null && !callInnerChannel.isCanceled()) {
            callInnerChannel.cancel();
        }
        if (mRetryGetInnerChannel != null) {
            RetryPool.getInstance().removeWork(mRetryGetInnerChannel);
            mRetryGetInnerChannel = null;
        }
    }

    public boolean getInnerChannelList() {
        callInnerChannel = XmdNetwork.getInstance().getService(SpaService.class)
                .getInnerChannelList(AccountManager.getInstance().getToken());
        XmdNetwork.getInstance().requestSync(callInnerChannel, new NetworkSubscriber<InnerChannelListResult>() {
            @Override
            public void onCallbackSuccess(InnerChannelListResult result) {
                mChannels = result.getRespData();
                resultInnerChannel = true;
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.i("getInnerChannelList error :" + e.getLocalizedMessage());
                if (e instanceof ServerException && ((ServerException) e).statusCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                    // token过期
                    resultInnerChannel = true;
                } else {
                    resultInnerChannel = false;
                }
            }
        });
        return resultInnerChannel;
    }


    // 获取详情
    public Subscription getInnerHoleBatchSubscription(String payOrderId, final Callback<InnerRecordInfo> callBack) {
        Observable<InnerBatchHoleResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getInnerHoleBatch(AccountManager.getInstance().getToken(), payOrderId);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<InnerBatchHoleResult>() {
            @Override
            public void onCallbackSuccess(InnerBatchHoleResult result) {
                callBack.onSuccess(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {
                callBack.onError(e.getLocalizedMessage());
            }
        });
    }

    public void printInnerRecordInfo(InnerRecordInfo info, boolean retry, boolean keep, Callback<?> callback) {
        mPos.setPrintListener(callback);
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter((keep ? "商户存根" : "客户联") + (retry ? "(补打小票)" : ""));
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
        mPos.printText("消费详情", Utils.moneyToStringEx(info.originalAmount), true);
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

        int paidOrderAmount = 0;
        int paidCouponAmount = 0;
        int paidMemberAmount = 0;
        for (OrderDiscountInfo discountInfo : info.orderDiscountList) {
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
                default:
                    break;
            }
        }
        mPos.printDivide();
        mPos.printText("订单金额：", "￥" + Utils.moneyToStringEx(info.originalAmount));
        mPos.printText("预约抵扣：", "-￥" + Utils.moneyToStringEx(paidOrderAmount));
        mPos.printText("用券抵扣：", "-￥" + Utils.moneyToStringEx(paidCouponAmount));
        mPos.printText("会员优惠：", "-￥" + Utils.moneyToStringEx(paidMemberAmount));
        mPos.printDivide();
        mPos.printRight("实收金额：" + Utils.moneyToStringEx(info.payAmount) + "元", true);
        mPos.printDivide();

        if (keep) {
            mPos.printText("优惠详情", "-" + Utils.moneyToString(paidOrderAmount + paidMemberAmount + paidCouponAmount), true);
            for (OrderDiscountInfo discountInfo : info.orderDiscountList) {
                OrderDiscountCheckInfo discountCheckInfo = discountInfo.checkInfo;
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
            }
            mPos.printDivide();
            if (!TextUtils.isEmpty(info.description)) {
                mPos.printText("备注：" + info.description);
                mPos.printDivide();
            }
        }

        mPos.printText("交易号：", info.payId);
        if (!TextUtils.isEmpty(info.transactionId)) {
            mPos.printText("流水号：", info.transactionId);
        }
        mPos.printText("交易时间：", info.createTime);
        mPos.printText("支付方式：", info.payChannelName);
        mPos.printText("收款人员：", (TextUtils.isEmpty(info.operatorName) ? AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")" : info.operatorName));
        mPos.printText("打印时间：", DateUtils.doDate2String(new Date()));

        if (!keep) {
            byte[] qrCodeBytes = getInnerCodeBytes(info);
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
                mPos.printCenter("微信扫码，选技师、抢优惠");
            }
        }
        mPos.printEnd();
    }

    private byte[] innnerQrcodeBytes;

    private byte[] getInnerCodeBytes(InnerRecordInfo innerRecordInfo) {
        innnerQrcodeBytes = null;
        Call<StringResult> tradeCodeCall = XmdNetwork.getInstance().getService(SpaService.class)
                .getTradeQrcode(AccountManager.getInstance().getToken(), innerRecordInfo.id, innerRecordInfo.payChannel, RequestConstant.DEFAULT_SIGN_VALUE);
        XmdNetwork.getInstance().requestSync(tradeCodeCall, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                String content = result.getRespData();
                if (!TextUtils.isEmpty(content)) {
                    XLogger.d("getInnerCodeBytes content:" + content);
                    try {
                        Bitmap bitmap = MyQrEncoder.encode(content, 240, 240);
                        if (bitmap != null) {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                                innnerQrcodeBytes = bos.toByteArray();
                            } else {
                                XLogger.d("getInnerCodeBytes--bitmap compress failed");
                            }
                            bitmap.recycle();
                        } else {
                            XLogger.d("getInnerCodeBytes--Qrcode encode failed");
                        }
                    } catch (WriterException e) {
                        XLogger.d("getInnerCodeBytes--Qrcode encode exception:" + e.getLocalizedMessage());
                    }
                } else {
                    XLogger.d("getInnerCodeBytes--request success && isEmpty");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.d("getInnerCodeBytes--request error:" + e.getLocalizedMessage());
            }
        });

        if (innnerQrcodeBytes == null) {
            innnerQrcodeBytes = TradeManager.getInstance().getClubQRCodeSync();
        }

        return innnerQrcodeBytes;
    }
}
