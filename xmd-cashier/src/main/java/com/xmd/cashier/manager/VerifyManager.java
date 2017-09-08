package com.xmd.cashier.manager;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.ErrCode;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.CheckInfo;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.dal.bean.PrizeInfo;
import com.xmd.cashier.dal.bean.TreatInfo;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.dal.bean.VerifyRecordInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.CheckInfoListResult;
import com.xmd.cashier.dal.net.response.CommonVerifyResult;
import com.xmd.cashier.dal.net.response.CouponResult;
import com.xmd.cashier.dal.net.response.OrderResult;
import com.xmd.cashier.dal.net.response.PrizeResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.net.response.VerifyRecordDetailResult;
import com.xmd.cashier.dal.net.response.VerifyRecordResult;
import com.xmd.cashier.dal.net.response.VerifyTypeResult;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkException;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.ServerException;
import com.xmd.m.network.XmdNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-5-19.
 */

public class VerifyManager {
    private IPos mPos;

    private List<CheckInfo> mVerifyList;    //核销列表

    private static VerifyManager mInstance = new VerifyManager();

    public static VerifyManager getInstance() {
        return mInstance;
    }

    private VerifyManager() {
        mPos = PosFactory.getCurrentCashier();
        mVerifyList = new ArrayList<>();
    }

    /**********************************************************************************************/
    public List<CheckInfo> getVerifyList() {
        return mVerifyList;
    }

    public void clearVerifyList() {
        if (mVerifyList != null) {
            mVerifyList.clear();
        }
    }

    // 获取要核销的折扣券列表
    public List<CouponInfo> getDiscountList() {
        List<CouponInfo> discountList = new ArrayList<>();
        for (CheckInfo info : mVerifyList) {
            if (info.getSelected() && !info.getSuccess() && AppConstants.TYPE_DISCOUNT_COUPON.equals(info.getType())) {
                discountList.add((CouponInfo) info.getInfo());
            }
        }
        return discountList;
    }

    // 要核销列表中是否有折扣券
    public boolean hasDiscount() {
        for (CheckInfo info : mVerifyList) {
            if (info.getSelected() && !info.getSuccess() && AppConstants.TYPE_DISCOUNT_COUPON.equals(info.getType())) {
                return true;
            }
        }
        return false;
    }

    public List<CheckInfo> getResultList() {
        List<CheckInfo> resultList = new ArrayList<>();
        for (CheckInfo info : mVerifyList) {
            if (info.getSelected() && !info.getSuccess()) {
                resultList.add(info);
            }
        }
        return resultList;
    }

    public List<CheckInfo> getSuccessList() {
        List<CheckInfo> successList = new ArrayList<>();
        for (CheckInfo info : mVerifyList) {
            if (info.getSelected() && info.getSuccess()) {
                successList.add(info);
            }
        }
        return successList;
    }

    public boolean hasFailed() {
        for (CheckInfo info : mVerifyList) {
            if (info.getSelected() && !info.getSuccess()) {
                return true;
            }
        }
        return false;
    }

    public int getSelectedCount() {
        int count = 0;
        for (CheckInfo info : mVerifyList) {
            if (info.getSelected()) {
                count++;
            }
        }
        return count;
    }

    public void setItemSelectedStatus(CheckInfo info, boolean selected) {
        int index = mVerifyList.indexOf(info);
        if (index >= 0) {
            mVerifyList.get(index).setSelected(selected);
        }
    }

    public int getSuccessCount() {
        int count = 0;
        for (CheckInfo info : mVerifyList) {
            if (info.getSelected() && info.getSuccess()) {
                count++;
            }
        }
        return count;
    }

    public int getFailedCount() {
        return getSelectedCount() - getSuccessCount();
    }

