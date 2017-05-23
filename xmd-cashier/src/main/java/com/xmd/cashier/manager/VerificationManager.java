package com.xmd.cashier.manager;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.ErrCode;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.dal.net.NetworkSubscriber;
import com.xmd.cashier.dal.net.SpaRetrofit;
import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.dal.net.response.OrderOrCouponResult;
import com.xmd.cashier.dal.net.response.UserCouponListResult;
import com.xmd.cashier.exceptions.NetworkException;
import com.xmd.cashier.exceptions.ServerException;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by heyangya on 16-8-23.
 */

public class VerificationManager {
    private static VerificationManager mInstance = new VerificationManager();
    private AccountManager mAccountManager;
    private TradeManager mTradeManager;
    private static final String RESULT_SUCCESS = "Y";
    private static final String RESULT_FAILED = "N";

    private VerificationManager() {
        mAccountManager = AccountManager.getInstance();
        mTradeManager = TradeManager.getInstance();
    }

    public static VerificationManager getInstance() {
        return mInstance;
    }

    /******************************************** 仅做保留 *****************************************/
    // 获取手机号关联的券列表
    public Subscription listCoupons(String phoneNumber, String couponType, final Callback<UserCouponListResult> callback) {
        return SpaRetrofit.getService().listUserCoupons(mAccountManager.getToken(), AppConstants.SESSION_TYPE, phoneNumber, couponType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<UserCouponListResult>() {
                    @Override
                    public void onCallbackSuccess(UserCouponListResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }


    // 获取优惠券或订单信息
    public Subscription getOrderOrCouponView(String orderNumber, final Callback<OrderOrCouponResult> callback) {
        return SpaRetrofit.getService().getOrderOrCouponView(mAccountManager.getToken(), AppConstants.SESSION_TYPE, orderNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<OrderOrCouponResult>() {
                    @Override
                    public void onCallbackSuccess(OrderOrCouponResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // 核销当前已选择的优惠券
    public Subscription verificationCoupon(final int originMoney, final Callback<List<VerificationItem>> callback) {
        final List<VerificationItem> verificationItems = mTradeManager.getCurrentTrade().getCouponList();
        return Observable
                .create(new Observable.OnSubscribe<List<VerificationItem>>() {
                    @Override
                    public void call(Subscriber<? super List<VerificationItem>> subscriber) {
                        //优先核销其他类型的优惠券
                        for (final VerificationItem v : verificationItems) {
                            if (!v.selected) {
                                continue;
                            }
                            //处理核销优惠券
                            if (v.type.equals(AppConstants.TYPE_COUPON)) {
                                // update interface 1.1.0.5
                                SpaRetrofit.getService().verifyCoupon(mAccountManager.getToken(), v.couponInfo.couponNo)
                                        .subscribe(new NetworkSubscriber<BaseResult>() {
                                            @Override
                                            public void onCallbackSuccess(BaseResult result) {
                                                v.success = true;//设置成功结果
                                                v.errorMsg = RESULT_SUCCESS;
                                            }

                                            @Override
                                            public void onCallbackError(Throwable e) {
                                                v.success = false; //设置失败
                                                v.errorMsg = e.getLocalizedMessage();
                                                XLogger.e(v.couponInfo.couponNo + "核销失败：" + v.errorMsg);
                                                if (e instanceof NetworkException) {
                                                    v.errorCode = ErrCode.ERRCODE_NETWORK;
                                                } else if (e instanceof ServerException) {
                                                    v.errorCode = ErrCode.ERRCODE_SERVER;
                                                }
                                            }
                                        });
                            }
                            //处理核销预约订单
                            if (v.type.equals(AppConstants.TYPE_ORDER)) {
                                // update interface 1.1.0.5
                                SpaRetrofit.getService().verifyPaidOrder(mAccountManager.getToken(), v.order.orderNo, AppConstants.PAID_ORDER_OP_VERIFIED)
                                        .subscribe(new NetworkSubscriber<BaseResult>() {
                                            @Override
                                            public void onCallbackSuccess(BaseResult result) {
                                                v.success = true;
                                                v.errorMsg = RESULT_SUCCESS;
                                            }

                                            @Override
                                            public void onCallbackError(Throwable e) {
                                                v.success = false; //设置失败
                                                v.errorMsg = e.getLocalizedMessage();
                                                XLogger.e(v.order.orderNo + "核销失败：" + v.errorMsg);
                                                if (e instanceof NetworkException) {
                                                    v.errorCode = ErrCode.ERRCODE_NETWORK;
                                                } else if (e instanceof ServerException) {
                                                    v.errorCode = ErrCode.ERRCODE_SERVER;
                                                }
                                            }
                                        });
                            }
                        }
                        //现在开始核销请客券
                        calculateTreatUseMoney(originMoney); //计算请客券需要核销的金额
                        for (final VerificationItem v : verificationItems) {
                            if (!v.selected) {
                                continue;
                            }

                            if (v.type.equals(AppConstants.TYPE_PAY_FOR_OTHER)) {
                                if (v.treatInfo.useMoney == 0) {
                                    v.success = true;
                                    continue;
                                }

                                // update interface 1.1.0.5
                                SpaRetrofit.getService().verifyCommon(mAccountManager.getToken(), String.valueOf(v.treatInfo.useMoney), v.treatInfo.authorizeCode, v.type)
                                        .subscribe(new NetworkSubscriber<BaseResult>() {
                                            @Override
                                            public void onCallbackSuccess(BaseResult result) {
                                                v.success = true;
                                                v.errorMsg = RESULT_SUCCESS;
                                            }

                                            @Override
                                            public void onCallbackError(Throwable e) {
                                                v.success = false; //设置失败
                                                v.errorMsg = e.getLocalizedMessage();
                                                XLogger.e(v.treatInfo.authorizeCode + "核销失败：" + v.errorMsg);
                                                if (e instanceof NetworkException) {
                                                    v.errorCode = ErrCode.ERRCODE_NETWORK;
                                                } else if (e instanceof ServerException) {
                                                    v.errorCode = ErrCode.ERRCODE_SERVER;
                                                }
                                            }
                                        });
                            }
                        }
                        subscriber.onNext(verificationItems);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VerificationItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(List<VerificationItem> items) {
                        callback.onSuccess(items);
                    }
                });
    }

    /*************************************************************************************************************************/
    //增加优惠券
    public void addVerificationInfo(VerificationItem verificationItem) {
        List<VerificationItem> verificationItems = mTradeManager.getCurrentTrade().getCouponList();
        if (!verificationItems.contains(verificationItem)) {
            if (verificationItem.type.equals(AppConstants.TYPE_PAY_FOR_OTHER)) {
                verificationItems.add(0, verificationItem);
            } else {
                verificationItems.add(verificationItem);
            }
        }
    }


    public VerificationItem getVerificationById(String id) {
        for (VerificationItem v : mTradeManager.getCurrentTrade().getCouponList()) {
            if (v.couponInfo != null && v.couponInfo.couponNo.equals(id)) {
                return v;
            }
            if (v.order != null && v.order.orderNo != null && v.order.orderNo.equals(id)) {
                return v;
            }
        }
        return null;
    }

    public List<VerificationItem> getVerificationList() {
        return mTradeManager.getCurrentTrade().getCouponList();
    }

    public void cleanVerificationList() {
        mTradeManager.getCurrentTrade().cleanCouponList();
    }

    public void setVerificationSelectedStatus(VerificationItem v, boolean selected) {
        int l = mTradeManager.getCurrentTrade().getCouponList().indexOf(v);
        if (l >= 0) {
            VerificationItem verificationItem = mTradeManager.getCurrentTrade().getCouponList().get(l);
            verificationItem.selected = selected;
        }
    }

    //返回当前选择的优惠金额，单位为分
    public void calculateVerificationValue() {
        calculateVerificationValue(false);
    }

    public void calculateSuccessVerificationValue() {
        calculateVerificationValue(true);
    }

    //只包含请客优惠券，这时不能单独核销
    public boolean onlyHaveTreat() {
        for (VerificationItem v : mTradeManager.getCurrentTrade().getCouponList()) {
            if (v.selected && !v.type.equals(AppConstants.TYPE_PAY_FOR_OTHER)) {
                return false;
            }
        }
        return true;
    }

    public boolean haveFailed() {
        for (VerificationItem v : mTradeManager.getCurrentTrade().getCouponList()) {
            if (v.selected && !v.success) {
                return true;
            }
        }
        return false;
    }

    public boolean haveSelected() {
        for (VerificationItem v : mTradeManager.getCurrentTrade().getCouponList()) {
            if (v.selected) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param onlySuccess 是否只计算核销成功的
     * @return 核销的优惠金额，单位为分
     */
    private void calculateVerificationValue(boolean onlySuccess) {
        int total = 0;
        int noUseTreatMoney = 0;
        //首先计算出所有优惠券提供的总优惠金额
        Trade trade = mTradeManager.getCurrentTrade();
        int selectCount = 0;
        for (VerificationItem info : trade.getCouponList()) {
            if (info.selected && !onlySuccess || info.success) {
                selectCount++;
                switch (info.type) {
                    case AppConstants.TYPE_COUPON:
                        total += info.couponInfo.getReallyCouponMoney() * 100;
                        break;
                    case AppConstants.TYPE_ORDER:
                        total += info.order.downPayment * 100;
                        break;
                    case AppConstants.TYPE_PAY_FOR_OTHER:
                        if (onlySuccess) {
                            total += info.treatInfo.useMoney;
                            noUseTreatMoney += info.treatInfo.amount - info.treatInfo.useMoney;
                        } else {
                            total += info.treatInfo.amount;
                        }
                        break;
                }
            }
        }
        if (!onlySuccess) {
            if (trade.getVerificationMoney() != total) {
                //用户重新选择了优惠金额
                trade.setWillDiscountMoney(total);
            }
            trade.setVerificationMoney(total);
            trade.setVerificationCount(selectCount);
            XLogger.i("calculateVerificationValue:" + total);
        } else {
            trade.setVerificationSuccessfulMoney(total);
            trade.setVerificationNoUseTreatMoney(noUseTreatMoney);
            XLogger.i("calculateVerificationSuccessValue:" + total);
        }
    }

    private void calculateTreatUseMoney(int originMoney) {
        int total = 0;
        //首先计算出所有优惠券提供的总优惠金额
        for (VerificationItem info : mTradeManager.getCurrentTrade().getCouponList()) {
            if (info.selected && info.success) {
                switch (info.type) {
                    case AppConstants.TYPE_COUPON:
                        total += info.couponInfo.getReallyCouponMoney();
                        break;
                    case AppConstants.TYPE_ORDER:
                        total += info.order.downPayment;
                        break;
                }
            }
        }

        total *= 100;

        int leftMoney = originMoney - total;

        //然后计算朋友请客可以抵扣的部分
        for (VerificationItem info : mTradeManager.getCurrentTrade().getCouponList()) {
            if (info.selected && info.type.equals(AppConstants.TYPE_PAY_FOR_OTHER)) {
                //计算实际应该核销的金额
                if (leftMoney > 0) {
                    if (leftMoney <= info.treatInfo.amount) {
                        info.treatInfo.useMoney = leftMoney;
                        break;
                    } else {
                        info.treatInfo.useMoney = info.treatInfo.amount;
                        leftMoney -= info.treatInfo.useMoney;
                    }
                }
            }
        }
    }

    public String getCouponInfo() {
        int size = 0;
        for (VerificationItem info : mTradeManager.getCurrentTrade().getCouponList()) {
            if (info.selected) {
                size++;
            }
        }
        if (size > 0) {
            return "优惠券X" + size;
        } else {
            return "";
        }
    }

    public boolean hasVerification() {
        for (VerificationItem item : mTradeManager.getCurrentTrade().getCouponList()) {
            if (item.selected && item.success) {
                return true;
            }
        }
        return false;
    }

    public static String formatCouponList(List<VerificationItem> couponList) {
        StringBuilder result = new StringBuilder();
        for (VerificationItem item : couponList) {
            if (item.selected) {
                switch (item.type) {
                    case AppConstants.TYPE_COUPON:
                        result.append(item.couponInfo.couponNo + "|");
                        break;
                    case AppConstants.TYPE_ORDER:
                        result.append(item.order.orderNo + "|");
                        break;
                    case AppConstants.TYPE_PAY_FOR_OTHER:
                        result.append(item.treatInfo.authorizeCode + "|");
                        break;
                    default:
                        result.append("0000|");
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

    public static String formatCouponResult(List<VerificationItem> couponList) {
        StringBuilder result = new StringBuilder();
        for (VerificationItem item : couponList) {
            if (item.selected) {
                if (item.errorMsg.equals(RESULT_SUCCESS)) {
                    result.append(RESULT_SUCCESS + "|");
                } else {
                    result.append(RESULT_FAILED + "|");
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
}