    /********************************************* 核销记录 ****************************************/
    // 核销记录类型列表
    public Subscription getVerifyTypeList(final Callback<VerifyTypeResult> callback) {
        Observable<VerifyTypeResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getVerifyTypeList(AccountManager.getInstance().getToken());
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<VerifyTypeResult>() {
            @Override
            public void onCallbackSuccess(VerifyTypeResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 核销记录列表
    public Subscription getVerifyRecordList(int page, int pageSize, String telephone, String type, String startDate, String endDate, final Callback<VerifyRecordResult> callback) {
        Observable<VerifyRecordResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getVerifyRecordList(AccountManager.getInstance().getToken(), String.valueOf(page), String.valueOf(pageSize), telephone, type, AppConstants.APP_REQUEST_YES, startDate, endDate);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<VerifyRecordResult>() {
            @Override
            public void onCallbackSuccess(VerifyRecordResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 核销记录详情
    public Subscription getVerifyRecordDetail(String recordId, final Callback<VerifyRecordDetailResult> callback) {
        Observable<VerifyRecordDetailResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getVerifyRecordDetail(AccountManager.getInstance().getToken(), recordId);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<VerifyRecordDetailResult>() {
            @Override
            public void onCallbackSuccess(VerifyRecordDetailResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    /***************************************** 核销详情 ********************************************/
    // ---根据核销码查询核销码类型---
    public Subscription getVerifyType(String code, final Callback<StringResult> callback) {
        Observable<StringResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getVerifyType(AccountManager.getInstance().getToken(), code);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // ---根据手机号获取核销列表:券+付费预约---
    public Subscription getCheckInfoList(String code, final Callback<CheckInfoListResult> callback) {
        Observable<CheckInfoListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getCheckInfoList(code, AccountManager.getInstance().getToken());
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<CheckInfoListResult>() {
            @Override
            public void onCallbackSuccess(CheckInfoListResult result) {
                // 处理没有数据的情况
                if (result == null || result.getRespData() == null || result.getRespData().isEmpty()) {
                    callback.onError("暂未查询到可用的核销信息");
                    return;
                }

                // 处理内容
                Gson gson = new Gson();
                for (CheckInfo info : result.getRespData()) {
                    switch (info.getInfoType()) {
                        case AppConstants.CHECK_INFO_TYPE_COUPON:
                            // 券
                            if (info.getInfo() instanceof String) {
                                info.setInfo(gson.fromJson((String) info.getInfo(), CouponInfo.class));
                            } else {
                                info.setInfo(gson.fromJson(gson.toJson(info.getInfo()), CouponInfo.class));
                            }
                            CouponInfo couponInfo = (CouponInfo) info.getInfo();
                            couponInfo.valid = info.getValid();
                            break;
                        case AppConstants.CHECK_INFO_TYPE_ORDER:
                            // 付费预约
                            if (info.getInfo() instanceof String) {
                                info.setInfo(gson.fromJson((String) info.getInfo(), OrderInfo.class));
                            } else {
                                info.setInfo(gson.fromJson(gson.toJson(info.getInfo()), OrderInfo.class));
                            }
                            break;
                        default:
                            break;
                    }
                }

                // 排序
                Collections.sort(result.getRespData(), new Comparator<CheckInfo>() {
                    @Override
                    public int compare(CheckInfo lhs, CheckInfo rhs) {
                        if (lhs.getValid() && rhs.getValid()) {
                            return 0;
                        } else if (lhs.getValid()) {
                            return -1;
                        } else if (rhs.getValid()) {
                            return 1;
                        }
                        return 0;
                    }
                });
                mVerifyList = result.getRespData();
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // ---查询核销详情：优惠券---
    public Subscription getCouponInfo(String couponNo, final Callback<CouponResult> callback) {
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

    // ---查询核销详情：付费预约---
    public Subscription getPaidOrderInfo(String orderNo, final Callback<OrderResult> callback) {
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

    // ---查询核销详情：奖品---
    public Subscription getPrizeInfo(String verifyCode, final Callback<PrizeResult> callback) {
        Observable<PrizeResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getPrizeInfo(AccountManager.getInstance().getToken(), verifyCode);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<PrizeResult>() {
            @Override
            public void onCallbackSuccess(PrizeResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // ---查询核销详情：项目券---
    public Subscription getServiceCouponInfo(String couponNo, final Callback<CouponResult> callback) {
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

    // ---查询核销详情：默认---
    public Subscription getCommonVerifyInfo(String code, String type, final Callback<CommonVerifyResult> callback) {
        Observable<CommonVerifyResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getCommonVerifyInfo(AccountManager.getInstance().getToken(), code, type);
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

    /******************************************** 处理核销 *****************************************/
    // ---核销：优惠券---
    public Subscription verifyCoupon(String couponNo, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .verifyCoupon(AccountManager.getInstance().getToken(), couponNo);
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

    // ---核销：付费预约---
    public Subscription verifyPaidOrder(String orderNo, String processType, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .verifyPaidOrder(AccountManager.getInstance().getToken(), orderNo, processType);
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

    // ---核销：奖品---
    public Subscription verifyLuckyWheel(String verifyCode, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .verifyLuckyWheel(AccountManager.getInstance().getToken(), verifyCode);
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

    // ---核销：项目券---
    public Subscription verifyServiceCoupon(String couponNo, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .verifyServiceCoupon(AccountManager.getInstance().getToken(), couponNo);
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


    // ---核销：withMoney---
    public Subscription verifyWithMoney(int amount, String code, String type, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .verifyWithMoney(AccountManager.getInstance().getToken(), String.valueOf(amount), code, type);
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

    // ---核销：任意---
    public Subscription verifyCommon(String code, final Callback<BaseBean> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .verifyCommon(AccountManager.getInstance().getToken(), code);
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

    // ---核销:批量---
    public Subscription verifyCheckInfo(final Callback<List<CheckInfo>> callback) {
        final List<CheckInfo> infos = mVerifyList;
        return Observable
                .create(new Observable.OnSubscribe<List<CheckInfo>>() {
                    @Override
                    public void call(Subscriber<? super List<CheckInfo>> subscriber) {
                        for (final CheckInfo info : infos) {
                            if (!info.getSelected() || info.getSuccess()) {
                                continue;
                            }
                            switch (info.getType()) {
                                case AppConstants.TYPE_COUPON:
                                case AppConstants.TYPE_CASH_COUPON:
                                case AppConstants.TYPE_GIFT_COUPON:
                                case AppConstants.TYPE_PAID_COUPON:
                                case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                                    Call<BaseBean> commonCall = XmdNetwork.getInstance().getService(SpaService.class)
                                            .verifyCommonCall(AccountManager.getInstance().getToken(), info.getCode());
                                    XmdNetwork.getInstance().requestSync(commonCall, new NetworkSubscriber<BaseBean>() {
                                        @Override
                                        public void onCallbackSuccess(BaseBean result) {
                                            info.setErrorCode(result.getStatusCode());
                                            info.setSuccess(true);
                                            info.setErrorMsg(AppConstants.APP_REQUEST_YES);
                                        }

                                        @Override
                                        public void onCallbackError(Throwable e) {
                                            info.setSuccess(false);
                                            info.setErrorMsg(e.getLocalizedMessage());
                                            if (e instanceof NetworkException) {
                                                info.setErrorCode(ErrCode.ERRCODE_NETWORK);
                                            } else if (e instanceof ServerException) {
                                                info.setErrorCode(ErrCode.ERRCODE_SERVER);
                                            }
                                        }
                                    });
                                    break;
                                case AppConstants.TYPE_DISCOUNT_COUPON:
                                    CouponInfo discountInfo = (CouponInfo) info.getInfo();
                                    Call<BaseBean> discountCall = XmdNetwork.getInstance().getService(SpaService.class)
                                            .verifyWithMoneyCall(AccountManager.getInstance().getToken(), String.valueOf(discountInfo.originAmount), info.getCode(), info.getType());
                                    XmdNetwork.getInstance().requestSync(discountCall, new NetworkSubscriber<BaseBean>() {
                                        @Override
                                        public void onCallbackSuccess(BaseBean result) {
                                            info.setErrorCode(result.getStatusCode());
                                            info.setSuccess(true);
                                            info.setErrorMsg(AppConstants.APP_REQUEST_YES);
                                        }

                                        @Override
                                        public void onCallbackError(Throwable e) {
                                            info.setSuccess(false);
                                            info.setErrorMsg(e.getLocalizedMessage());
                                            if (e instanceof NetworkException) {
                                                info.setErrorCode(ErrCode.ERRCODE_NETWORK);
                                            } else if (e instanceof ServerException) {
                                                info.setErrorCode(ErrCode.ERRCODE_SERVER);
                                            }
                                        }
                                    });
                                    break;
                                case AppConstants.TYPE_ORDER:
                                    Call<BaseBean> orderCall = XmdNetwork.getInstance().getService(SpaService.class)
                                            .verifyPaidOrderCall(AccountManager.getInstance().getToken(), info.getCode(), AppConstants.PAID_ORDER_OP_VERIFIED);
                                    XmdNetwork.getInstance().requestSync(orderCall, new NetworkSubscriber<BaseBean>() {
                                        @Override
                                        public void onCallbackSuccess(BaseBean result) {
                                            info.setErrorCode(result.getStatusCode());
                                            info.setSuccess(true);
                                            info.setErrorMsg(AppConstants.APP_REQUEST_YES);
                                        }

                                        @Override
                                        public void onCallbackError(Throwable e) {
                                            info.setSuccess(false);
                                            info.setErrorMsg(e.getLocalizedMessage());
                                            if (e instanceof NetworkException) {
                                                info.setErrorCode(ErrCode.ERRCODE_NETWORK);
                                            } else if (e instanceof ServerException) {
                                                info.setErrorCode(ErrCode.ERRCODE_SERVER);
                                            }
                                        }
                                    });
                                    break;
                                default:
                                    break;
                            }
                        }
                        subscriber.onNext(infos);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CheckInfo>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(List<CheckInfo> list) {
                        callback.onSuccess(list);
                    }
                });
    }

    // 体验券 现金券 礼品券 折扣券
    private void printCouponInfo(CouponInfo info) {
        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("(核销小票)");
        mPos.printCenter("\n");
        mPos.printText("客户:", info.userName);
        mPos.printText("手机:", Utils.formatPhone(info.userPhone));
        mPos.printText("核销类型:", "优惠券");
        mPos.printText("核销时间:", DateUtils.doDate2String(new Date()));
        mPos.printText("核销人:", AccountManager.getInstance().getUser().userName);
        mPos.printText("核销码:", info.couponNo);
        mPos.printDivide();
        mPos.printText("优惠券名称:", info.actTitle);
        mPos.printText("优惠券详情:", info.consumeMoneyDescription);
        mPos.printText("优惠券类型:", info.useTypeName + "(" + info.useTypeName + ")");
        mPos.printText("有效期:", info.couponPeriod);
        mPos.printEnd();
    }

    // 点钟券
    private void printPaidCouponInfo(CouponInfo paidCoupon) {
        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("(核销小票)");
        mPos.printCenter("\n");
        mPos.printText("客户:", paidCoupon.userName);
        mPos.printText("手机:", Utils.formatPhone(paidCoupon.userPhone));
        mPos.printText("核销类型:", "点钟券");
        mPos.printText("核销时间:", DateUtils.doDate2String(new Date()));
        mPos.printText("核销人:", AccountManager.getInstance().getUser().userName);
        mPos.printText("核销码:", paidCoupon.couponNo);
        mPos.printDivide();
        mPos.printText("点钟券名称:", paidCoupon.actTitle);
        mPos.printText("点钟券详情:", paidCoupon.consumeMoneyDescription);
        mPos.printText("有效期:", paidCoupon.couponPeriod);
        mPos.printEnd();
    }

    // 项目券
    private void printServiceItemCouponInfo(CouponInfo serviceCoupon) {
        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("(核销小票)");
        mPos.printCenter("\n");
        mPos.printText("客户:", serviceCoupon.userName);
        mPos.printText("手机:", Utils.formatPhone(serviceCoupon.userPhone));
        mPos.printText("核销类型:", "项目券");
        mPos.printText("核销时间:", DateUtils.doDate2String(new Date()));
        mPos.printText("核销人:", AccountManager.getInstance().getUser().userName);
        mPos.printText("核销码:", serviceCoupon.couponNo);
        mPos.printDivide();
        mPos.printText("活动名称:", serviceCoupon.actSubTitle);
        if (serviceCoupon.itemNames != null && !serviceCoupon.itemNames.isEmpty()) {
            StringBuilder itemsBuild = new StringBuilder();
            for (String item : serviceCoupon.itemNames) {
                itemsBuild.append(item).append(",");
            }
            itemsBuild.setLength(itemsBuild.length() - 1);
            mPos.printText("项目名称:", itemsBuild.toString());
        }
        mPos.printText("原价:", Utils.moneyToStringEx(serviceCoupon.consumeAmount) + "元");
        switch (serviceCoupon.paidType) {
            case AppConstants.TYPE_PAID_AMOUNT:
                mPos.printText("实收:", Utils.moneyToStringEx(serviceCoupon.actAmount) + "元");
                break;
            case AppConstants.TYPE_PAID_CREDITS:
                mPos.printText("实收:", serviceCoupon.creditAmount + "积分");
                break;
            case AppConstants.TYPE_PAID_FREE:
                mPos.printText("实收:", "免费");
                break;
            default:
                break;
        }
        mPos.printEnd();
    }

    //付费预约
    public void printOrder(OrderInfo orderInfo) {
        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("(核销小票)");
        mPos.printCenter("\n");
        mPos.printText("客户:", orderInfo.customerName);
        mPos.printText("手机:", Utils.formatPhone(orderInfo.phoneNum));
        mPos.printText("核销类型:", "付费预约");
        mPos.printText("核销时间:", DateUtils.doDate2String(new Date()));
        mPos.printText("核销人:", AccountManager.getInstance().getUser().userName);
        mPos.printText("核销码:", orderInfo.orderNo);
        mPos.printDivide();
        mPos.printText("预约定金:", Utils.moneyToStringEx(orderInfo.downPayment) + "元");
        if (TextUtils.isEmpty(orderInfo.techNo)) {
            mPos.printText("预约技师:", orderInfo.techName);
        } else {
            mPos.printText("预约技师:", orderInfo.techName + "[" + orderInfo.techNo + "]");
        }
        mPos.printText("预约项目:", (TextUtils.isEmpty(orderInfo.serviceItemName) ? "到店选择" : orderInfo.serviceItemName));
        mPos.printText("下单时间:", orderInfo.createdAt);
        mPos.printText("预约时间:", orderInfo.appointTime);
        mPos.printEnd();
    }

    // 转盘奖品
    public void printLuckWheel(PrizeInfo prizeInfo) {
        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("(核销小票)");
        mPos.printCenter("\n");
        mPos.printText("客户:", prizeInfo.userName);
        mPos.printText("手机:", Utils.formatPhone(prizeInfo.telephone));
        mPos.printText("核销类型:", "奖品");
        mPos.printText("核销时间:", DateUtils.doDate2String(new Date()));
        mPos.printText("核销人:", AccountManager.getInstance().getUser().userName);
        mPos.printText("核销码:", prizeInfo.verifyCode);
        mPos.printDivide();
        mPos.printText("活动名称:", prizeInfo.activityName);
        mPos.printText("奖品:", prizeInfo.prizeName);
        mPos.printEnd();
    }

    // 会员请客
    public void printTreatInfo(TreatInfo treatInfo) {
        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("(核销小票)");
        mPos.printCenter("\n");
        mPos.printText("客户:", treatInfo.userName);
        mPos.printText("手机:", Utils.formatPhone(treatInfo.userPhone));
        mPos.printText("核销类型:", "会员请客");
        mPos.printText("核销时间:", DateUtils.doDate2String(new Date()));
        mPos.printText("核销人:", AccountManager.getInstance().getUser().userName);
        mPos.printText("核销码:", treatInfo.authorizeCode);
        mPos.printDivide();
        mPos.printText("抵扣金额:", Utils.moneyToStringEx(treatInfo.useMoney) + "元");
        mPos.printEnd();
    }

    // 核销:根据核销类型打印
    public void printByVerifyType(CheckInfo checkInfo) {
        switch (checkInfo.getType()) {
            case AppConstants.TYPE_COUPON:
            case AppConstants.TYPE_CASH_COUPON:
            case AppConstants.TYPE_GIFT_COUPON:
            case AppConstants.TYPE_DISCOUNT_COUPON:
                printCouponInfo((CouponInfo) checkInfo.getInfo());
                break;
            case AppConstants.TYPE_PAID_COUPON:
                printPaidCouponInfo((CouponInfo) checkInfo.getInfo());
                break;
            case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                printServiceItemCouponInfo((CouponInfo) checkInfo.getInfo());
                break;
            case AppConstants.TYPE_ORDER:
                printOrder((OrderInfo) checkInfo.getInfo());
                break;
            default:
                break;
        }
    }

    // 收银:根据核销类型打印
    public void printByVerifyType(VerificationItem item) {
        switch (item.type) {
            case AppConstants.TYPE_COUPON:
            case AppConstants.TYPE_CASH_COUPON:
            case AppConstants.TYPE_DISCOUNT_COUPON:
                printCouponInfo(item.couponInfo);
                break;
            case AppConstants.TYPE_PAID_COUPON:
                printPaidCouponInfo(item.couponInfo);
                break;
            case AppConstants.TYPE_ORDER:
                printOrder(item.order);
                break;
            case AppConstants.TYPE_PAY_FOR_OTHER:
                printTreatInfo(item.treatInfo);
                break;
            default:
                break;
        }
    }


    // 根据券类型打印
    public void printByCouponType(CouponInfo info) {
        switch (info.couponType) {
            case AppConstants.COUPON_TYPE_PAID:     //点钟券
                printPaidCouponInfo(info);
                break;
            case AppConstants.COUPON_TYPE_SERVICE_ITEM:  //项目券
                printServiceItemCouponInfo(info);
                break;
            case AppConstants.COUPON_TYPE_COUPON:   //体验券
            case AppConstants.COUPON_TYPE_GIFT:     //礼品券
            case AppConstants.COUPON_TYPE_CASH:     //现金券
            case AppConstants.COUPON_TYPE_DISCOUNT: //折扣券
                printCouponInfo(info);
                break;
            default:
                break;
        }
    }

    /**********************************************************************************************/
    public void printCheckInfoList(List<CheckInfo> list, boolean keep, Callback<?> callback) {
        if (list == null || list.isEmpty()) {
            return;
        }
        mPos.setPrintListener(callback);
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter(keep ? "商户存根" : "客户联");
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
        if (keep) {
            mPos.printText("手机号码：", list.get(0).getUserPhone() + "(" + list.get(0).getUserName() + ")");
        } else {
            mPos.printText("手机号码：", Utils.formatPhone(list.get(0).getUserPhone()) + "(" + Utils.formatName(list.get(0).getUserName()) + ")");
        }
        mPos.printDivide();

        int orderAmount = 0;
        int couponAmount = 0;
        int originAmount = 0;
        for (CheckInfo info : list) {
            switch (info.getInfoType()) {
                case AppConstants.CHECK_INFO_TYPE_COUPON:
                    CouponInfo couponInfo = (CouponInfo) info.getInfo();
                    couponAmount += couponInfo.getReallyCouponMoney();
                    if (AppConstants.COUPON_TYPE_DISCOUNT.equals(couponInfo.couponType)) {
                        originAmount = couponInfo.originAmount;
                    }
                    break;
                case AppConstants.CHECK_INFO_TYPE_ORDER:
                    orderAmount += ((OrderInfo) info.getInfo()).downPayment;
                    break;
                default:
                    break;
            }
        }
        mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(originAmount));
        mPos.printText("用券抵扣：", "-￥ " + Utils.moneyToStringEx(couponAmount));
        mPos.printText("预约抵扣：", "-￥ " + Utils.moneyToStringEx(orderAmount));
        mPos.printDivide();
        mPos.printRight("实收金额：" + 0 + " 元");
        mPos.printDivide();

        mPos.printText("优惠详情");
        for (CheckInfo info : list) {
            switch (info.getType()) {
                case AppConstants.TYPE_COUPON:
                case AppConstants.TYPE_CASH_COUPON:
                case AppConstants.TYPE_GIFT_COUPON:
                case AppConstants.TYPE_DISCOUNT_COUPON:
                case AppConstants.TYPE_PAID_COUPON:
                    CouponInfo couponInfo = (CouponInfo) info.getInfo();
                    mPos.printText("[" + couponInfo.couponTypeName + "]" + couponInfo.actTitle, "(-" + Utils.moneyToString(couponInfo.getReallyCouponMoney()) + "元)");
                    mPos.printText(couponInfo.consumeMoneyDescription + "/" + couponInfo.couponNo + "/" + Utils.formatCode(couponInfo.userPhone));
                    break;
                case AppConstants.TYPE_SERVICE_ITEM_COUPON: //项目券
                    CouponInfo serviceCouponInfo = (CouponInfo) info.getInfo();
                    String items = null;
                    if (serviceCouponInfo.itemNames != null && !serviceCouponInfo.itemNames.isEmpty()) {
                        StringBuilder itemsBuild = new StringBuilder();
                        for (String item : serviceCouponInfo.itemNames) {
                            itemsBuild.append(item).append(",");
                        }
                        itemsBuild.setLength(itemsBuild.length() - 1);
                        items = itemsBuild.toString();
                    }
                    mPos.printText("[" + serviceCouponInfo.couponTypeName + "]" + serviceCouponInfo.actSubTitle, "(-" + Utils.moneyToString(serviceCouponInfo.consumeAmount) + "元)");
                    mPos.printText((TextUtils.isEmpty(items) ? "未指定" : items) + "/" + serviceCouponInfo.consumeMoneyDescription);
                    mPos.printText(serviceCouponInfo.couponNo + "/" + Utils.formatCode(serviceCouponInfo.userPhone));
                    break;
                case AppConstants.TYPE_ORDER:
                    OrderInfo orderInfo = (OrderInfo) info.getInfo();
                    mPos.printText("[付费预约]" + (TextUtils.isEmpty(orderInfo.serviceItemName) ? "到店选择" : orderInfo.serviceItemName) + (TextUtils.isEmpty(orderInfo.techNo) ? "" : "，" + orderInfo.techNo + "号"), "(-" + Utils.moneyToString(orderInfo.downPayment) + "元)");
                    mPos.printText(orderInfo.orderNo + "/" + Utils.formatCode(orderInfo.phoneNum));
                    break;
                default:
                    break;
            }
        }
        mPos.printDivide();
        mPos.printText("交易时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));
        mPos.printText("核销终端：", "POS机");
        mPos.printText("收款人员：", AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")");
        mPos.printText("打印时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));
        if (!keep) {    //客户联
            byte[] qrCodeBytes = TradeManager.getInstance().getClubQRCodeSync();
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
                mPos.printCenter("微信扫码，选技师、抢优惠");
            }
        }
        mPos.printEnd();
    }

    // 券
    public void printCoupon(CouponInfo couponInfo, boolean keep, Callback<?> callback) {
        mPos.setPrintListener(callback);
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter(keep ? "商户存根" : "客户联");
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
        mPos.printText("手机号码：", (keep ? couponInfo.userPhone : Utils.formatPhone(couponInfo.userPhone)) + "(" + (keep ? couponInfo.userName : Utils.formatName(couponInfo.userName)) + ")");
        mPos.printDivide();

        mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(couponInfo.originAmount));
        mPos.printText(couponInfo.couponTypeName + "抵扣金额：", "-￥ " + Utils.moneyToStringEx(couponInfo.getReallyCouponMoney()));
        mPos.printDivide();
        mPos.printRight("实收金额：" + 0 + "元", true);
        mPos.printDivide();
        mPos.printText("优惠详情");
        switch (couponInfo.couponType) {
            case AppConstants.COUPON_TYPE_SERVICE_ITEM:
                String items = null;
                if (couponInfo.itemNames != null && !couponInfo.itemNames.isEmpty()) {
                    StringBuilder itemsBuild = new StringBuilder();
                    for (String item : couponInfo.itemNames) {
                        itemsBuild.append(item).append(",");
                    }
                    itemsBuild.setLength(itemsBuild.length() - 1);
                    items = itemsBuild.toString();
                }
                mPos.printText("[" + couponInfo.couponTypeName + "]" + couponInfo.actSubTitle, "(-" + Utils.moneyToString(couponInfo.consumeAmount) + "元)");
                mPos.printText((TextUtils.isEmpty(items) ? "未指定" : items) + "/" + couponInfo.consumeMoneyDescription);
                mPos.printText(couponInfo.couponNo + "/" + Utils.formatCode(couponInfo.userPhone));
                break;
            default:
                mPos.printText("[" + couponInfo.couponTypeName + "]" + couponInfo.actTitle, "(-" + Utils.moneyToString(couponInfo.getReallyCouponMoney()) + "元)");
                mPos.printText(couponInfo.consumeMoneyDescription + "/" + couponInfo.couponNo + "/" + Utils.formatCode(couponInfo.userPhone));
                break;
        }
        mPos.printDivide();

        mPos.printText("交易时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));
        mPos.printText("核销终端：", "POS机");
        mPos.printText("收款人员：", AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")");
        mPos.printText("打印时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));
        if (!keep) {    //客户联
            byte[] qrCodeBytes = TradeManager.getInstance().getClubQRCodeSync();
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
                mPos.printCenter("微信扫码，选技师、抢优惠");
            }
        }
        mPos.printEnd();
    }

    // 预约
    public void printOrder(OrderInfo orderInfo, boolean keep, Callback<?> callback) {
        mPos.setPrintListener(callback);
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter(keep ? "商户存根" : "客户联");
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
        mPos.printText("手机号码：", (keep ? orderInfo.phoneNum : Utils.formatPhone(orderInfo.phoneNum)) + "(" + (keep ? orderInfo.customerName : Utils.formatName(orderInfo.customerName)) + ")");
        mPos.printDivide();

        mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(orderInfo.downPayment));
        mPos.printText("预约抵扣金额：", "-￥ " + Utils.moneyToStringEx(orderInfo.downPayment));
        mPos.printDivide();
        mPos.printRight("实收金额：" + 0 + "元", true);
        mPos.printDivide();

        mPos.printText("优惠详情");
        mPos.printText("[付费预约]" + (TextUtils.isEmpty(orderInfo.serviceItemName) ? "到店选择" : orderInfo.serviceItemName) + (TextUtils.isEmpty(orderInfo.techNo) ? "" : "，" + orderInfo.techNo), "(-" + Utils.moneyToString(orderInfo.downPayment) + "元)");
        mPos.printText(orderInfo.orderNo + "/" + Utils.formatCode(orderInfo.phoneNum));
        mPos.printDivide();

        mPos.printText("交易时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));
        mPos.printText("核销终端：", "POS机");
        mPos.printText("收款人员：", AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")");
        mPos.printText("打印时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));

        if (!keep) {    //客户联
            byte[] qrCodeBytes = TradeManager.getInstance().getClubQRCodeSync();
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
                mPos.printCenter("微信扫码，选技师、抢优惠");
            }
        }
        mPos.printEnd();
    }

    // 奖品
    public void printPrize(PrizeInfo prizeInfo, boolean keep, Callback<?> callback) {
        mPos.setPrintListener(callback);
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter(keep ? "商户存根" : "客户联");
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
        mPos.printText("手机号码：", (keep ? prizeInfo.telephone : Utils.formatPhone(prizeInfo.telephone)) + "(" + (keep ? prizeInfo.userName : Utils.formatName(prizeInfo.userName)) + ")");
        mPos.printDivide();

        mPos.printText("订单金额：", "￥ " + 0);
        mPos.printText("抵扣金额：", "-￥ " + 0);
        mPos.printDivide();
        mPos.printRight("实收金额：" + 0 + "元", true);
        mPos.printDivide();

        mPos.printText("优惠详情");
        mPos.printText("[奖品]" + prizeInfo.activityName);
        mPos.printText("奖品：" + prizeInfo.prizeName);
        mPos.printText(prizeInfo.verifyCode + "/" + Utils.formatCode(prizeInfo.telephone));
        mPos.printDivide();

        mPos.printText("交易时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));
        mPos.printText("核销终端：", "POS机");
        mPos.printText("收款人员：", AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")");
        mPos.printText("打印时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));

        if (!keep) {    //客户联
            byte[] qrCodeBytes = TradeManager.getInstance().getClubQRCodeSync();
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
                mPos.printCenter("微信扫码，选技师、抢优惠");
            }
        }
        mPos.printEnd();
    }

    // 会员请客
    public void printTreat(TreatInfo treatInfo, boolean keep, Callback<?> callback) {
        mPos.setPrintListener(callback);
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter(keep ? "商户存根" : "客户联");
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
        mPos.printText("手机号码：", (keep ? treatInfo.userPhone : Utils.formatPhone(treatInfo.userPhone)) + "(" + (keep ? treatInfo.userName : Utils.formatName(treatInfo.userName)) + ")");
        mPos.printDivide();

        mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(treatInfo.useMoney));
        mPos.printText("抵扣金额：", "-￥ " + Utils.moneyToStringEx((int) (treatInfo.useMoney * (1000 - treatInfo.memberDiscount) / 1000.0f)));
        mPos.printDivide();
        mPos.printRight("实收金额：" + Utils.moneyToStringEx((int) (treatInfo.useMoney * treatInfo.memberDiscount / 1000.0f)) + "元", true);
        mPos.printDivide();

        mPos.printText("优惠详情");
        mPos.printText("[会员卡]" + treatInfo.memberTypeName + "," + String.format("%.02f", treatInfo.memberDiscount / 100.0f) + "折" + Utils.formatCode(treatInfo.userPhone), "(-" + Utils.moneyToString((int) (treatInfo.useMoney * (1000 - treatInfo.memberDiscount) / 1000.0f)) + "元)");
        mPos.printDivide();

        mPos.printText("交易时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));
        mPos.printText("核销终端：", "POS机");
        mPos.printText("收款人员：", AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")");
        mPos.printText("打印时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));

        if (!keep) {    //客户联
            byte[] qrCodeBytes = TradeManager.getInstance().getClubQRCodeSync();
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
                mPos.printCenter("微信扫码，选技师、抢优惠");
            }
        }
        mPos.printEnd();
    }

    // 核销记录
    public void printRecord(VerifyRecordInfo recordInfo, boolean keep) {
        byte[] qrCodeBytes = TradeManager.getInstance().getClubQRCodeSync();
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter((keep ? "商户存根" : "客户联") + "(补打小票)");
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
        mPos.printText("手机号码：", (keep ? recordInfo.telephone : Utils.formatPhone(recordInfo.telephone)) + (TextUtils.isEmpty(recordInfo.userName) ? "" : "(" + (keep ? recordInfo.userName : Utils.formatName(recordInfo.userName)) + ")"));
        mPos.printDivide();
        switch (recordInfo.businessType) {
            case AppConstants.TYPE_COUPON:
            case AppConstants.TYPE_CASH_COUPON:
            case AppConstants.TYPE_GIFT_COUPON:
            case AppConstants.TYPE_DISCOUNT_COUPON:
                mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(recordInfo.originalAmount));
                mPos.printText(recordInfo.businessTypeName + "抵扣金额：", "-￥ " + Utils.moneyToStringEx(recordInfo.amount));
                mPos.printDivide();
                mPos.printRight("实收金额：" + Utils.moneyToStringEx((recordInfo.originalAmount >= recordInfo.amount ? recordInfo.originalAmount - recordInfo.amount : 0)) + "元", true);
                mPos.printDivide();
                mPos.printText("优惠详情");
                mPos.printText("[" + recordInfo.businessTypeName + "]" + recordInfo.description, "(-" + Utils.moneyToString(recordInfo.amount) + "元)");
                mPos.printText(recordInfo.consumeMoneyDescription + "/" + recordInfo.verifyCode + "/" + Utils.formatCode(recordInfo.telephone));
                mPos.printDivide();
                break;
            case AppConstants.TYPE_PAID_COUPON:
                mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(recordInfo.originalAmount));
                mPos.printText(recordInfo.businessTypeName + "抵扣金额：", "-￥ " + Utils.moneyToStringEx(recordInfo.originalAmount - recordInfo.amount));
                mPos.printDivide();
                mPos.printRight("实收金额：" + Utils.moneyToStringEx(recordInfo.amount) + "元", true);
                mPos.printDivide();
                mPos.printText("优惠详情");
                mPos.printText("[" + recordInfo.businessTypeName + "]" + recordInfo.description, "(-" + Utils.moneyToStringEx(recordInfo.originalAmount - recordInfo.amount) + "元)");
                mPos.printText(recordInfo.consumeMoneyDescription + "/" + recordInfo.verifyCode + "/" + Utils.formatCode(recordInfo.telephone));
                mPos.printDivide();
                break;
            case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(recordInfo.originalAmount));
                mPos.printText(recordInfo.businessTypeName + "抵扣金额：", "-￥ " + Utils.moneyToStringEx(recordInfo.originalAmount));
                mPos.printDivide();
                switch (recordInfo.paidType) {
                    case AppConstants.TYPE_PAID_AMOUNT:
                        mPos.printRight("实收：" + Utils.moneyToStringEx(recordInfo.amount) + "元", true);
                        break;
                    case AppConstants.TYPE_PAID_CREDITS:
                        mPos.printRight("实收：" + recordInfo.amount + "积分", true);
                        break;
                    case AppConstants.TYPE_PAID_FREE:
                        mPos.printRight("实收：" + "免费", true);
                        break;
                    default:
                        break;
                }
                mPos.printDivide();
                mPos.printText("优惠详情");
                mPos.printText("[" + recordInfo.businessTypeName + "]" + recordInfo.description, "(-" + Utils.moneyToStringEx(recordInfo.originalAmount) + "元)");
                mPos.printText(recordInfo.sourceTypeName + "/" + recordInfo.verifyCode + "/" + Utils.formatCode(recordInfo.telephone));
                mPos.printDivide();
                break;
            case AppConstants.TYPE_ORDER:
                mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(recordInfo.originalAmount));
                mPos.printText(recordInfo.businessTypeName + "抵扣金额：", "-￥ " + Utils.moneyToStringEx(recordInfo.amount));
                mPos.printDivide();
                mPos.printRight("实收金额：" + Utils.moneyToStringEx((recordInfo.originalAmount >= recordInfo.amount ? recordInfo.originalAmount - recordInfo.amount : 0)) + "元", true);
                mPos.printDivide();
                mPos.printText("[" + recordInfo.businessTypeName + "]" + (TextUtils.isEmpty(recordInfo.serviceItemName) ? "到店选择" : recordInfo.serviceItemName) + (TextUtils.isEmpty(recordInfo.techDescription) ? "" : "，" + recordInfo.techDescription), "(-" + Utils.moneyToStringEx(recordInfo.amount) + "元)");
                mPos.printText(recordInfo.verifyCode + "/" + Utils.formatCode(recordInfo.telephone));
                mPos.printDivide();
                break;
            case AppConstants.TYPE_PAY_FOR_OTHER:
                mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(recordInfo.originalAmount));
                mPos.printText(recordInfo.businessTypeName + "抵扣金额：", "-￥ " + Utils.moneyToStringEx(recordInfo.originalAmount - recordInfo.amount));
                mPos.printDivide();
                mPos.printRight("实收金额：" + Utils.moneyToStringEx(recordInfo.amount) + "元", true);
                mPos.printDivide();
                // TODO 会员等级 + 会员折扣
                mPos.printText("优惠详情");
                mPos.printText("[" + recordInfo.businessTypeName + "]" + "授权手机号:" + Utils.formatCode(recordInfo.memberPhone), "(-" + Utils.moneyToStringEx(recordInfo.originalAmount) + "元)");
                mPos.printDivide();
                break;
            case AppConstants.TYPE_LUCKY_WHEEL:
                mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(recordInfo.originalAmount));
                mPos.printText(recordInfo.businessTypeName + "抵扣金额：", "-￥ " + Utils.moneyToStringEx(recordInfo.amount));
                mPos.printDivide();
                mPos.printRight("实收金额：" + Utils.moneyToStringEx((recordInfo.originalAmount >= recordInfo.amount ? recordInfo.originalAmount - recordInfo.amount : 0)) + "元", true);
                mPos.printDivide();
                mPos.printText("优惠详情");
                mPos.printText("[" + recordInfo.businessTypeName + "]" + recordInfo.description, "(-" + Utils.moneyToStringEx(recordInfo.originalAmount) + "元)");
                mPos.printText(recordInfo.sourceTypeName + "/" + recordInfo.verifyCode + "/" + Utils.formatCode(recordInfo.telephone));
                mPos.printDivide();
                break;
        }
        mPos.printText("交易时间：", recordInfo.verifyTime);
        mPos.printText("核销终端：", recordInfo.platformName);
        mPos.printText("收款人员：", recordInfo.operatorName);
        mPos.printText("打印时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));
        if (!keep) {
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
                mPos.printCenter("微信扫码，选技师、抢优惠");
            }
        }
        mPos.printEnd();
    }
}